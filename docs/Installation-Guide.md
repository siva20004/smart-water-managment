# Windows Installation & Local Setup Guide

Follow these step-by-step instructions to configure, run, and verify the Smart Water Management System on a Windows development workstation.

---

## 1. System Requirements

- **Operating System**: Windows 10 / 11 (64-bit)
- **Java Development Kit**: JDK 21+ (Eclipse Temurin or Oracle OpenJDK)
- **Node.js**: v20.x or v22.x LTS with npm
- **Database**: PostgreSQL 16+ (or Docker Engine)
- **Memory**: Minimum 8 GB RAM (16 GB recommended)

---

## 2. PostgreSQL Configuration

1. Ensure the PostgreSQL Windows Service is active:
   ```powershell
   Get-Service -Name postgresql*
   ```
2. Verify or create the database using `psql`:
   ```powershell
   $env:PGPASSWORD = "postgres"
   & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE smart_water_management;"
   ```
3. Execute Flyway database schema migrations:
   ```powershell
   $psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
   & $psql -U postgres -d smart_water_management -f database/migrations/V1__initial_schema.sql
   & $psql -U postgres -d smart_water_management -f database/migrations/V2__views_indexes_and_seed.sql
   & $psql -U postgres -d smart_water_management -f database/migrations/V5__schema_alignments_and_roles.sql
   ```

---

## 3. Spring Boot Backend Setup

1. Open PowerShell and navigate to the `backend` directory:
   ```powershell
   cd C:\Users\dell\Desktop\veloia\backend
   ```
2. Set development environment variables:
   ```powershell
   $env:DB_HOST = "localhost"
   $env:DB_PORT = "5432"
   $env:DB_NAME = "smart_water_management"
   $env:DB_USERNAME = "postgres"
   $env:DB_PASSWORD = "postgres"
   $env:JWT_SECRET = "supersecretjwtkeyforwatermanagementsystemdevelopmentonlyverylong256bitkey"
   $env:FLYWAY_ENABLED = "true"
   ```
3. Compile and execute tests:
   ```powershell
   .\mvnw.cmd test
   ```
4. Start the backend application:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   *Backend will start on `http://localhost:8080`*.

---

## 4. React + TypeScript Frontend Setup

1. Open a new PowerShell terminal and navigate to the `frontend` directory:
   ```powershell
   cd C:\Users\dell\Desktop\veloia\frontend
   ```
2. Install npm dependencies:
   ```powershell
   npm install
   ```
3. Verify production compilation:
   ```powershell
   npm run build
   ```
4. Start the Vite development server:
   ```powershell
   npm run dev
   ```
   *Access the web console in browser at `http://localhost:5173`*.

---

## 5. Verification Checklist

| Verification Task | Command / URL | Expected Result |
| :--- | :--- | :--- |
| **Backend Health Check** | `http://localhost:8080/api/health` | HTTP 200 `{"status": "UP"}` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | Interactive API documentation |
| **Frontend UI** | `http://localhost:5173` | Executive SCADA Dashboard loaded |
| **Database Migrations** | `flyway_schema_history` table | Versions 1, 2, 5 status `SUCCESS` |
