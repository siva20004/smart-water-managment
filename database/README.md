# Database Engineering & Migration Manual

This directory contains the database layer for the **Smart Water Management System** (Database: `smart_water_management`).

---

## 1. Directory Structure

```text
/database
  ├── migrations/
  │   ├── V1__initial_schema.sql             # Base extensions and facilities schema
  │   ├── V2__smart_water_schema.sql         # 11 Relational tables with BIGINT identities & constraints
  │   ├── V3__indexes_and_views.sql          # B-Tree/partial indexes and 3 analytical reporting views
  │   └── V4__triggers_and_validation.sql    # Monotonic reading validation trigger & audit triggers
  ├── seeds/
  │   └── R__sample_japanese_utility_seed.sql# Simulated Japanese utility dataset (120+ records)
  ├── scripts/
  │   ├── init-db.sql                        # Role and database creation script
  │   ├── seed-data.sql                      # Convenience script to run seed data
  │   ├── validate-data.sql                  # Automated data integrity & business rule validator
  │   ├── analytics-examples.sql             # SQL examples (JOIN, GROUP BY, CTE, Subqueries, LAG, LEAD)
  │   └── backup-restore.ps1                 # Turn-key PowerShell backup and restore utility
  ├── backups/                               # Local backup directory (git-ignored)
  └── README.md                              # This file
```

---

## 2. Quick Execution Guide (Windows PowerShell)

Ensure PostgreSQL 14+ is running locally on port 5432:

```powershell
# 1. Provision Database
$env:PGPASSWORD="postgres"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -f database\scripts\init-db.sql

# 2. Run Flyway Migrations in Order
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\migrations\V1__initial_schema.sql
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\migrations\V2__smart_water_schema.sql
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\migrations\V3__indexes_and_views.sql
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\migrations\V4__triggers_and_validation.sql
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\seeds\R__sample_japanese_utility_seed.sql

# 3. Verify Database Integrity & Business Rules
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\scripts\validate-data.sql

# 4. Execute Advanced Analytical Query Suite
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -d smart_water_management -f database\scripts\analytics-examples.sql
```

---

## 3. Database Architecture Documentation

For complete normalization rationale, table definitions, entity-relationship diagrams, and backup runbooks, see [docs/DATABASE_ARCHITECTURE.md](file:///c:/Users/dell/Desktop/veloia/docs/DATABASE_ARCHITECTURE.md).
