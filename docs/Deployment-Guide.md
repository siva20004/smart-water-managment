# Container Deployment Guide (Docker & Production)

This guide outlines containerized deployment procedures using Docker and Docker Compose.

---

## 1. Container Architecture

The system deploys three isolated, networked containers:

```
[ Host Port 5173 / 80 ] ──▶ [ smartwater-frontend (Nginx Alpine) ]
                                   │
                                   │ Proxy / API requests
                                   ▼
[ Host Port 8080 ]      ──▶ [ smartwater-backend (Temurin JRE 21) ]
                                   │
                                   │ JDBC Connection Pool (HikariCP)
                                   ▼
[ Host Port 5432 ]      ──▶ [ smartwater-postgres (PostgreSQL 16) ]
```

---

## 2. Docker Compose Deployment

### 2.1 Start Full Stack
```bash
docker compose -f docker/docker-compose.yml up -d --build
```

### 2.2 Verify Container Health
```bash
docker compose -f docker/docker-compose.yml ps
```

Expected output:
```text
NAME                  IMAGE                  COMMAND                  SERVICE      STATUS
smartwater-postgres   postgres:16-alpine     "docker-entrypoint.s…"   postgres     Up (healthy)
smartwater-backend    veloia-backend:latest  "java -XX:+UseContai…"   backend      Up
smartwater-frontend   veloia-frontend:latest "/docker-entrypoint.…"   frontend     Up
```

### 2.3 Access Application Services
- **Web Frontend**: `http://localhost:5173`
- **Backend API**: `http://localhost:8080/api`
- **Swagger Documentation**: `http://localhost:8080/swagger-ui.html`
- **Spring Actuator Health**: `http://localhost:8080/actuator/health`

---

## 3. Environment Variables Reference

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `SERVER_PORT` | `8080` | Port for Spring Boot Tomcat server |
| `DB_HOST` | `postgres` | Hostname of PostgreSQL service |
| `DB_PORT` | `5432` | PostgreSQL listening port |
| `DB_NAME` | `smart_water_management` | Database name |
| `DB_USERNAME` | `water_admin` | Database username |
| `DB_PASSWORD` | `water_secure_password` | Database password |
| `JWT_SECRET` | `(32-byte secret)` | HMAC-SHA256 signing secret |
| `FLYWAY_ENABLED` | `true` | Automatically run Flyway migrations |
| `CORS_ALLOWED_ORIGINS`| `http://localhost:5173` | Whitelist for CORS requests |

---

## 4. Teardown & Maintenance

### Stop Services
```bash
docker compose -f docker/docker-compose.yml down
```

### Wipe Data Volume (Fresh Database)
```bash
docker compose -f docker/docker-compose.yml down -v
```
