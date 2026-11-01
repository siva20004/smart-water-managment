-- =============================================================================
-- Migration: V2__smart_water_schema.sql
-- Description: Core Relational Database Schema for Smart Water Management System
-- Module: 2 - Advanced PostgreSQL Database Engineering
-- Target Database: PostgreSQL 14+ (tested on PostgreSQL 16 & 18)
-- =============================================================================

-- 1. REGIONS
-- Administrative utility sectors, prefectures, or operational districts
CREATE TABLE IF NOT EXISTS regions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    prefecture VARCHAR(64) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_regions_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'PLANNED'))
);

-- 2. LOCATIONS
-- Physical installation points, facility coordinates, and addresses
CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    region_id BIGINT NOT NULL REFERENCES regions(id) ON DELETE RESTRICT,
    location_code VARCHAR(64) NOT NULL UNIQUE,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    elevation_meters NUMERIC(6, 2),
    installation_type VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_locations_type CHECK (installation_type IN ('RESIDENTIAL', 'COMMERCIAL', 'INDUSTRIAL', 'PUBLIC_MUNICIPAL', 'TREATMENT_PLANT', 'PUMP_STATION')),
    CONSTRAINT chk_locations_latitude CHECK (latitude BETWEEN -90.0 AND 90.0),
    CONSTRAINT chk_locations_longitude CHECK (longitude BETWEEN -180.0 AND 180.0)
);

-- 3. CUSTOMERS
-- Water utility account holders, corporations, and municipal bodies
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_number VARCHAR(32) NOT NULL UNIQUE,
    customer_type VARCHAR(32) NOT NULL,
    full_name VARCHAR(128) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(32),
    tax_id VARCHAR(64) UNIQUE,
    billing_address VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_customers_type CHECK (customer_type IN ('INDIVIDUAL', 'CORPORATE', 'GOVERNMENT')),
    CONSTRAINT chk_customers_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED'))
);

-- 4. WATER METERS
-- Physical and smart AMI water measurement instruments
-- Supports multiple meters per customer; attached to a physical location
CREATE TABLE IF NOT EXISTS water_meters (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    serial_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id BIGINT REFERENCES customers(id) ON DELETE SET NULL,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    meter_model VARCHAR(64) NOT NULL,
    meter_type VARCHAR(32) NOT NULL,
    diameter_mm INTEGER NOT NULL,
    installation_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_inspection_date DATE,
    battery_level_percent NUMERIC(5, 2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_meters_type CHECK (meter_type IN ('ULTRASONIC', 'ELECTROMAGNETIC', 'MECHANICAL_PISTON', 'OPTICAL_DIGITAL')),
    CONSTRAINT chk_meters_diameter CHECK (diameter_mm > 0 AND diameter_mm <= 1000),
    CONSTRAINT chk_meters_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'FAULTY', 'RETIRED')),
    CONSTRAINT chk_meters_battery CHECK (battery_level_percent IS NULL OR (battery_level_percent >= 0.00 AND battery_level_percent <= 100.00))
);

-- 5. METER READINGS
-- Timeseries telemetry and periodic cumulative readings
-- Strictly prevents duplicate meter readings for the same meter and timestamp
CREATE TABLE IF NOT EXISTS meter_readings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    meter_id BIGINT NOT NULL REFERENCES water_meters(id) ON DELETE CASCADE,
    reading_timestamp TIMESTAMPTZ NOT NULL,
    cumulative_reading_m3 NUMERIC(12, 3) NOT NULL,
    incremental_consumption_m3 NUMERIC(10, 3) NOT NULL DEFAULT 0.000,
    water_flow_rate_lpm NUMERIC(8, 2),
    water_pressure_bar NUMERIC(5, 2),
    water_temperature_celsius NUMERIC(4, 1),
    reading_source VARCHAR(32) NOT NULL DEFAULT 'AMI_AUTOMATED',
    is_validated BOOLEAN NOT NULL DEFAULT TRUE,
    validation_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_meter_reading_timestamp UNIQUE (meter_id, reading_timestamp),
    CONSTRAINT chk_readings_cumulative CHECK (cumulative_reading_m3 >= 0.000),
    CONSTRAINT chk_readings_incremental CHECK (incremental_consumption_m3 >= 0.000),
    CONSTRAINT chk_readings_flow CHECK (water_flow_rate_lpm IS NULL OR water_flow_rate_lpm >= 0.00),
    CONSTRAINT chk_readings_pressure CHECK (water_pressure_bar IS NULL OR (water_pressure_bar >= 0.00 AND water_pressure_bar <= 25.00)),
    CONSTRAINT chk_readings_temperature CHECK (water_temperature_celsius IS NULL OR (water_temperature_celsius >= -10.0 AND water_temperature_celsius <= 60.0)),
    CONSTRAINT chk_readings_source CHECK (reading_source IN ('AMI_AUTOMATED', 'AMR_DRIVE_BY', 'MANUAL_INSPECTION', 'ESTIMATED'))
);

-- 6. WATER BILLS
-- Generated customer consumption utility invoices
CREATE TABLE IF NOT EXISTS water_bills (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bill_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    meter_id BIGINT REFERENCES water_meters(id) ON DELETE SET NULL,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    base_fee NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    volumetric_rate NUMERIC(10, 4) NOT NULL DEFAULT 0.0000,
    consumption_m3 NUMERIC(10, 3) NOT NULL,
    consumption_charge NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    tax_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    total_amount NUMERIC(12, 2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ISSUED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bills_period CHECK (billing_period_end >= billing_period_start),
    CONSTRAINT chk_bills_base_fee CHECK (base_fee >= 0.00),
    CONSTRAINT chk_bills_volumetric_rate CHECK (volumetric_rate >= 0.0000),
    CONSTRAINT chk_bills_consumption CHECK (consumption_m3 >= 0.000),
    CONSTRAINT chk_bills_charge CHECK (consumption_charge >= 0.00),
    CONSTRAINT chk_bills_tax CHECK (tax_amount >= 0.00),
    CONSTRAINT chk_bills_total CHECK (total_amount >= 0.00),
    CONSTRAINT chk_bills_status CHECK (status IN ('ISSUED', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED', 'DISPUTED'))
);

-- 7. PAYMENTS
-- Customer remittances and transactions (supports multiple payments per bill)
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    payment_reference VARCHAR(64) NOT NULL UNIQUE,
    bill_id BIGINT NOT NULL REFERENCES water_bills(id) ON DELETE RESTRICT,
    payment_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(32) NOT NULL,
    transaction_status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_payments_amount CHECK (amount > 0.00),
    CONSTRAINT chk_payments_method CHECK (payment_method IN ('BANK_TRANSFER', 'DIRECT_DEBIT', 'CREDIT_CARD', 'CONVENIENCE_STORE_PAY', 'ELECTRONIC_WALLET')),
    CONSTRAINT chk_payments_status CHECK (transaction_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED'))
);

-- 8. WATER QUALITY RECORDS
-- Physical-chemical lab sample telemetry (supports multiple samples per location)
CREATE TABLE IF NOT EXISTS water_quality_records (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sample_code VARCHAR(64) NOT NULL UNIQUE,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    sample_timestamp TIMESTAMPTZ NOT NULL,
    ph_level NUMERIC(4, 2) NOT NULL,
    turbidity_ntu NUMERIC(6, 3) NOT NULL,
    residual_chlorine_mg_l NUMERIC(5, 3) NOT NULL,
    dissolved_oxygen_mg_l NUMERIC(5, 2),
    conductivity_us_cm NUMERIC(7, 2),
    water_temperature_celsius NUMERIC(4, 1),
    potability_status VARCHAR(32) NOT NULL DEFAULT 'POTABLE',
    sampled_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_wqr_ph CHECK (ph_level BETWEEN 0.00 AND 14.00),
    CONSTRAINT chk_wqr_turbidity CHECK (turbidity_ntu >= 0.000),
    CONSTRAINT chk_wqr_chlorine CHECK (residual_chlorine_mg_l >= 0.000),
    CONSTRAINT chk_wqr_oxygen CHECK (dissolved_oxygen_mg_l IS NULL OR dissolved_oxygen_mg_l >= 0.00),
    CONSTRAINT chk_wqr_conductivity CHECK (conductivity_us_cm IS NULL OR conductivity_us_cm >= 0.00),
    CONSTRAINT chk_wqr_temperature CHECK (water_temperature_celsius IS NULL OR (water_temperature_celsius >= -5.0 AND water_temperature_celsius <= 50.0)),
    CONSTRAINT chk_wqr_status CHECK (potability_status IN ('POTABLE', 'POTABLE_MONITORING_REQUIRED', 'NON_POTABLE_ALERT'))
);

-- 9. LEAK ALERTS
-- Anomalous events triggered by continuous flow, pressure drop, or acoustics
CREATE TABLE IF NOT EXISTS leak_alerts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    alert_code VARCHAR(64) NOT NULL UNIQUE,
    meter_id BIGINT REFERENCES water_meters(id) ON DELETE SET NULL,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    severity VARCHAR(32) NOT NULL,
    alert_type VARCHAR(64) NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_loss_rate_m3_h NUMERIC(8, 3),
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    resolution_notes TEXT,
    resolved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_leak_severity CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_leak_type CHECK (alert_type IN ('CONTINUOUS_FLOW', 'PRESSURE_DROP', 'ACOUSTIC_LEAK_SIGNATURE', 'METER_TAMPER', 'BURST_PIPE_SUSPECTED')),
    CONSTRAINT chk_leak_loss_rate CHECK (estimated_loss_rate_m3_h IS NULL OR estimated_loss_rate_m3_h >= 0.000),
    CONSTRAINT chk_leak_status CHECK (status IN ('NEW', 'ACKNOWLEDGED', 'INVESTIGATING', 'WORK_ORDER_DISPATCHED', 'RESOLVED', 'FALSE_POSITIVE'))
);

-- 10. APP USERS
-- Internal security identity management (passwords stored as BCrypt hashes)
CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL,
    department VARCHAR(64),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'UTILITY_ENGINEER', 'FIELD_TECHNICIAN', 'BILLING_OFFICER', 'AUDITOR', 'CUSTOMER')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DEACTIVATED'))
);

-- 11. MAINTENANCE REQUESTS
-- Work orders connected to water meters, leak alerts, and locations
CREATE TABLE IF NOT EXISTS maintenance_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_number VARCHAR(64) NOT NULL UNIQUE,
    meter_id BIGINT REFERENCES water_meters(id) ON DELETE SET NULL,
    alert_id BIGINT REFERENCES leak_alerts(id) ON DELETE SET NULL,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    assigned_technician_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL,
    priority VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
    category VARCHAR(64) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    scheduled_start TIMESTAMPTZ,
    actual_completion TIMESTAMPTZ,
    resolution_summary TEXT,
    parts_cost NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    labor_hours NUMERIC(5, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_mr_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'EMERGENCY')),
    CONSTRAINT chk_mr_category CHECK (category IN ('LEAK_REPAIR', 'METER_REPLACEMENT', 'CALIBRATION', 'PRESSURE_CHECK', 'WATER_QUALITY_RETEST', 'ROUTINE_INSPECTION')),
    CONSTRAINT chk_mr_status CHECK (status IN ('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'PENDING_PARTS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_mr_parts_cost CHECK (parts_cost >= 0.00),
    CONSTRAINT chk_mr_labor_hours CHECK (labor_hours >= 0.00)
);
