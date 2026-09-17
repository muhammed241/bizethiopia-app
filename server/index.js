import express from 'express';
import cors from 'cors';
import {
  getDashboard,
  createProduct,
  createCustomer,
  createSale,
  createExpense,
  createOrder
} from './db.js';

const app = express();
const PORT = 3001;

app.use(cors());
app.use(express.json());

app.get('/api/dashboard', (req, res) => {
  res.json(getDashboard());
});

app.post('/api/products', (req, res) => {
  try {
    const { name, price, stock, min_stock } = req.body;

    if (!name) {
      return res.status(400).json({ error: 'Name is required' });
    }

    const id = createProduct({ name, price, stock, min_stock });
    res.json({ id, message: 'Product created' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/customers', (req, res) => {
  try {
    const { name, phone } = req.body;

    if (!name) {
      return res.status(400).json({ error: 'Name is required' });
    }

    const id = createCustomer({ name, phone });
    res.json({ id, message: 'Customer saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/sales', (req, res) => {
  try {
    const { product_id, quantity, discount, payment, customer } = req.body;
    createSale({ product_id, quantity, discount, payment, customer });
    res.json({ message: 'Sale saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/expenses', (req, res) => {
  try {
    const { category, amount, note } = req.body;

    if (!category || !amount) {
      return res.status(400).json({ error: 'Category and amount are required' });
    }

    const id = createExpense({ category, amount, note });
    res.json({ id, message: 'Expense saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.post('/api/orders', (req, res) => {
  try {
    const { customer, items, total, status } = req.body;
    const id = createOrder({ customer, items, total, status });
    res.json({ id, message: 'Order saved' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

app.listen(PORT, () => {
  console.log(`BizEthiopia API running on http://localhost:${PORT}`);
});
