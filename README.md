# BizEthiopia App

A full-stack MVP business management app for a small business in Ethiopia.

## Features
- Sales dashboard
- Product inventory tracking
- Low-stock alerts
- Customer debt tracking
- Expense tracking
- WhatsApp order list
- SQLite persistence
- React + Express architecture
- Production Docker image with persistent database volume
- API health check at `/api/health`

## Run locally

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

## Run production build locally

```bash
npm install
npm run build
npm start
```

Open `http://localhost:3001`.

## Run with Docker

```bash
docker compose up --build
```

Open `http://localhost:3001`.

SQLite data is persisted in the `bizethiopia-data` Docker volume.

## API

- `GET /api/health`
- `GET /api/dashboard`
- `POST /api/products`
- `POST /api/sales`
- `POST /api/expenses`
- `POST /api/customers`
- `POST /api/orders`

## Notes
This is a working MVP for local demo and development. Authentication, role-based access, payment-provider integration, WhatsApp webhooks, backups, and PostgreSQL migration are still required before production SaaS deployment.
