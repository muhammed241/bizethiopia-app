import express from 'express';
import cors from 'cors';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import {
  getDashboard,
  createProduct,
  createCustomer,
  createSale,
  createExpense,
  createOrder
} from './db.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const app = express();
const PORT = Number(process.env.PORT || 3001);

app.use(cors());
app.use(express.json({ limit: '1mb' }));

app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', service: 'bizethiopia-api', timestamp: new Date().toISOString() });
});

app.get('/api/dashboard', (req, res) => {
  try {
    res.json(getDashboard());
  } catch (error) {
    res.status(500).json({ error: 'Unable to load dashboard' });
  }
});

app.post('/api/products', (req, res) => {
  try {
    const { name, price, stock, min_stock } = req.body;
    if (!String(name || '').trim()) return res.status(400).json({ error: 'Name is required' });
    if (!Number.isFinite(Number(price)) || Number(price) < 0) return res.status(400).json({ error: 'Price must be a non-negative number' });
    if (!Number.isInteger(Number(stock)) || Number(stock) < 0) return res.status(400).json({ error: 'Stock must be a non-negative whole number' });
    const id = createProduct({ name: String(name).trim(), price, stock, min_stock });
    res.status(201).json({ id, message: 'Product created' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/customers', (req, res) => {
  try {
    const { name, phone } = req.body;
    if (!String(name || '').trim()) return res.status(400).json({ error: 'Name is required' });
    const id = createCustomer({ name: String(name).trim(), phone: String(phone || '').trim() });
    res.status(201).json({ id, message: 'Customer saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/sales', (req, res) => {
  try {
    const { product_id, quantity, discount, payment, customer } = req.body;
    if (!Number.isInteger(Number(product_id)) || !Number.isInteger(Number(quantity))) {
      return res.status(400).json({ error: 'Product and quantity are required' });
    }
    createSale({ product_id, quantity, discount, payment, customer });
    res.status(201).json({ message: 'Sale saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/expenses', (req, res) => {
  try {
    const { category, amount, note } = req.body;
    if (!String(category || '').trim()) return res.status(400).json({ error: 'Category is required' });
    if (!Number.isFinite(Number(amount)) || Number(amount) <= 0) return res.status(400).json({ error: 'Amount must be greater than zero' });
    const id = createExpense({ category: String(category).trim(), amount, note });
    res.status(201).json({ id, message: 'Expense saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/orders', (req, res) => {
  try {
    const { customer, items, total, status } = req.body;
    if (!String(customer || '').trim() || !String(items || '').trim()) return res.status(400).json({ error: 'Customer and items are required' });
    if (!Number.isFinite(Number(total)) || Number(total) < 0) return res.status(400).json({ error: 'Total must be a non-negative number' });
    const id = createOrder({ customer: String(customer).trim(), items: String(items).trim(), total, status });
    res.status(201).json({ id, message: 'Order saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

const distPath = path.join(__dirname, '..', 'dist');
app.use(express.static(distPath));
app.get('*', (req, res, next) => {
  if (req.path.startsWith('/api/')) return next();
  res.sendFile(path.join(distPath, 'index.html'), (error) => error && next());
});

app.use((error, req, res, next) => {
  console.error(error);
  res.status(500).json({ error: 'Internal server error' });
});

app.listen(PORT, () => {
  console.log(`BizEthiopia API running on http://localhost:${PORT}`);
});
