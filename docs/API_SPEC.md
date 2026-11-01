# API Specification — Smart Water Management System

## Base URL
- Local Development: `http://localhost:8080/api`
- Docker Network: `http://backend:8080/api`

---

## Health Check Endpoint

### `GET /api/health`
Returns the operational diagnostic status of the backend application, JVM host resources, and the PostgreSQL database connectivity.

#### Response: `200 OK`
```json
{
  "status": "UP",
  "service": "Smart Water Management & Analytics Backend",
  "version": "1.0.0",
  "environment": "default",
  "timestamp": "2026-11-01T16:50:00.000Z",
  "uptimeSeconds": 120,
  "database": {
    "status": "UP",
    "databaseProductName": "PostgreSQL 16.1",
    "responseTimeMs": 4,
    "details": "Database connection operational"
  },
  "system": {
    "totalMemoryMb": 512,
    "freeMemoryMb": 380,
    "maxMemoryMb": 8192,
    "availableProcessors": 8,
    "jvmUptimeMs": 125000
  }
}
```

#### Status Values
- `UP`: Backend and database are fully operational.
- `DEGRADED`: Backend is running, but database connection timed out or is offline.
- `DOWN`: Core services are unserviceable.

---

## Spring Boot Actuator Endpoints

- `GET /actuator/health` — Spring Boot native health status
- `GET /actuator/info` — Build and version information
- `GET /actuator/metrics` — JVM runtime metrics list
