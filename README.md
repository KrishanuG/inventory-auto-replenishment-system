# Event-Driven Inventory Auto-Replenishment System

Microservices-based system that automatically generates purchase orders when inventory falls below threshold.

## Architecture

- `inventory-service`
- `order-service`
- `procurement-service`

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Security (JWT)
- Apache Kafka
- PostgreSQL
- Docker
- AWS

---

## Local PostgreSQL Access (Docker)

PostgreSQL runs inside Docker container defined in `docker-compose.yml`.

###  Start Database
```bash
docker compose up -d
```

### Stop Database (Keep Data)
```bash
docker compose down
```

###  Stop & Remove Data (Danger – Deletes all records)
```bash
docker compose down -v
```

###  Connect to PostgreSQL via Docker CLI
```bash
docker exec -it inventory-postgres psql -U inventory_user -d inventory_db
```

**Explanation:**
- `inventory-postgres` → container name
- `-U inventory_user` → username
- `-d inventory_db` → database name

---

## Useful Commands Inside psql

List tables:
```sql
\dt
```

View inventory records:
```sql
SELECT * FROM inventory;
```

Exit:
```sql
\q
```