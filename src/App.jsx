import { useEffect, useState } from 'react';

const money = (value) =>
  new Intl.NumberFormat('en-US', {
    maximumFractionDigits: 0
  }).format(Number(value || 0));

async function fetchJson(url, options) {
  const response = await fetch(url, options);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || 'Request failed');
  }

  return response.json();
}

export default function App() {
  const [data, setData] = useState({
    metrics: {},
    products: [],
    sales: [],
    expenses: [],
    customers: [],
    orders: []
  });

  const [productForm, setProductForm] = useState({
    name: '',
    price: '',
    stock: '',
    min_stock: '5'
  });

  const [saleForm, setSaleForm] = useState({
    product_id: '',
    quantity: '1',
    discount: '0',
    payment: 'Cash',
    customer: 'Walk-in'
  });

  const [expenseForm, setExpenseForm] = useState({
    category: 'Stock purchases',
    amount: '',
    note: ''
  });

  const [customerForm, setCustomerForm] = useState({
    name: '',
    phone: ''
  });

  const loadData = async () => {
    try {
      const result = await fetchJson('/api/dashboard');
      setData(result);

      if (result.products.length > 0 && !saleForm.product_id) {
        setSaleForm((prev) => ({ ...prev, product_id: String(result.products[0].id) }));
      }
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const addProduct = async (e) => {
    e.preventDefault();

    await fetchJson('/api/products', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: productForm.name,
        price: Number(productForm.price),
        stock: Number(productForm.stock),
        min_stock: Number(productForm.min_stock)
      })
    });

    setProductForm({ name: '', price: '', stock: '', min_stock: '5' });
    loadData();
  };

  const addSale = async (e) => {
    e.preventDefault();

    await fetchJson('/api/sales', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        product_id: Number(saleForm.product_id),
        quantity: Number(saleForm.quantity),
        discount: Number(saleForm.discount),
        payment: saleForm.payment,
        customer: saleForm.customer
      })
    });

    setSaleForm({
      product_id: data.products[0]?.id ? String(data.products[0].id) : '',
      quantity: '1',
      discount: '0',
      payment: 'Cash',
      customer: 'Walk-in'
    });

    loadData();
  };

  const addExpense = async (e) => {
    e.preventDefault();

    await fetchJson('/api/expenses', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        category: expenseForm.category,
        amount: Number(expenseForm.amount),
        note: expenseForm.note
      })
    });

    setExpenseForm({ category: 'Stock purchases', amount: '', note: '' });
    loadData();
  };

  const addCustomer = async (e) => {
    e.preventDefault();

    await fetchJson('/api/customers', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: customerForm.name,
        phone: customerForm.phone
      })
    });

    setCustomerForm({ name: '', phone: '' });
    loadData();
  };

  const metrics = data.metrics || {};
  const latestSales = data.sales || [];
  const lowStock = (data.products || []).filter((p) => p.stock <= p.min_stock);

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <div className="brand-mark">B</div>
          <div>
            <strong>BizEthiopia</strong>
            <small>Business manager</small>
          </div>
        </div>
        <div className="date-pill">{new Date().toLocaleDateString()}</div>
      </header>

      <main className="container">
        <section className="hero">
          <h1>Good evening 👋</h1>
          <p>Track sales, inventory, customers, and profit in one place.</p>
        </section>

        <section className="cards">
          <div className="card">
            <span>Sales today</span>
            <h2>{money(metrics.salesToday)} ETB</h2>
            <small>Live</small>
          </div>
          <div className="card">
            <span>Expenses</span>
            <h2>{money(metrics.expensesToday)} ETB</h2>
            <small>Today</small>
          </div>
          <div className="card">
            <span>Profit</span>
            <h2>{money(metrics.profitToday)} ETB</h2>
            <small>Operating</small>
          </div>
          <div className="card">
            <span>Transactions</span>
            <h2>{metrics.txnCount || 0}</h2>
            <small>Daily</small>
          </div>
        </section>

        <section className="panel">
          <div className="panel-header">
            <h3>Quick actions</h3>
          </div>

          <div className="grid-two">
            <form className="mini-form" onSubmit={addSale}>
              <h4>New sale</h4>
              <select
                value={saleForm.product_id}
                onChange={(e) => setSaleForm({ ...saleForm, product_id: e.target.value })}
              >
                <option value="">Select product</option>
                {(data.products || []).map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name} — {money(p.price)} ETB
                  </option>
                ))}
              </select>

              <input
                type="number"
                min="1"
                placeholder="Quantity"
                value={saleForm.quantity}
                onChange={(e) => setSaleForm({ ...saleForm, quantity: e.target.value })}
              />
              <input
                type="number"
                min="0"
                placeholder="Discount"
                value={saleForm.discount}
                onChange={(e) => setSaleForm({ ...saleForm, discount: e.target.value })}
              />
              <input
                type="text"
                placeholder="Customer"
                value={saleForm.customer}
                onChange={(e) => setSaleForm({ ...saleForm, customer: e.target.value })}
              />
              <select
                value={saleForm.payment}
                onChange={(e) => setSaleForm({ ...saleForm, payment: e.target.value })}
              >
                <option>Cash</option>
                <option>Telebirr</option>
                <option>CBE Birr</option>
                <option>Credit</option>
              </select>
              <button type="submit">Save sale</button>
            </form>

            <form className="mini-form" onSubmit={addProduct}>
              <h4>Add product</h4>
              <input
                type="text"
                placeholder="Product name"
                value={productForm.name}
                onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
              />
              <input
                type="number"
                placeholder="Price"
                value={productForm.price}
                onChange={(e) => setProductForm({ ...productForm, price: e.target.value })}
              />
              <input
                type="number"
                placeholder="Opening stock"
                value={productForm.stock}
                onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })}
              />
              <input
                type="number"
                placeholder="Min stock alert"
                value={productForm.min_stock}
                onChange={(e) => setProductForm({ ...productForm, min_stock: e.target.value })}
              />
              <button type="submit">Add product</button>
            </form>
          </div>
        </section>

        {lowStock.length > 0 && (
          <section className="alert-box warning">
            <strong>Low stock alert</strong>
            <ul>
              {lowStock.map((item) => (
                <li key={item.id}>
                  {item.name}: {item.stock} left, reorder soon.
                </li>
              ))}
            </ul>
          </section>
        )}

        <section className="panel">
          <div className="panel-header">
            <h3>Recent sales</h3>
          </div>

          <div className="list">
            {(latestSales || []).slice(0, 5).map((sale) => (
              <div className="row" key={sale.id}>
                <div>
                  <strong>{sale.product_name}</strong>
                  <small>
                    {sale.customer} • {sale.payment}
                  </small>
                </div>
                <strong>{money(sale.total)} ETB</strong>
              </div>
            ))}
          </div>
        </section>

        <section className="grid-two">
          <div className="panel">
            <div className="panel-header">
              <h3>Products</h3>
            </div>
            <div className="list">
              {(data.products || []).map((product) => (
                <div className="row" key={product.id}>
                  <div>
                    <strong>{product.name}</strong>
                    <small>{money(product.price)} ETB</small>
                  </div>
                  <div className="status-group">
                    <strong>{product.stock}</strong>
                    <span className={product.stock <= product.min_stock ? 'tag danger' : 'tag ok'}>
                      {product.stock <= product.min_stock ? 'Low' : 'OK'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="panel">
            <div className="panel-header">
              <h3>Customers</h3>
            </div>
            <div className="list">
              {(data.customers || []).map((customer) => (
                <div className="row" key={customer.id}>
                  <div>
                    <strong>{customer.name}</strong>
                    <small>{customer.phone || 'No phone'}</small>
                  </div>
                  <div className="status-group">
                    <strong>{money(customer.debt)} ETB</strong>
                    <span className={customer.debt > 0 ? 'tag danger' : 'tag ok'}>
                      {customer.debt > 0 ? 'Debt' : 'Clear'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="grid-two">
          <form className="mini-form" onSubmit={addExpense}>
            <h4>Add expense</h4>
            <select
              value={expenseForm.category}
              onChange={(e) => setExpenseForm({ ...expenseForm, category: e.target.value })}
            >
              <option>Stock purchases</option>
              <option>Rent</option>
              <option>Utilities</option>
              <option>Transport</option>
              <option>Staff wages</option>
              <option>Other</option>
            </select>
            <input
              type="number"
              min="0"
              placeholder="Amount"
              value={expenseForm.amount}
              onChange={(e) => setExpenseForm({ ...expenseForm, amount: e.target.value })}
            />
            <input
              type="text"
              placeholder="Note"
              value={expenseForm.note}
              onChange={(e) => setExpenseForm({ ...expenseForm, note: e.target.value })}
            />
            <button type="submit">Save expense</button>
          </form>

          <form className="mini-form" onSubmit={addCustomer}>
            <h4>Add customer</h4>
            <input
              type="text"
              placeholder="Customer name"
              value={customerForm.name}
              onChange={(e) => setCustomerForm({ ...customerForm, name: e.target.value })}
            />
            <input
              type="text"
              placeholder="Phone"
              value={customerForm.phone}
              onChange={(e) => setCustomerForm({ ...customerForm, phone: e.target.value })}
            />
            <button type="submit">Save customer</button>
          </form>
        </section>

        <section className="panel">
          <div className="panel-header">
            <h3>Expenses</h3>
          </div>
          <div className="list">
            {(data.expenses || []).map((expense) => (
              <div className="row" key={expense.id}>
                <div>
                  <strong>{expense.category}</strong>
                  <small>{expense.note || 'No note'}</small>
                </div>
                <strong>{money(expense.amount)} ETB</strong>
              </div>
            ))}
          </div>
        </section>

        <section className="panel">
          <div className="panel-header">
            <h3>WhatsApp orders</h3>
          </div>
          <div className="list">
            {(data.orders || []).map((order) => (
              <div className="row" key={order.id}>
                <div>
                  <strong>{order.customer}</strong>
                  <small>{order.items}</small>
                </div>
                <div className="status-group">
                  <strong>{money(order.total)} ETB</strong>
                  <span className="tag">{order.status}</span>
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}
