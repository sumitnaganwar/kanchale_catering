# Kanchale Catering

Spring Boot REST API and React customer/admin website for Kanchale Catering.

## Requirements

- Java 17
- Docker Desktop (or PostgreSQL 16 installed locally)
- Node.js 18+

## Start PostgreSQL

From this directory:

```sh
docker compose up -d postgres
```

The development database is `kanchale_catering` on `localhost:5432` (user/password `postgres`). These are local Docker development credentials; override them for any shared or deployed database. JPA creates/updates tables on startup. The menu is initialized when the database is empty, and new starter menu items are added if missing.

## Start the backend

Set the admin credentials and a strong JWT secret in your shell before starting the app. The admin account is not created unless both `ADMIN_EMAIL` and `ADMIN_PASSWORD` are set. Do not commit these values.

Windows PowerShell:

```powershell
$env:ADMIN_EMAIL = "admin@example.com"
$env:ADMIN_PASSWORD = "replace-with-a-strong-password"
$env:JWT_SECRET = "replace-with-at-least-32-random-characters"
$env:DB_PASSWORD = "postgres"
./mvnw.cmd spring-boot:run
```

For macOS/Linux, export the same variables and run `./mvnw spring-boot:run`. `DB_URL` and `DB_USERNAME` default to the local Docker database; set `DB_PASSWORD` to match its configured password.

API runs on `http://localhost:8080`.

## Start the frontend

```sh
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The Vite development server proxies `/api` requests to the backend. The admin sign-in is at `http://localhost:5173/admin`.

## Available APIs

- `GET /api/foods` and `GET /api/categories` — public menu endpoints
- `GET /api/thalis`, `GET /api/thalis/{id}` — public thali cards and included foods
- `GET /api/admin/thalis`, `POST /api/admin/thalis`, `PUT /api/admin/thalis/{id}`, `DELETE /api/admin/thalis/{id}` — protected thali management
- `POST /api/orders` — validate and persist customer orders
- `GET /api/orders/track?orderId=...&phone=...` — private order lookup
- `POST /api/auth/login` — admin JWT sign-in
- `GET /api/admin/dashboard`, `GET /api/admin/orders`, `PATCH /api/admin/orders/{orderId}/status`
- `GET /api/admin/foods`, `POST /api/admin/foods`, `PUT /api/admin/foods/{id}`, `DELETE /api/admin/foods/{id}`
- `POST /api/admin/foods/images`, `DELETE /api/admin/foods/images?path=...` — protected food photo upload/removal; files are served from `/uploads/**`
- `GET /api/admin/customers`
- `POST /api/catering-bookings` — submit a bulk catering enquiry (minimum 200 plates)
- `GET /api/admin/catering-bookings`, `GET /api/admin/catering-bookings/{id}`
- `PATCH /api/admin/catering-bookings/{id}/status`, `PATCH /api/admin/catering-bookings/{id}/quotation`

All `/api/admin/**` endpoints require `Authorization: Bearer <token>` and are restricted to the ADMIN role. Customer orders are priced from current database prices; client-submitted totals are ignored.

## Before production

Set strong values for `ADMIN_EMAIL`, `ADMIN_PASSWORD`, and `JWT_SECRET`; configure a managed PostgreSQL database; replace `ddl-auto=update` with versioned migrations; configure the production frontend origin for CORS; and supply real phone/WhatsApp contact details. Uploaded food images are stored in the local `uploads/` directory; use durable object storage such as Cloudinary or S3 for production.
