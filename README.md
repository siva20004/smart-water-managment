# Smart Water Management & Database Analytics System

> **Module 1: Architecture & Project Foundation**  
> An educational, high-performance water-utility simulation platform inspired by municipal water-management operations and industrial SCADA analytics.

---

## 1. Monorepo Folder Structure

```text
veloia/
├── backend/                               # Spring Boot 3 + Java 21 REST API
│   ├── .mvn/wrapper/                      # Maven Wrapper binaries & properties
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/veloia/smartwater/
│   │   │   │   ├── config/                # CORS and Spring Web MVC configuration
│   │   │   │   │   └── CorsConfig.java
│   │   │   │   ├── controller/            # REST API Controllers
│   │   │   │   │   └── HealthController.java (GET /api/health)
│   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   │   ├── DatabaseHealthInfo.java
│   │   │   │   │   └── HealthResponse.java
│   │   │   │   ├── service/               # Core business & diagnostic logic
│   │   │   │   │   └── HealthService.java
│   │   │   │   └── SmartWaterApplication.java
│   │   │   └── resources/
│   │   │       ├── db/migration/          # Flyway SQL migrations
│   │   │       │   └── V1__initial_schema.sql
│   │   │       └── application.yml        # Environment-driven configuration
│   │   └── test/                          # Unit and integration test suite
│   │       ├── java/com/veloia/smartwater/
│   │       │   └── SmartWaterApplicationTests.java
│   │       └── resources/
│   │           └── application-test.yml
│   ├── .env.example                       # Backend environment template
│   ├── mvnw                               # Maven wrapper script (Unix)
│   ├── mvnw.cmd                           # Maven wrapper script (Windows)
│   └── pom.xml                            # Maven project definition
│
├── frontend/                              # React + TypeScript + Vite SPA
│   ├── src/
│   │   ├── api/                           # Axios API client & endpoints
│   │   │   ├── apiClient.ts               # Request/Response interceptors & error handler
│   │   │   └── healthService.ts           # Health check probe service
│   │   ├── components/
│   │   │   ├── common/                    # Reusable UI primitives
│   │   │   │   ├── ErrorBoundary.tsx      # Global React error boundary
│   │   │   │   ├── Footer.tsx             # System footer
│   │   │   │   ├── Navbar.tsx             # Header with live backend status indicator
│   │   │   │   └── StatusBadge.tsx        # Status pill indicator
│   │   │   └── dashboard/                 # Module 1 Dashboard widgets
│   │   │       ├── DatabaseStatusCard.tsx # PostgreSQL engine & latency diagnostic card
│   │   │       ├── HealthCard.tsx         # Backend service & uptime card
│   │   │       ├── ModuleRoadmapCard.tsx  # Architectural milestone roadmap
│   │   │       └── SystemMetricsCard.tsx  # JVM memory & CPU telemetry card
│   │   ├── pages/
│   │   │   ├── LandingDashboard.tsx       # Primary Module 1 control panel
│   │   │   └── NotFoundPage.tsx           # 404 handler
│   │   ├── routes/
│   │   │   └── AppRoutes.tsx              # React Router v7 routes
│   │   ├── types/
│   │   │   └── health.ts                  # TypeScript API contracts
│   │   ├── App.tsx                        # Root application layout
│   │   ├── index.css                      # Tailwind CSS v3 directives
│   │   └── main.tsx                       # React DOM entrypoint
│   ├── .env.example                       # Frontend environment template
│   ├── index.html                         # SPA index template
│   ├── package.json                       # Dependencies (Recharts, Router, Axios, Lucide)
│   ├── postcss.config.js                  # PostCSS plugins
│   ├── tailwind.config.js                 # Tailwind CSS palette & configuration
│   ├── tsconfig.json                      # TypeScript configuration
│   └── vite.config.ts                     # Vite build configuration
│
├── database/                              # Database assets & scripts
│   ├── migrations/
│   │   └── V1__initial_schema.sql         # Baseline migration mirror
│   ├── scripts/
│   │   └── init-db.sql                    # Initial PostgreSQL user & db provisioning
│   └── README.md                          # Database documentation
│
├── docs/                                  # Project technical documentation
│   ├── API_SPEC.md                        # REST API endpoint contracts
│   ├── ARCHITECTURE.md                    # Architecture diagram & module roadmap
│   └── SETUP.md                           # Extended Windows setup runbook
│
├── docker/                                # Containerization manifests
│   ├── .env.docker.example                # Docker Compose environment template
│   ├── docker-compose.yml                 # Multi-service compose (PostgreSQL, Backend, Frontend)
│   ├── Dockerfile.backend                 # Multi-stage Maven + Java 21 build
│   ├── Dockerfile.frontend                # Multi-stage Vite + Nginx build
│   └── nginx.conf                         # Reverse proxy & static serving config
│
├── .env.example                           # Root environment variable template
├── .gitignore                             # Git ignore rules for Java, Node, IDEs, and secrets
└── README.md                              # This documentation file
```

---

## 2. Tech Stack

- **Backend**: Java 21 LTS, Spring Boot 3.3.6
- **Database & ORM**: PostgreSQL 16, Spring Data JPA / Hibernate, Flyway Migrations
- **Frontend**: React 19, TypeScript, Vite, Tailwind CSS, React Router DOM, Recharts, Lucide React, Axios
- **Build & Quality Tools**: Maven Wrapper (`mvnw.cmd`), npm
- **Containerization**: Docker & Docker Compose (Optional for local development)

---

## 3. Environment Variables Reference

Copy `.env.example` to `.env` or set environment variables in your Windows terminal:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `SERVER_PORT` | `8080` | Port for Spring Boot backend |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `smart_water_db` | Database name |
| `DB_USERNAME` | `postgres` (or `water_admin`) | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `FLYWAY_ENABLED` | `true` | Run migrations automatically on startup |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Allowed origins for web client |
| `VITE_API_BASE_URL` | `http://localhost:8080/api` | REST API URL for Vite frontend |

---

## 4. Database Setup on Windows

### Option A: Using Docker for PostgreSQL only (Recommended)
You do not need to containerize the whole app for local development. Run just the PostgreSQL database in the background:
```powershell
docker compose -f docker/docker-compose.yml up -d postgres
```

### Option B: Local PostgreSQL Installation (Windows Service)
1. In PowerShell, execute the initialization script against your local PostgreSQL instance:
   ```powershell
   Get-Content database\scripts\init-db.sql | psql -U postgres -d postgres
   ```
2. When prompted, enter your PostgreSQL administrative password. This will create:
   - Database user: `water_admin`
   - Database name: `smart_water_db`

---

## 5. How to Start Both Applications on Windows

### Terminal 1: Backend (Spring Boot 3)
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
- **Service URL**: `http://localhost:8080`
- **Health Check Probe**: `http://localhost:8080/api/health`
- **Actuator Health**: `http://localhost:8080/actuator/health`

### Terminal 2: Frontend (React + Vite)
```powershell
cd frontend
npm install
npm run dev
```
- **Dashboard URL**: `http://localhost:5173`

---

## 6. How to Run Builds and Tests

### Run Backend Unit & Integration Tests:
```powershell
cd backend
.\mvnw.cmd clean test
```
*Tests automatically utilize an embedded in-memory database test profile (`application-test.yml`), verifying context loading and `/api/health` contracts without requiring external database dependencies.*

### Run Frontend Production Build & TypeScript Typecheck:
```powershell
cd frontend
npm run build
```

---

## 7. Optional: Full Docker Compose Run
To launch the entire stack (PostgreSQL + Backend + Frontend) inside Docker containers:
```powershell
docker compose -f docker/docker-compose.yml up -d --build
```
Access the application at `http://localhost:5173`.
"# smart-water-managment-system" 
