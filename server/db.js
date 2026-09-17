import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import Database from 'better-sqlite3';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const dataDir = path.join(__dirname, '..', 'data');
fs.mkdirSync(dataDir, { recursive: true });

const db = new Database(path.join(dataDir, 'bizethiopia.db'));
db.pragma('journal_mode = WAL');

db.exec(`
  CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    min_stock INTEGER NOT NULL DEFAULT 5,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    phone TEXT,
    debt REAL NOT NULL DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS sales (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT NOT NULL,
    customer TEXT NOT NULL,
    product_name TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    total REAL NOT NULL,
    payment TEXT NOT NULL DEFAULT 'Cash',
    product_id INTEGER,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT NOT NULL,
    category TEXT NOT NULL,
    amount REAL NOT NULL,
    note TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer TEXT NOT NULL,
    items TEXT NOT NULL,
    total REAL NOT NULL,
    status TEXT NOT NULL DEFAULT 'Pending',
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  );
`);

const today = () => new Date().toISOString().slice(0, 10);

const seedIfEmpty = () => {
  const productCount = db.prepare('SELECT COUNT(*) as count FROM products').get().count;

  if (productCount === 0) {
    db.prepare(`
      INSERT INTO products (name, price, stock, min_stock)
      VALUES
        ('Coffee', 120, 15, 5),
        ('Bread', 60, 30, 10),
        ('Milk', 90, 20, 8),
        ('Rice', 250, 12, 5)
    `).run();

    db.prepare(`
      INSERT INTO customers (name, phone, debt)
      VALUES
        ('Abebe Kebede', '0911000000', 450),
        ('Hanna Store', '0922000000', 0)
    `).run();

    db.prepare(`
      INSERT INTO sales (date, customer, product_name, quantity, total, payment, product_id)
      VALUES
        (?, 'Walk-in', 'Coffee', 2, 240, 'Cash', 1),
        (?, 'Abebe', 'Bread', 5, 300, 'Telebirr', 2)
    `).run(today(), today());

    db.prepare(`
      INSERT INTO expenses (date, category, amount, note)
      VALUES
        (?, 'Transport', 100, 'Delivery'),
        (?, 'Utilities', 200, 'Power bill')
    `).run(today(), today());

    db.prepare(`
      INSERT INTO orders (customer, items, total, status)
      VALUES
        ('Hanna Store', '10 × Milk', 900, 'Pending'),
        ('Abebe Kebede', '4 × Rice', 1000, 'Confirmed')
    `).run();
  }
};

seedIfEmpty();

export function listProducts() {
  return db.prepare('SELECT * FROM products ORDER BY id DESC').all();
}

export function listCustomers() {
  return db.prepare('SELECT * FROM customers ORDER BY id DESC').all();
}

export function listSales() {
  return db.prepare('SELECT * FROM sales ORDER BY id DESC').all();
}

export function listExpenses() {
  return db.prepare('SELECT * FROM expenses ORDER BY id DESC').all();
}

export function listOrders() {
  return db.prepare('SELECT * FROM orders ORDER BY id DESC').all();
}

export function getDashboard() {
  const salesToday = db.prepare('SELECT COALESCE(SUM(total), 0) as total, COUNT(*) as count FROM sales WHERE date = ?').get(today());
  const expensesToday = db.prepare('SELECT COALESCE(SUM(amount), 0) as total FROM expenses WHERE date = ?').get(today());
  const revenueAll = db.prepare('SELECT COALESCE(SUM(total), 0) as total FROM sales').get();
  const expenseAll = db.prepare('SELECT COALESCE(SUM(amount), 0) as total FROM expenses').get();
  const debtTotal = db.prepare('SELECT COALESCE(SUM(debt), 0) as total FROM customers').get();

  return {
    metrics: {
      salesToday: Number(salesToday.total),
      expensesToday: Number(expensesToday.total),
      profitToday: Number(salesToday.total) - Number(expensesToday.total),
      txnCount: salesToday.count,
      revenueAll: Number(revenueAll.total),
      expenseAll: Number(expenseAll.total),
      profitAll: Number(revenueAll.total) - Number(expenseAll.total),
      debtTotal: Number(debtTotal.total)
    },
    products: listProducts(),
    customers: listCustomers(),
    sales: listSales(),
    expenses: listExpenses(),
    orders: listOrders()
  };
}

export function createProduct({ name, price, stock, min_stock }) {
  const result = db.prepare(`
    INSERT INTO products (name, price, stock, min_stock)
    VALUES (?, ?, ?, ?)
  `).run(name, Number(price), Number(stock), Number(min_stock || 5));

  return result.lastInsertRowid;
}

export function createCustomer({ name, phone }) {
  const existing = db.prepare('SELECT * FROM customers WHERE name = ?').get(name);

  if (existing) {
    return existing.id;
  }

  const result = db.prepare(`
    INSERT INTO customers (name, phone, debt)
    VALUES (?, ?, 0)
  `).run(name, phone || '');

  return result.lastInsertRowid;
}

export function createSale({ product_id, quantity, discount, payment, customer }) {
  const product = db.prepare('SELECT * FROM products WHERE id = ?').get(product_id);

  if (!product) {
    throw new Error('Product not found');
  }

  if (Number(quantity) <= 0) {
    throw new Error('Quantity must be positive');
  }

  if (Number(quantity) > product.stock) {
    throw new Error('Not enough stock');
  }

  const total = Math.max(0, product.price * Number(quantity) - Number(discount || 0));

  db.transaction(() => {
    db.prepare('UPDATE products SET stock = stock - ? WHERE id = ?').run(Number(quantity), product_id);

    db.prepare(`
      INSERT INTO sales (date, customer, product_name, quantity, total, payment, product_id)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `).run(
      today(),
      customer || 'Walk-in',
      product.name,
      Number(quantity),
      total,
      payment || 'Cash',
      product_id
    );

    if ((payment || 'Cash') === 'Credit') {
      const existing = db.prepare('SELECT * FROM customers WHERE name = ?').get(customer || 'Walk-in');

      if (existing) {
        db.prepare('UPDATE customers SET debt = debt + ? WHERE id = ?').run(total, existing.id);
      } else {
        db.prepare('INSERT INTO customers (name, phone, debt) VALUES (?, ?, ?)').run(customer || 'Walk-in', '', total);
      }
    }
  })();

  return true;
}

export function createExpense({ category, amount, note }) {
  const result = db.prepare(`
    INSERT INTO expenses (date, category, amount, note)
    VALUES (?, ?, ?, ?)
  `).run(today(), category, Number(amount), note || '');

  return result.lastInsertRowid;
}

export function createOrder({ customer, items, total, status }) {
  const result = db.prepare(`
    INSERT INTO orders (customer, items, total, status)
    VALUES (?, ?, ?, ?)
  `).run(customer, items, Number(total), status || 'Pending');

  return result.lastInsertRowid;
}
