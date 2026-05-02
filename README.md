# Distributed Event-Driven Inventory Management System (Spring Boot + Kafka + AWS)

A microservices-based system that automatically generates purchase orders when inventory falls below a configured threshold, using an event-driven architecture with Apache Kafka.

---
## Features

- Event-driven microservices architecture
- Automatic purchase order generation (LOW_STOCK trigger)
- Centralized JWT authentication (auth-service)
- Kafka-based async communication with retry + DLT
- Idempotent consumers to prevent duplicate processing
- Keyset pagination for high-performance querying
- Dockerized deployment with AWS EC2 hosting

---
## Architecture

```
┌──────────────────┐        ┌─────────────────┐
│  inventory-service│──────▶│     Kafka        │
│  (port 8080)     │        │  stock-event     │
└──────────────────┘        └────────┬────────┘
                                     │
┌──────────────────┐                 ▼
│  auth-service    │        ┌─────────────────────┐
│  (port 8082)     │        │ procurement-service  │
└──────────────────┘        │ (port 8081)          │
                             │ auto-creates PO      │
┌──────────────────┐        └─────────────────────┘
│   PostgreSQL     │
│   (port 5432)    │
└──────────────────┘
```

**Flow:** When stock drops below `minStockThreshold`, inventory-service publishes a `LOW_STOCK` event to Kafka. Procurement-service consumes it and automatically creates a Purchase Order.

---
### Authentication Flow

- `auth-service` handles user authentication and JWT token generation.
- Other services (inventory, procurement) validate JWT tokens using `common-lib`.
- Ensures centralized authentication with decentralized authorization.

---
### End-to-End Flow

1. User decreases stock via inventory-service
2. If below threshold → `LOW_STOCK` event published to Kafka
3. procurement-service consumes event
4. Purchase Order is created automatically
5. On approval + receive → `RECEIVE_GOODS` event published
6. inventory-service consumes event → stock increased

---
## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.1 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Auth | Centralized JWT authentication |
| Messaging | Apache Kafka (KRaft mode) |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA + Hibernate 6 |
| Containerization | Docker + Docker Compose |
| Cloud | AWS EC2 (free tier) |
| Build | Maven (multi-module) |


---

## Project Structure

```
inventory-auto-replenishment-system/
├── common-lib/              # Shared: JWT utils, StockEvent, LoginRequest
├── auth-service/            # Issues JWT tokens (port 8082)
├── inventory-service/       # Product + stock management (port 8080)
├── procurement-service/     # Purchase order lifecycle (port 8081)
├── docker-compose.yml       # Full stack orchestration
├── .env.example             # Environment variable template
└── build.sh                 # Build all JARs in correct order
```

---

## Live Demo (AWS EC2)

Base URL: `http://54.253.24.96`

| Service | URL |
|---|---|
| Login (get JWT) | `POST http://54.253.24.96:8082/api/auth/login` |
| Products | `GET http://54.253.24.96:8080/api/products` |
| Inventory | `POST http://54.253.24.96:8080/api/inventory/{productId}/stock/increase` |
| Purchase Orders | `GET http://54.253.24.96:8081/api/purchase-orders` |

---

## Running Locally

### Prerequisites
- Java 17
- Maven 3.9+
- Docker Desktop

### Step 1 — Clone and configure

```bash
git clone https://github.com/your-username/inventory-auto-replenishment-system.git
cd inventory-auto-replenishment-system
cp .env.example .env
# Edit .env with your values
```

### Step 2 — Start infrastructure only

```bash
docker compose up postgres kafka -d
```

### Step 3 — Build all JARs

```bash
# On Linux/Mac
chmod +x build.sh && ./build.sh

# On Windows
mvn -pl common-lib install -DskipTests
mvn -pl auth-service,inventory-service,procurement-service package -DskipTests
```

### Step 4 — Run services

Run each from IntelliJ or via Maven:
```bash
# Terminal 1
cd auth-service && mvn spring-boot:run

# Terminal 2
cd inventory-service && mvn spring-boot:run

# Terminal 3
cd procurement-service && mvn spring-boot:run
```

Services will connect to Docker-hosted Postgres and Kafka via `localhost`.

---

## Running Full Stack via Docker (Production Mode)

```bash
# Build JARs first
./build.sh

# Start everything
docker compose up -d --build

This starts:
- PostgreSQL database
- Kafka (KRaft mode)
- inventory-service
- procurement-service
- auth-service

# Check status
docker compose ps

# View logs
docker compose logs -f inventory-service
```

---

## API Quick Reference

### 1. Login
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```
Returns: `{"token": "eyJ..."}`

Use the token as `Authorization: Bearer <token>` in all subsequent requests.

### 2. Create a Product (ADMIN)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Widget A",
    "sku": "SKU-001",
    "description": "Test product",
    "price": 99.99,
    "minStockThreshold": 10,
    "maxStockThreshold": 100
  }'
```

### 3. Increase Stock (ADMIN)
```bash
curl -X POST "http://localhost:8080/api/inventory/{productId}/stock/increase?quantity=20" \
  -H "Authorization: Bearer <token>"
```

### 4. Decrease Stock — triggers LOW_STOCK event if below threshold (ADMIN)
```bash
curl -X POST "http://localhost:8080/api/inventory/{productId}/stock/decrease?quantity=15" \
  -H "Authorization: Bearer <token>"
```
Response includes `"lowStock": true` when threshold is breached.

### 5. Check Auto-Generated Purchase Orders
```bash
curl -X GET http://localhost:8081/api/purchase-orders \
  -H "Authorization: Bearer <token>"
```

### 6. Approve and Receive a Purchase Order (ADMIN)
```bash
# Approve
curl -X POST http://localhost:8081/api/purchase-orders/{id}/approve \
  -H "Authorization: Bearer <token>"

# Receive (triggers stock replenishment back to inventory-service via Kafka)
curl -X POST http://localhost:8081/api/purchase-orders/{id}/receive \
  -H "Authorization: Bearer <token>"
```

---

## Default Credentials

| Username | Password | Role |
|---|---|---|
| admin | password | ADMIN |
| user | password | USER |

---

## PostgreSQL Access (Docker)

```bash
# Connect
docker exec -it inventory-postgres psql -U inventory_user -d inventory_db

# Useful queries
\dt                          -- list tables
SELECT * FROM products;
SELECT * FROM inventories;
SELECT * FROM purchase_orders;
SELECT * FROM event_logs;
\q                           -- exit
```

---

## Environment Variables

Copy `.env.example` to `.env` and fill in values:

```env
POSTGRES_DB=inventory_db
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret_min_32_characters
JWT_EXPIRATION=3600000
```

---

## What to Ignore / Not Commit

| File/Folder | Reason |
|---|---|
| `.env` | Contains real passwords — never commit |
| `**/target/` | Compiled JARs — generated by build |
| `*.pem` | AWS SSH key — never commit |
| `.idea/` | IntelliJ project files |

---

## Key Design Decisions

- **Idempotent Event Processing:** Each Kafka event carries a unique `eventId`. A ProcessedEvent table ensures duplicate messages (due to retries or rebalancing) are ignored, guaranteeing exactly-once logical processing
- **Event Reliability with Retry & Dead Letter Topic:** Kafka consumers use retry with backoff for transient failures. Messages that fail after retries are routed to a Dead Letter Topic `stock-event-dlt` for later inspection and recovery.
- **Efficient Pagination Strategy:** Keyset (cursor-based) pagination is implemented for `/api/products/keyset` to handle large datasets efficiently and avoid performance issues of offset-based pagination.
- **Optimized Persistence using Dirty Checking:** Inventory updates rely on Hibernate’s dirty checking within transactional boundaries `@Transactional` - no explicit `save()` needed for stock updates, reducing unnecessary database writes and improving performance.
- **Shared Library:** `common-lib` module holds common DTOs, JWT utilities, and event models are centralized to ensure consistency across services and avoid duplication..
