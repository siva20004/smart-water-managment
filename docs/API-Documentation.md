# REST API Documentation

Base URL: `http://localhost:8080/api`  
Interactive Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON Specification: `http://localhost:8080/v3/api-docs`

---

## 1. Authentication & Identity (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate user credentials and issue signed JWT | No |
| `GET` | `/api/auth/me` | Fetch currently authenticated user profile | Yes |
| `GET` | `/api/auth/demo-accounts`| List simulated demo test identities and assigned roles | No |

### Example Request (`POST /api/auth/login`)
```json
{
  "username": "admin",
  "password": "Password123!"
}
```

---

## 2. Customer Management (`/api/customers`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/customers` | Search & paginate customers with filters (`type`, `status`) |
| `GET` | `/api/customers/{id}` | Fetch customer detail by ID |
| `POST` | `/api/customers` | Register a new customer |
| `PUT` | `/api/customers/{id}` | Update existing customer details |
| `PATCH`| `/api/customers/{id}/status`| Update status (`ACTIVE`, `INACTIVE`, `SUSPENDED`) |
| `DELETE`| `/api/customers/{id}`| Remove customer account |

---

## 3. Water Meter Fleet (`/api/meters`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/meters` | Query smart meter asset fleet with pagination & filters |
| `GET` | `/api/meters/{id}` | Retrieve meter asset profile and battery level |
| `POST` | `/api/meters` | Provision and install new smart meter |
| `PUT` | `/api/meters/{id}` | Modify meter technical parameters |
| `PATCH`| `/api/meters/{id}/status` | Transition status (`ACTIVE`, `FAULTY`, `RETIRED`) |
| `PATCH`| `/api/meters/{id}/assign` | Assign meter to customer account |

---

## 4. Telemetry Readings & Ingestion (`/api/readings`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/readings` | Stream meter readings with pagination |
| `GET` | `/api/readings/meter/{meterId}` | Retrieve historical readings for specific meter |
| `POST` | `/api/readings` | Ingest new telemetry packet with cumulative check |

### Example Ingestion Request (`POST /api/readings`)
```json
{
  "meterId": 1,
  "readingTimestamp": "2026-11-01T23:00:00Z",
  "cumulativeReadingM3": 1450.75,
  "waterFlowRateLpm": 22.4,
  "waterPressureBar": 3.8,
  "waterTemperatureCelsius": 17.2,
  "readingSource": "AUTOMATIC_IOT",
  "meterReset": false
}
```

---

## 5. Water Billing & Payments (`/api/bills`, `/api/payments`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/bills` | Search utility invoices by customer and status |
| `GET` | `/api/bills/{id}` | Retrieve itemized bill breakdown |
| `POST` | `/api/bills/generate` | Generate invoice with simulated tiered tariff |
| `POST` | `/api/bills/payments` | Record payment against outstanding bill |
| `GET` | `/api/bills/{id}/payments` | View payment receipts for a specific bill |
| `GET` | `/api/payments` | Paginate all settlement receipts |

---

## 6. Leak Alerts & Heuristic Engine (`/api/leak-alerts`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/leak-alerts` | Query active leak alarms by severity and status |
| `GET` | `/api/leak-alerts/{id}` | Retrieve acoustic and pressure telemetry details |
| `POST` | `/api/leak-alerts/scan` | Trigger autonomous heuristic anomaly detection scan |
| `PATCH`| `/api/leak-alerts/{id}/status` | Update incident status (`INVESTIGATING`, `RESOLVED`) |

---

## 7. Water Quality Surveillance (`/api/water-quality`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/water-quality` | Query laboratory water quality assay records |
| `GET` | `/api/water-quality/statistics` | Retrieve aggregate compliance and average chemical stats |
| `GET` | `/api/water-quality/trends` | Fetch multi-parameter time-series trends |
| `POST` | `/api/water-quality` | Record physical-chemical assay sample |

---

## 8. Maintenance Work Orders (`/api/maintenance`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/maintenance` | Paginate field work orders by status and priority |
| `GET` | `/api/maintenance/{id}` | Retrieve work order ticket details |
| `POST` | `/api/maintenance` | Dispatch new maintenance work order |
| `PATCH`| `/api/maintenance/{id}/status` | Lifecycle transition (`IN_PROGRESS`, `COMPLETED`) |

---

## 9. Executive Analytics (`/api/analytics`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/analytics` | Retrieve 10 high-level KPIs and 9 aggregated chart sets |
| `GET` | `/api/analytics/export/csv` | Stream executive KPI summary export as CSV |
