# VELOIA AQUA-SCADA 2.0 — Smart Water Management & Database Analytics System

> **Educational Utility Disclaimer**: This project is an advanced educational simulation platform designed for academic, training, and database architecture benchmarking. All municipal customer names, meter telemetry, Japanese ward addresses (Tokyo-Kanto), billing tariffs, hydraulic leak events, and water quality assays are simulated test fixtures.

---

## 🌊 System Overview

The **Smart Water Management & Database Analytics System** is a full-stack, enterprise-grade municipal water utility platform engineered with **Java 21**, **Spring Boot 3**, **PostgreSQL 18**, and **React 19 + TypeScript + Vite**.

It provides operational intelligence, advanced relational database modeling, real-time IoT telemetry ingestion, automated non-revenue water (NRW) leak detection, water quality surveillance, field maintenance dispatching, and executive analytics.

---

## 🏛️ Comprehensive Architecture

```
                                  [ Internet / SCADA Edge ]
                                              │
                      ┌───────────────────────┴───────────────────────┐
                      ▼                                               ▼
             [ React 19 Frontend ]                           [ IoT Water Meters ]
           (Vite + Tailwind + Recharts)                   (Telemetry Ingestion Stream)
                      │                                               │
                      │ HTTP / REST / JWT                             │ REST POST /api/readings
                      ▼                                               ▼
     ┌─────────────────────────────────────────────────────────────────────────┐
     │                      Spring Boot 3 Backend (Java 21)                    │
     │                                                                         │
     │  ┌───────────────────┐  ┌───────────────────┐  ┌─────────────────────┐  │
     │  │ Security & RBAC   │  │ Web REST Layer    │  │ Business Services   │  │
     │  │ (JWT + BCrypt)    │  │ (11 Controllers)  │  │ (10 Service Impls)  │  │
     │  └───────────────────┘  └───────────────────┘  └─────────────────────┘  │
     │                                                                         │
     │  ┌───────────────────┐  ┌───────────────────┐  ┌─────────────────────┐  │
     │  │ Leak Heuristics   │  │ Tariff Billing    │  │ Quality Compliance  │  │
     │  │ (Surge/Acoustic)  │  │ (Tiered Engine)   │  │ (WHO / MHLW Rules)  │  │
     │  └───────────────────┘  └───────────────────┘  └─────────────────────┘  │
     │                                     │                                   │
     │                                     ▼                                   │
     │                       Spring Data JPA & Hibernate 6                     │
     └─────────────────────────────────────┬───────────────────────────────────┘
                                           │ HikariCP (Connection Pool)
                                           ▼
     ┌─────────────────────────────────────────────────────────────────────────┐
     │                 PostgreSQL Database (Flyway Migrated)                  │
     │                                                                         │
     │  • 11 Relational Tables with Identity Keys & Strict Domain Constraints  │
     │  • 3 Optimized Analytical Views (Latest Readings, Monthly, Overdue)     │
     │  • 12 High-Cardinality Composite & Partial B-Tree Indexes               │
     │  • Window Function LAG() Telemetry Pipelines & Recursive CTEs           │
     └─────────────────────────────────────────────────────────────────────────┘
```

---

## 📂 Monorepo Structure

```
veloia/
├── backend/                       # Spring Boot 3 + Java 21 REST API
│   ├── src/main/java/com/veloia/smartwater/
│   │   ├── config/                # OpenAPI, Cors, WebMvc configs
│   │   ├── controller/            # 11 REST controllers
│   │   ├── dto/                   # Request & Response DTOs
│   │   ├── entity/                # JPA entities with schema mapping
│   │   ├── exception/             # RestControllerAdvice error handler
│   │   ├── mapper/                # Component mappers
│   │   ├── repository/            # Spring Data JPA repositories with @EntityGraph
│   │   ├── security/              # JWT provider, filter & SecurityConfig
│   │   └── service/               # Core business services & heuristics
│   └── src/main/resources/
│       ├── application.yml        # Multi-profile environment configuration
│       └── db/migration/          # Flyway SQL migrations V1 - V5
├── frontend/                      # React 19 + TypeScript + Vite UI
│   ├── src/
│   │   ├── api/                   # Axios API clients & interceptors
│   │   ├── components/            # Layout, Navigation, Cards, Modals
│   │   ├── context/               # AuthContext (JWT & Role Switcher)
│   │   ├── pages/                 # 9 enterprise operational views
│   │   ├── routes/                # Client routing & auth guards
│   │   └── types/                 # TypeScript models matching backend DTOs
├── database/                      # Flyway migrations, seeds & SQL scripts
│   ├── migrations/                # V1 through V5 SQL migration scripts
│   ├── seeds/                     # Simulated Tokyo-Kanto utility fixtures
│   └── scripts/                   # Backup, restore & query verification
├── docker/                        # Multi-stage Dockerfiles & Docker Compose
│   ├── Dockerfile.backend         # Eclipse Temurin 21 JRE Alpine image
│   ├── Dockerfile.frontend        # Node 20 builder + Nginx Alpine runner
│   └── docker-compose.yml         # 3-tier container orchestration
└── docs/                          # Architecture & Engineering documentation
```

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **Java**: Eclipse Temurin or Oracle JDK 21+
- **Node.js**: v20+ with npm
- **Database**: PostgreSQL 16+ (running on `localhost:5432`)

### 2. Database Setup
```bash
# Set PostgreSQL superuser password
$env:PGPASSWORD = "postgres"

# Execute Flyway migrations and seed fixtures
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d smart_water_management -f database/migrations/V1__initial_schema.sql
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d smart_water_management -f database/migrations/V2__views_indexes_and_seed.sql
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d smart_water_management -f database/migrations/V5__schema_alignments_and_roles.sql
```

### 3. Backend Startup
```bash
cd backend
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "postgres"
$env:JWT_SECRET = "supersecretjwtkeyforwatermanagementsystemdevelopmentonlyverylong256bitkey"
.\mvnw.cmd spring-boot:run
```
*API running at*: `http://localhost:8080/api`  
*Swagger Documentation*: `http://localhost:8080/swagger-ui.html`

### 4. Frontend Startup
```bash
cd frontend
npm install
npm run dev
```
*Web Dashboard running at*: `http://localhost:5173`

---

## 👥 Demo Authentication Credentials

The system includes pre-seeded role-based user accounts with password `Password123!`:

| Username | Role | Scope of Responsibility |
| :--- | :--- | :--- |
| `admin` | **ADMIN** | Full administrative rights, user provisioning, global exports |
| `operator` | **OPERATOR** | Daily SCADA operations, meter assignment, billing generation |
| `technician`| **TECHNICIAN** | Work order lifecycle management, field maintenance execution |
| `analyst` | **ANALYST** | Read-only executive analytics, non-revenue water modeling |

---

## 📚 Complete Documentation Index

- [Architecture Guide](file:///C:/Users/dell/Desktop/veloia/docs/Architecture.md)
- [Database Design Specification](file:///C:/Users/dell/Desktop/veloia/docs/Database-Design.md)
- [Entity-Relationship Diagram](file:///C:/Users/dell/Desktop/veloia/docs/ER-Diagram.md)
- [REST API Documentation](file:///C:/Users/dell/Desktop/veloia/docs/API-Documentation.md)
- [Windows Installation Guide](file:///C:/Users/dell/Desktop/veloia/docs/Installation-Guide.md)
- [Automated Testing Report](file:///C:/Users/dell/Desktop/veloia/docs/Testing-Report.md)
- [Database Optimization & Query Analysis Report](file:///C:/Users/dell/Desktop/veloia/docs/Database-Optimization-Report.md)
- [Container Deployment Guide](file:///C:/Users/dell/Desktop/veloia/docs/Deployment-Guide.md)
