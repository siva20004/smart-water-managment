-- =============================================================================
-- Migration: V3__indexes_and_views.sql
-- Description: Performance Indexes and Analytical Views
-- Module: 2 - Advanced PostgreSQL Database Engineering
-- =============================================================================

-- =============================================================================
-- 1. INDEXES FOR PERFORMANCE OPTIMIZATION
-- =============================================================================

-- Regions
CREATE INDEX IF NOT EXISTS idx_regions_prefecture ON regions(prefecture);
CREATE INDEX IF NOT EXISTS idx_regions_status ON regions(status);

-- Locations
CREATE INDEX IF NOT EXISTS idx_locations_region_id ON locations(region_id);
CREATE INDEX IF NOT EXISTS idx_locations_city ON locations(city);
CREATE INDEX IF NOT EXISTS idx_locations_postal_code ON locations(postal_code);
CREATE INDEX IF NOT EXISTS idx_locations_type ON locations(installation_type);

-- Customers
CREATE INDEX IF NOT EXISTS idx_customers_type ON customers(customer_type);
CREATE INDEX IF NOT EXISTS idx_customers_status ON customers(status);
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(full_name);

-- Water Meters
CREATE INDEX IF NOT EXISTS idx_meters_customer_id ON water_meters(customer_id);
CREATE INDEX IF NOT EXISTS idx_meters_location_id ON water_meters(location_id);
CREATE INDEX IF NOT EXISTS idx_meters_status ON water_meters(status);
CREATE INDEX IF NOT EXISTS idx_meters_type ON water_meters(meter_type);

-- Meter Readings (High-Volume Timeseries)
CREATE INDEX IF NOT EXISTS idx_readings_meter_time_desc ON meter_readings(meter_id, reading_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_readings_timestamp ON meter_readings(reading_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_readings_flow_rate ON meter_readings(water_flow_rate_lpm) WHERE water_flow_rate_lpm > 0.0;
CREATE INDEX IF NOT EXISTS idx_readings_pressure ON meter_readings(water_pressure_bar);

-- Water Bills
CREATE INDEX IF NOT EXISTS idx_bills_customer_id ON water_bills(customer_id);
CREATE INDEX IF NOT EXISTS idx_bills_meter_id ON water_bills(meter_id);
CREATE INDEX IF NOT EXISTS idx_bills_status ON water_bills(status);
CREATE INDEX IF NOT EXISTS idx_bills_due_date ON water_bills(due_date);
-- Partial index for active delinquent / outstanding collections
CREATE INDEX IF NOT EXISTS idx_bills_active_unpaid ON water_bills(due_date)
    WHERE status IN ('ISSUED', 'PARTIALLY_PAID', 'OVERDUE');

-- Payments
CREATE INDEX IF NOT EXISTS idx_payments_bill_id ON payments(bill_id);
CREATE INDEX IF NOT EXISTS idx_payments_date ON payments(payment_date DESC);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(transaction_status);

-- Water Quality Records
CREATE INDEX IF NOT EXISTS idx_wqr_location_time ON water_quality_records(location_id, sample_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_wqr_status ON water_quality_records(potability_status);
CREATE INDEX IF NOT EXISTS idx_wqr_turbidity ON water_quality_records(turbidity_ntu);

-- Leak Alerts
CREATE INDEX IF NOT EXISTS idx_leak_location_id ON leak_alerts(location_id);
CREATE INDEX IF NOT EXISTS idx_leak_meter_id ON leak_alerts(meter_id);
CREATE INDEX IF NOT EXISTS idx_leak_severity ON leak_alerts(severity);
CREATE INDEX IF NOT EXISTS idx_leak_status ON leak_alerts(status);
-- Partial index for open actionable alerts
CREATE INDEX IF NOT EXISTS idx_leak_actionable ON leak_alerts(detected_at DESC)
    WHERE status IN ('NEW', 'ACKNOWLEDGED', 'INVESTIGATING', 'WORK_ORDER_DISPATCHED');

-- App Users
CREATE INDEX IF NOT EXISTS idx_users_role ON app_users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON app_users(status);

-- Maintenance Requests
CREATE INDEX IF NOT EXISTS idx_mr_meter_id ON maintenance_requests(meter_id);
CREATE INDEX IF NOT EXISTS idx_mr_alert_id ON maintenance_requests(alert_id);
CREATE INDEX IF NOT EXISTS idx_mr_location_id ON maintenance_requests(location_id);
CREATE INDEX IF NOT EXISTS idx_mr_technician_id ON maintenance_requests(assigned_technician_id);
CREATE INDEX IF NOT EXISTS idx_mr_status ON maintenance_requests(status);
CREATE INDEX IF NOT EXISTS idx_mr_priority ON maintenance_requests(priority);


-- =============================================================================
-- 2. ANALYTICAL REPORTING VIEWS
-- =============================================================================

-- VIEW 1: v_latest_meter_readings
-- Retrieves the most recent telemetry packet for each deployed water meter
CREATE OR REPLACE VIEW v_latest_meter_readings AS
WITH ranked_readings AS (
    SELECT 
        r.id AS reading_id,
        r.meter_id,
        r.reading_timestamp,
        r.cumulative_reading_m3,
        r.incremental_consumption_m3,
        r.water_flow_rate_lpm,
        r.water_pressure_bar,
        r.water_temperature_celsius,
        r.reading_source,
        r.is_validated,
        ROW_NUMBER() OVER (
            PARTITION BY r.meter_id 
            ORDER BY r.reading_timestamp DESC
        ) AS rank_order
    FROM meter_readings r
)
SELECT 
    m.id AS meter_id,
    m.serial_number,
    m.meter_model,
    m.meter_type,
    m.diameter_mm,
    m.status AS meter_status,
    m.battery_level_percent,
    c.id AS customer_id,
    c.customer_number,
    c.full_name AS customer_name,
    c.customer_type,
    l.id AS location_id,
    l.location_code,
    l.city,
    l.address_line1,
    reg.name AS region_name,
    reg.prefecture,
    rr.reading_id,
    rr.reading_timestamp AS latest_reading_timestamp,
    rr.cumulative_reading_m3,
    rr.incremental_consumption_m3,
    rr.water_flow_rate_lpm,
    rr.water_pressure_bar,
    rr.water_temperature_celsius,
    rr.reading_source,
    rr.is_validated,
    ROUND((EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - rr.reading_timestamp)) / 3600.0)::NUMERIC, 1) AS hours_since_last_reading
FROM water_meters m
JOIN locations l ON m.location_id = l.id
JOIN regions reg ON l.region_id = reg.id
LEFT JOIN customers c ON m.customer_id = c.id
LEFT JOIN ranked_readings rr ON m.id = rr.meter_id AND rr.rank_order = 1;


-- VIEW 2: v_monthly_consumption
-- Monthly consumption aggregates per meter and customer with pressure & flow statistics
CREATE OR REPLACE VIEW v_monthly_consumption AS
SELECT 
    m.id AS meter_id,
    m.serial_number AS meter_serial,
    c.id AS customer_id,
    c.customer_number,
    c.full_name AS customer_name,
    c.customer_type,
    l.location_code,
    reg.name AS region_name,
    EXTRACT(YEAR FROM r.reading_timestamp)::INT AS consumption_year,
    EXTRACT(MONTH FROM r.reading_timestamp)::INT AS consumption_month,
    COUNT(r.id) AS reading_count,
    ROUND(SUM(r.incremental_consumption_m3), 3) AS total_consumption_m3,
    ROUND(AVG(r.water_flow_rate_lpm), 2) AS avg_flow_rate_lpm,
    ROUND(MAX(r.water_flow_rate_lpm), 2) AS peak_flow_rate_lpm,
    ROUND(MIN(r.water_pressure_bar), 2) AS min_pressure_bar,
    ROUND(MAX(r.water_pressure_bar), 2) AS max_pressure_bar,
    ROUND(AVG(r.water_pressure_bar), 2) AS avg_pressure_bar
FROM meter_readings r
JOIN water_meters m ON r.meter_id = m.id
JOIN locations l ON m.location_id = l.id
JOIN regions reg ON l.region_id = reg.id
LEFT JOIN customers c ON m.customer_id = c.id
GROUP BY 
    m.id, 
    m.serial_number, 
    c.id, 
    c.customer_number, 
    c.full_name, 
    c.customer_type, 
    l.location_code, 
    reg.name,
    EXTRACT(YEAR FROM r.reading_timestamp),
    EXTRACT(MONTH FROM r.reading_timestamp);


-- VIEW 3: v_outstanding_bills
-- Tracks unpaid, partially paid, and overdue utility invoices
CREATE OR REPLACE VIEW v_outstanding_bills AS
SELECT 
    b.id AS bill_id,
    b.bill_number,
    b.billing_period_start,
    b.billing_period_end,
    b.consumption_m3,
    b.total_amount AS total_amount_billed,
    COALESCE(SUM(p.amount) FILTER (WHERE p.transaction_status = 'COMPLETED'), 0.00) AS total_amount_paid,
    ROUND((b.total_amount - COALESCE(SUM(p.amount) FILTER (WHERE p.transaction_status = 'COMPLETED'), 0.00)), 2) AS outstanding_balance,
    b.due_date,
    b.status AS bill_status,
    GREATEST(0, (CURRENT_DATE - b.due_date)) AS days_overdue,
    CASE 
        WHEN CURRENT_DATE > b.due_date THEN 'OVERDUE'
        ELSE 'CURRENT'
    END AS collection_aging_status,
    c.id AS customer_id,
    c.customer_number,
    c.full_name AS customer_name,
    c.customer_type,
    c.email AS customer_email,
    c.phone_number AS customer_phone,
    m.id AS meter_id,
    m.serial_number AS meter_serial
FROM water_bills b
JOIN customers c ON b.customer_id = c.id
LEFT JOIN water_meters m ON b.meter_id = m.id
LEFT JOIN payments p ON b.id = p.bill_id
GROUP BY 
    b.id,
    b.bill_number,
    b.billing_period_start,
    b.billing_period_end,
    b.consumption_m3,
    b.total_amount,
    b.due_date,
    b.status,
    c.id,
    c.customer_number,
    c.full_name,
    c.customer_type,
    c.email,
    c.phone_number,
    m.id,
    m.serial_number
HAVING (b.total_amount - COALESCE(SUM(p.amount) FILTER (WHERE p.transaction_status = 'COMPLETED'), 0.00)) > 0.00;
