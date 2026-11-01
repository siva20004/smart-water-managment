# Windows Setup & Developer Guide

This guide provides step-by-step instructions for installing dependencies, setting up PostgreSQL, running migrations, and launching both backend and frontend applications on Windows 10/11.

---

## 1. Prerequisites Check (PowerShell)

Verify the required tools in PowerShell:
```powershell
java -version    # Must be Java 21 or higher
node -v          # Recommended Node.js v18+ (tested with v20/v25)
npm -v           # Recommended npm v9+
```

---

## 2. Database Setup

### Option 1: Standalone PostgreSQL via Docker (Fastest)
```powershell
docker compose -f docker/docker-compose.yml up -d postgres
```

### Option 2: Local PostgreSQL Service
Create the database and user using the initialization script:
```powershell
Get-Content database\scripts\init-db.sql | psql -U postgres -d postgres
```

---

## 3. Starting the Backend (Spring Boot 3)

```powershell
cd backend

# (Optional) Provide customized database environment variables
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="smart_water_db"
$env:DB_USERNAME="water_admin"
$env:DB_PASSWORD="water_secure_password"

# Launch Spring Boot
.\mvnw.cmd spring-boot:run
```
The backend starts on `http://localhost:8080`.
Verify health by visiting:
- `http://localhost:8080/api/health`
- `http://localhost:8080/actuator/health`

---

## 4. Starting the Frontend (React + Vite)

Open a new PowerShell terminal:
```powershell
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.
The dashboard will immediately probe `http://localhost:8080/api/health` and display real-time connection status, JVM memory telemetry, and database latency.
