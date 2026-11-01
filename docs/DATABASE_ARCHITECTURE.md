# Module 2: Advanced PostgreSQL Database Engineering Specification
## Smart Water Management System (Simulated Japanese Municipal Water Utility)

---

## 1. Entity-Relationship (ER) Architecture Diagram

```mermaid
erDiagram
    REGIONS ||--o{ LOCATIONS : "encompasses"
    LOCATIONS ||--o{ WATER_METERS : "installs"
    CUSTOMERS ||--o{ WATER_METERS : "subscribes"
    WATER_METERS ||--o{ METER_READINGS : "transmits"
    CUSTOMERS ||--o{ WATER_BILLS : "billed to"
    WATER_METERS |o--o{ WATER_BILLS : "measured by"
    WATER_BILLS ||--o{ PAYMENTS : "settled via"
    LOCATIONS ||--o{ WATER_QUALITY_RECORDS : "sampled at"
    LOCATIONS ||--o{ LEAK_ALERTS : "detected at"
    WATER_METERS |o--o{ LEAK_ALERTS : "flagged by"
    APP_USERS ||--o{ MAINTENANCE_REQUESTS : "assigned to"
    WATER_METERS |o--o{ MAINTENANCE_REQUESTS : "service on"
    LEAK_ALERTS |o--o{ MAINTENANCE_REQUESTS : "resolves"
    LOCATIONS ||--o{ MAINTENANCE_REQUESTS : "executed at"

    REGIONS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR code UK "Unique Region Code"
        VARCHAR name "Sector / Ward Name"
        VARCHAR prefecture "Prefecture (Tokyo, Kanagawa, Osaka)"
        VARCHAR status "ACTIVE, INACTIVE, PLANNED"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    LOCATIONS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        BIGINT region_id FK "REFERENCES regions(id)"
        VARCHAR location_code UK "Unique Facility Code"
        VARCHAR address_line1 "Street Address"
        VARCHAR city "Ward / Municipality"
        VARCHAR postal_code "Postal Code"
        NUMERIC latitude "Coordinate (-90 to 90)"
        NUMERIC longitude "Coordinate (-180 to 180)"
        NUMERIC elevation_meters "Elevation in meters"
        VARCHAR installation_type "RESIDENTIAL, COMMERCIAL, INDUSTRIAL, etc."
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    CUSTOMERS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR customer_number UK "Account Identifier"
        VARCHAR customer_type "INDIVIDUAL, CORPORATE, GOVERNMENT"
        VARCHAR full_name "Legal Entity / Person"
        VARCHAR email UK "Customer Email"
        VARCHAR phone_number "Contact Telephone"
        VARCHAR tax_id UK "Corporate / Individual Tax ID"
        VARCHAR billing_address "Official Billing Address"
        VARCHAR status "ACTIVE, SUSPENDED, TERMINATED"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    WATER_METERS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR serial_number UK "Hardware Serial Number"
        BIGINT customer_id FK "REFERENCES customers(id) ON DELETE SET NULL"
        BIGINT location_id FK "REFERENCES locations(id) ON DELETE RESTRICT"
        VARCHAR meter_model "Hardware Model"
        VARCHAR meter_type "ULTRASONIC, ELECTROMAGNETIC, etc."
        INTEGER diameter_mm "Nominal Pipe Diameter (mm)"
        DATE installation_date "Commissioning Date"
        VARCHAR status "ACTIVE, INACTIVE, MAINTENANCE, etc."
        DATE last_inspection_date "Field Calibration Date"
        NUMERIC battery_level_percent "Telemetry Battery State"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    METER_READINGS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        BIGINT meter_id FK "REFERENCES water_meters(id) ON DELETE CASCADE"
        TIMESTAMPTZ reading_timestamp "Sampling Moment (UTC)"
        NUMERIC cumulative_reading_m3 "Monotonic Index (m3)"
        NUMERIC incremental_consumption_m3 "Interval Flow Volume (m3)"
        NUMERIC water_flow_rate_lpm "Instantaneous Flow (L/min)"
        NUMERIC water_pressure_bar "Hydraulic Pressure (bar)"
        NUMERIC water_temperature_celsius "Temperature (°C)"
        VARCHAR reading_source "AMI_AUTOMATED, AMR, MANUAL"
        BOOLEAN is_validated "Integrity Flag"
        TEXT validation_notes "Audit & METER_RESET notes"
        TIMESTAMPTZ created_at
    }

    WATER_BILLS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR bill_number UK "Invoice Serial Number"
        BIGINT customer_id FK "REFERENCES customers(id) ON DELETE RESTRICT"
        BIGINT meter_id FK "REFERENCES water_meters(id) ON DELETE SET NULL"
        DATE billing_period_start "Period Start"
        DATE billing_period_end "Period End"
        NUMERIC base_fee "Fixed Connection Fee"
        NUMERIC volumetric_rate "Tiered Unit Rate"
        NUMERIC consumption_m3 "Billed Water Volume (m3)"
        NUMERIC consumption_charge "Calculated Consumption Charge"
        NUMERIC tax_amount "Consumption Tax"
        NUMERIC total_amount "Total Billed Amount"
        DATE due_date "Payment Deadline"
        VARCHAR status "ISSUED, PARTIALLY_PAID, PAID, OVERDUE"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    PAYMENTS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR payment_reference UK "Remittance Reference"
        BIGINT bill_id FK "REFERENCES water_bills(id) ON DELETE RESTRICT"
        TIMESTAMPTZ payment_date "Transaction Moment"
        NUMERIC amount "Paid Currency Amount"
        VARCHAR payment_method "BANK_TRANSFER, DIRECT_DEBIT, etc."
        VARCHAR transaction_status "PENDING, COMPLETED, FAILED"
        TEXT notes "Payment Gateway Audit"
        TIMESTAMPTZ created_at
    }

    WATER_QUALITY_RECORDS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR sample_code UK "Laboratory Test Code"
        BIGINT location_id FK "REFERENCES locations(id) ON DELETE RESTRICT"
        TIMESTAMPTZ sample_timestamp "Physical Sample Moment"
        NUMERIC ph_level "Acidity Index (0 to 14)"
        NUMERIC turbidity_ntu "Turbidity (NTU)"
        NUMERIC residual_chlorine_mg_l "Free Chlorine (mg/L)"
        NUMERIC dissolved_oxygen_mg_l "Dissolved O2 (mg/L)"
        NUMERIC conductivity_us_cm "Electrical Conductivity (uS/cm)"
        NUMERIC water_temperature_celsius "Sample Temperature (°C)"
        VARCHAR potability_status "POTABLE, NON_POTABLE_ALERT"
        VARCHAR sampled_by "Inspector / Chemist"
        TIMESTAMPTZ created_at
    }

    LEAK_ALERTS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR alert_code UK "SCADA Incident Reference"
        BIGINT meter_id FK "REFERENCES water_meters(id) ON DELETE SET NULL"
        BIGINT location_id FK "REFERENCES locations(id) ON DELETE RESTRICT"
        VARCHAR severity "LOW, MEDIUM, HIGH, CRITICAL"
        VARCHAR alert_type "CONTINUOUS_FLOW, PRESSURE_DROP, etc."
        TIMESTAMPTZ detected_at "Detection Timestamp"
        NUMERIC estimated_loss_rate_m3_h "Estimated Leak Rate (m3/h)"
        VARCHAR status "NEW, INVESTIGATING, RESOLVED"
        TEXT resolution_notes "Root Cause Analysis"
        TIMESTAMPTZ resolved_at "Clearance Moment"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    APP_USERS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR username UK "Login Identity"
        VARCHAR email UK "Corporate Email"
        VARCHAR password_hash "Salted BCrypt Hash ($2a$12$...)"
        VARCHAR full_name "Staff Name"
        VARCHAR role "ADMIN, UTILITY_ENGINEER, TECHNICIAN, etc."
        VARCHAR department "Organizational Unit"
        VARCHAR status "ACTIVE, SUSPENDED, DEACTIVATED"
        TIMESTAMPTZ last_login_at
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    MAINTENANCE_REQUESTS {
        BIGINT id PK "GENERATED ALWAYS AS IDENTITY"
        VARCHAR request_number UK "Work Order Number"
        BIGINT meter_id FK "REFERENCES water_meters(id) ON DELETE SET NULL"
        BIGINT alert_id FK "REFERENCES leak_alerts(id) ON DELETE SET NULL"
        BIGINT location_id FK "REFERENCES locations(id) ON DELETE RESTRICT"
        BIGINT assigned_technician_id FK "REFERENCES app_users(id)"
        VARCHAR priority "LOW, MEDIUM, HIGH, EMERGENCY"
        VARCHAR category "LEAK_REPAIR, METER_REPLACEMENT, etc."
        VARCHAR title "Work Order Title"
        TEXT description "Engineering Diagnostics"
        VARCHAR status "OPEN, IN_PROGRESS, COMPLETED"
        TIMESTAMPTZ scheduled_start
        TIMESTAMPTZ actual_completion
        TEXT resolution_summary
        NUMERIC parts_cost "Replacement Components Cost"
        NUMERIC labor_hours "Technician Man-Hours"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
```

---

## 2. Relational Normalization & Design Decisions

### 1NF (First Normal Form)
- All columns contain atomic, non-decomposable values. No CSV lists, arrays, or repeating groups are stored for telemetry readings, payments, or locations.
- Every table is enforced with an immutable `BIGINT GENERATED ALWAYS AS IDENTITY` primary key.

### 2NF (Second Normal Form)
- No non-key attribute is dependent on a fraction of a composite key.
- Natural multi-attribute business identities (e.g., `meter_id + reading_timestamp`) are protected via `UNIQUE` constraints (`uq_meter_reading_timestamp`), while synthetic identity keys optimize indexing and foreign key constraints across large-scale timeseries.

### 3NF & Boyce-Codd Normal Form (BCNF)
- **Separation of Physical Locations and Legal Customers**:
  - A customer can own or occupy multiple properties (e.g., a corporate client with both a manufacturing plant and a corporate headquarters).
  - A physical location persists regardless of customer turnover (meters remain installed while tenants change).
  - Eliminates transitive dependencies: Geographical attributes (`prefecture`, `city`, `postal_code`, `elevation_meters`, `latitude`, `longitude`) depend solely on `locations` and `regions`, never duplicated in `customers` or `water_meters`.
- **Decoupled Billing & Payment Ledger**:
  - `water_bills` and `payments` are strictly split to enable 1:M relationships (partial payments, installment settlements, multi-method remittances).
  - Outstanding balances and delinquency aging are computed dynamically or via analytical views (`v_outstanding_bills`) rather than stored redundantly in mutable customer balances, eliminating data divergence bugs.
- **Auditable Security**:
  - Passwords are strictly stored as salted BCrypt hashes (`password_hash VARCHAR(255)`), preventing plain-text credential leaks.

---

## 3. Business Integrity Constraints & Automated Triggers

### Monotonic Meter Reading Trigger (`fn_validate_meter_reading`)
In municipal water metering, cumulative index registers (`cumulative_reading_m3`) must increase monotonically over time.
- **Normal Flow**: When a new reading is inserted, the trigger automatically computes `incremental_consumption_m3 = NEW.cumulative_reading_m3 - PREVIOUS.cumulative_reading_m3`.
- **Negative Reading Trap**: If `NEW.cumulative_reading_m3 < PREVIOUS.cumulative_reading_m3`, the trigger throws a `check_violation` exception with error message:
  ```text
  Integrity Violation: Cumulative meter reading decreased from X m3 to Y m3 for Meter ID Z without authorized METER_RESET flag in validation_notes
  ```
- **Authorized Reset & Rollover**: If a meter rolls over past 99999.999 m3 or is physically replaced with a zeroed transducer, passing `METER_RESET` or `ROLLOVER` inside `validation_notes` permits the decrement and safely re-baselines the incremental consumption.

---

## 4. Analytical Views

### 1. `v_latest_meter_readings`
Combines meter hardware properties, customer demographics, and physical location coordinates with the single latest reading using window partition ordering:
```sql
ROW_NUMBER() OVER (PARTITION BY r.meter_id ORDER BY r.reading_timestamp DESC) = 1
```
Computes `hours_since_last_reading` to detect AMI communication dropouts.

### 2. `v_monthly_consumption`
Aggregates meter readings across calendar months, providing:
- Total consumed volume in cubic meters ($m^3$)
- Average and peak instantaneous flow rate ($L/min$)
- Minimum, maximum, and average hydraulic pressure ($bar$)

### 3. `v_outstanding_bills`
Monitors all uncollected utility revenue:
- Total billed vs total paid via completed transactions
- `outstanding_balance = total_amount - total_paid`
- `days_overdue = GREATEST(0, CURRENT_DATE - due_date)`
- Filtered using `HAVING outstanding_balance > 0.00`

---

## 5. Backup & Restore Runbook (Windows PowerShell)

The platform includes a dedicated PowerShell script [database/scripts/backup-restore.ps1](file:///c:/Users/dell/Desktop/veloia/database/scripts/backup-restore.ps1).

### Full Database Backup (Custom Binary Format -Fc)
```powershell
powershell -ExecutionPolicy Bypass -File database\scripts\backup-restore.ps1 -Action backup -Format custom
```
Creates a compressed archive in `database/backups/smart_water_management_YYYYMMDD_HHMMSS.dump`.

### Database Restore
```powershell
powershell -ExecutionPolicy Bypass -File database\scripts\backup-restore.ps1 -Action restore -BackupFile database\backups\<filename>.dump
```

### Table & View Inventory Verification
```powershell
powershell -ExecutionPolicy Bypass -File database\scripts\backup-restore.ps1 -Action verify
```

---

## 6. How to Run Migrations on a Fresh Database

To initialize and run all migrations on a clean PostgreSQL server:

1. **Create the Database**:
   ```powershell
   $env:PGPASSWORD="postgres"
   psql -U postgres -h localhost -c "CREATE DATABASE smart_water_management WITH OWNER postgres ENCODING 'UTF8';"
   ```

2. **Execute Flyway Migrations in Order**:
   ```powershell
   $env:PGPASSWORD="postgres"
   psql -U postgres -h localhost -d smart_water_management -f database\migrations\V1__initial_schema.sql
   psql -U postgres -h localhost -d smart_water_management -f database\migrations\V2__smart_water_schema.sql
   psql -U postgres -h localhost -d smart_water_management -f database\migrations\V3__indexes_and_views.sql
   psql -U postgres -h localhost -d smart_water_management -f database\migrations\V4__triggers_and_validation.sql
   psql -U postgres -h localhost -d smart_water_management -f database\seeds\R__sample_japanese_utility_seed.sql
   ```

3. **Execute Data Validation Suite**:
   ```powershell
   psql -U postgres -h localhost -d smart_water_management -f database\scripts\validate-data.sql
   ```

4. **Execute Advanced Analytics Suite**:
   ```powershell
   psql -U postgres -h localhost -d smart_water_management -f database\scripts\analytics-examples.sql
   ```
