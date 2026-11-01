-- =============================================================================
-- Migration: V1__initial_schema.sql
-- Description: Baseline schema definition for Smart Water Management System
-- Module: 1 - Project Foundation
-- =============================================================================

-- Enable UUID extension if available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. System Metadata & Configuration
CREATE TABLE IF NOT EXISTS system_metadata (
    id VARCHAR(64) PRIMARY KEY,
    key_name VARCHAR(128) NOT NULL UNIQUE,
    key_value TEXT NOT NULL,
    description TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. Water Utility Facilities / Stations (Foundation schema)
CREATE TABLE IF NOT EXISTS water_facilities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    facility_type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'INACTIVE',
    location_description VARCHAR(255),
    latitude NUMERIC(9, 6),
    longitude NUMERIC(9, 6),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_facilities_status ON water_facilities(status);
CREATE INDEX IF NOT EXISTS idx_facilities_type ON water_facilities(facility_type);

-- Seed minimal system metadata (system info only, no fake telemetry)
INSERT INTO system_metadata (id, key_name, key_value, description)
VALUES 
    ('meta-1', 'system.version', '1.0.0', 'Smart Water Management Core Version'),
    ('meta-2', 'system.metric_unit', 'METRIC_M3', 'Default volume metric unit (m3)'),
    ('meta-3', 'system.telemetry_interval_sec', '60', 'Default sensor telemetry polling interval')
ON CONFLICT (key_name) DO NOTHING;
