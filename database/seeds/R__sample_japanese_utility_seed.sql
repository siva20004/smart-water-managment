-- =============================================================================
-- Migration: R__sample_japanese_utility_seed.sql
-- Description: Realistic Seed Dataset for Simulated Japanese Water Utility
-- Disclaimer: [SIMULATED DATASET: Tokyo-Kanto Metropolitan Simulation - Educational Use Only]
-- All data is completely synthetic and generated for simulation testing.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. REGIONS (Simulated Japanese Metropolitan Utility Sectors)
-- -----------------------------------------------------------------------------
INSERT INTO regions (code, name, prefecture, description, status)
VALUES
    ('REG-TK-CENTRAL', 'Tokyo Central Chiyoda Sector [SIMULATED]', 'Tokyo', 'Metropolitan administrative, governmental, and commercial hub distribution sector', 'ACTIVE'),
    ('REG-TK-SHIN', 'Tokyo West Shinjuku High-Density Sector [SIMULATED]', 'Tokyo', 'High-density commercial skyscrapers and residential mixed distribution zone', 'ACTIVE'),
    ('REG-TK-MINATO', 'Tokyo Minato Bayside Maritime Sector [SIMULATED]', 'Tokyo', 'Coastal commercial, residential waterfront, and logistics port sector', 'ACTIVE'),
    ('REG-KN-YOKO', 'Kanagawa Yokohama Port Industrial Sector [SIMULATED]', 'Kanagawa', 'Heavy industrial, manufacturing, and port water infrastructure network', 'ACTIVE'),
    ('REG-OS-KITA', 'Osaka Kita Municipal Grid Sector [SIMULATED]', 'Osaka', 'Urban commercial center and multi-unit residential utility district', 'ACTIVE')
ON CONFLICT (code) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 2. LOCATIONS (Simulated Japanese Coordinates & Addresses)
-- -----------------------------------------------------------------------------
INSERT INTO locations (region_id, location_code, address_line1, address_line2, city, postal_code, latitude, longitude, elevation_meters, installation_type)
VALUES
    ((SELECT id FROM regions WHERE code = 'REG-TK-SHIN'), 'LOC-TK-SHIN-001', '2-8-1 Nishi-Shinjuku', 'Tokyo Gov Tower North Basement B2', 'Shinjuku-ku', '163-8001', 35.6895000, 139.6917000, 42.50, 'PUBLIC_MUNICIPAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-SHIN'), 'LOC-TK-SHIN-002', '3-20-2 Nishi-Shinjuku', 'Tokyo Opera City Tower Plant Room 1', 'Shinjuku-ku', '163-1407', 35.6861000, 139.6864000, 38.20, 'COMMERCIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-SHIN'), 'LOC-TK-SHIN-003', '4-15-7 Kabukicho', 'Central Commercial Plaza B1', 'Shinjuku-ku', '160-0021', 35.6953000, 139.7028000, 34.10, 'COMMERCIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-SHIN'), 'LOC-TK-SHIN-004', '1-2-12 Kita-Shinjuku', 'Shinjuku Garden Residence Apt 402', 'Shinjuku-ku', '169-0074', 35.7021000, 139.6950000, 31.80, 'RESIDENTIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-MINATO'), 'LOC-TK-MIN-001', '1-1-1 Roppongi', 'Roppongi Grand Tower Utility Intake', 'Minato-ku', '106-0032', 35.6645000, 139.7389000, 26.40, 'COMMERCIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-MINATO'), 'LOC-TK-MIN-002', '3-2-5 Shibaura', 'Shibaura Waterfront Canal Residenza A', 'Minato-ku', '108-0023', 35.6428000, 139.7523000, 4.20, 'RESIDENTIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-CENTRAL'), 'LOC-TK-CHI-001', '1-1 Chiyoda', 'Imperial Plaza East Gate Water Station', 'Chiyoda-ku', '100-8111', 35.6852000, 139.7528000, 18.90, 'PUBLIC_MUNICIPAL'),
    ((SELECT id FROM regions WHERE code = 'REG-TK-CENTRAL'), 'LOC-TK-CHI-002', '2-3-1 Marunouchi', 'Marunouchi Financial Center Sub-level', 'Chiyoda-ku', '100-6390', 35.6798000, 139.7639000, 12.00, 'COMMERCIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-KN-YOKO'), 'LOC-KN-YOK-001', '1-1-1 Minatomirai', 'Yokohama Landmark Tower Utility Vault', 'Yokohama-shi Nishi-ku', '220-8172', 35.4549000, 139.6314000, 5.80, 'COMMERCIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-KN-YOKO'), 'LOC-KN-YOK-002', '2-12 Daikoku-futo', 'Yokohama Marine Terminal Industrial Line', 'Yokohama-shi Tsurumi-ku', '230-0054', 35.4612000, 139.6798000, 3.10, 'INDUSTRIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-OS-KITA'), 'LOC-OS-KIT-001', '3-1-3 Umeda', 'Osaka Station City South Building Vault', 'Osaka-shi Kita-ku', '530-0001', 34.7024000, 135.4959000, 8.40, 'COMMERCIAL'),
    ((SELECT id FROM regions WHERE code = 'REG-OS-KITA'), 'LOC-OS-KIT-002', '1-4-10 Nakanoshima', 'Nakanoshima Riverfront Center Level B1', 'Osaka-shi Kita-ku', '530-0005', 34.6931000, 135.4982000, 6.20, 'COMMERCIAL')
ON CONFLICT (location_code) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 3. CUSTOMERS (Simulated Japanese Accounts)
-- -----------------------------------------------------------------------------
INSERT INTO customers (customer_number, customer_type, full_name, email, phone_number, tax_id, billing_address, status)
VALUES
    ('CUST-TK-2026-0001', 'INDIVIDUAL', 'Tanaka Kenji [SIMULATED]', 'tanaka.kenji.simulated@tokyo-water.jp.example', '03-5321-1111', 'JP-TAX-IND-881920', '1-2-12-402 Kita-Shinjuku, Shinjuku-ku, Tokyo', 'ACTIVE'),
    ('CUST-TK-2026-0002', 'INDIVIDUAL', 'Sato Yoko [SIMULATED]', 'sato.yoko.simulated@tokyo-water.jp.example', '03-5321-2222', 'JP-TAX-IND-881921', '3-2-5 Shibaura, Minato-ku, Tokyo', 'ACTIVE'),
    ('CUST-TK-2026-0003', 'CORPORATE', 'Tokyo Precision Dynamics KK [SIMULATED]', 'facility@tokyo-precision.example.jp', '03-3344-5566', 'JP-CORP-9012345678901', '2-8-1 Nishi-Shinjuku, Shinjuku-ku, Tokyo', 'ACTIVE'),
    ('CUST-TK-2026-0004', 'CORPORATE', 'Nippon Clean Tech Industries Ltd [SIMULATED]', 'operations@nippon-cleantech.example.jp', '045-222-3344', 'JP-CORP-9012345678902', '2-12 Daikoku-futo, Tsurumi-ku, Yokohama', 'ACTIVE'),
    ('CUST-TK-2026-0005', 'GOVERNMENT', 'Tokyo Metropolitan Facility Bureau [SIMULATED]', 'water.admin@metro.tokyo.example.jp', '03-5320-7777', 'JP-GOV-1000000000001', '2-8-1 Nishi-Shinjuku, Shinjuku-ku, Tokyo', 'ACTIVE'),
    ('CUST-TK-2026-0006', 'CORPORATE', 'Shinjuku Central Plaza Hospitality KK [SIMULATED]', 'billing@shinjuku-plaza.example.jp', '03-3200-9988', 'JP-CORP-9012345678903', '4-15-7 Kabukicho, Shinjuku-ku, Tokyo', 'ACTIVE'),
    ('CUST-TK-2026-0007', 'CORPORATE', 'Marunouchi Financial Tower Operations [SIMULATED]', 'facilities@marunouchi-tower.example.jp', '03-3211-4433', 'JP-CORP-9012345678904', '2-3-1 Marunouchi, Chiyoda-ku, Tokyo', 'ACTIVE'),
    ('CUST-TK-2026-0008', 'CORPORATE', 'Yokohama Landmark Asset Trust [SIMULATED]', 'infrastructure@landmark-trust.example.jp', '045-222-8899', 'JP-CORP-9012345678905', '1-1-1 Minatomirai, Nishi-ku, Yokohama', 'ACTIVE'),
    ('CUST-TK-2026-0009', 'CORPORATE', 'Osaka Umeda Rail Commercial Terminal [SIMULATED]', 'water@osaka-station.example.jp', '06-6345-1234', 'JP-CORP-9012345678906', '3-1-3 Umeda, Kita-ku, Osaka', 'ACTIVE'),
    ('CUST-TK-2026-0010', 'INDIVIDUAL', 'Yamamoto Daisuke [SIMULATED]', 'yamamoto.daisuke.sim@tokyo-water.jp.example', '03-5432-8877', 'JP-TAX-IND-881925', '3-2-5 Shibaura Residenza Apt 1204, Minato-ku, Tokyo', 'ACTIVE')
ON CONFLICT (customer_number) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 4. WATER METERS (Multiple meters per customer; tied to physical locations)
-- -----------------------------------------------------------------------------
INSERT INTO water_meters (serial_number, customer_id, location_id, meter_model, meter_type, diameter_mm, installation_date, status, last_inspection_date, battery_level_percent)
VALUES
    ('MTR-TK-889101', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0001'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-004'), 'Aichi Tokei SU20 Smart Ultra', 'ULTRASONIC', 20, '2024-04-15', 'ACTIVE', '2026-04-15', 94.50),
    ('MTR-TK-889102', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0002'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-MIN-002'), 'Aichi Tokei SU20 Smart Ultra', 'ULTRASONIC', 20, '2024-05-10', 'ACTIVE', '2026-05-10', 92.00),
    ('MTR-TK-889103', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0003'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-001'), 'Azbil MagneW 3000 PLUS Flowmeter', 'ELECTROMAGNETIC', 100, '2023-01-20', 'ACTIVE', '2026-06-01', 99.00),
    ('MTR-TK-889104', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0003'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-002'), 'Aichi Tokei AT25 High-Flow', 'MECHANICAL_PISTON', 50, '2023-06-15', 'ACTIVE', '2026-06-15', 88.50),
    ('MTR-TK-889105', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0004'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-002'), 'Azbil MagneW 3000 Heavy Industrial', 'ELECTROMAGNETIC', 150, '2022-11-10', 'ACTIVE', '2026-05-20', 97.20),
    ('MTR-TK-889106', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0005'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-CHI-001'), 'Kamstrup Multical 21 Ultrasonic', 'ULTRASONIC', 50, '2024-02-01', 'ACTIVE', '2026-02-01', 96.00),
    ('MTR-TK-889107', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0006'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-003'), 'Aichi Tokei SU40 Commercial', 'ULTRASONIC', 40, '2024-08-12', 'ACTIVE', '2026-08-12', 91.80),
    ('MTR-TK-889108', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0007'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-CHI-002'), 'Yokogawa ADMAG AXG Electromagnetic', 'ELECTROMAGNETIC', 80, '2023-09-05', 'ACTIVE', '2026-03-10', 98.40),
    ('MTR-TK-889109', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0008'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-001'), 'Yokogawa ADMAG AXG High-Rise', 'ELECTROMAGNETIC', 100, '2023-04-18', 'ACTIVE', '2026-04-18', 95.00),
    ('MTR-TK-889110', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0009'), (SELECT id FROM locations WHERE location_code = 'LOC-OS-KIT-001'), 'Azbil MagneW Multi-Zone', 'ELECTROMAGNETIC', 100, '2023-07-22', 'ACTIVE', '2026-07-22', 93.60),
    ('MTR-TK-889111', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0010'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-MIN-002'), 'Aichi Tokei SU20 Smart Ultra', 'ULTRASONIC', 20, '2024-10-01', 'ACTIVE', '2026-10-01', 99.50),
    ('MTR-TK-889112', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0004'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-002'), 'Aichi Tokei Backup Cooling Line', 'OPTICAL_DIGITAL', 50, '2024-01-15', 'ACTIVE', '2026-01-15', 89.00)
ON CONFLICT (serial_number) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 5. METER READINGS (Timeseries packets with flow, pressure, temperature)
-- -----------------------------------------------------------------------------
INSERT INTO meter_readings (meter_id, reading_timestamp, cumulative_reading_m3, incremental_consumption_m3, water_flow_rate_lpm, water_pressure_bar, water_temperature_celsius, reading_source, is_validated, validation_notes)
VALUES
    -- Residential Meter MTR-TK-889101 (Tanaka Kenji)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-01 00:00:00+09', 145.200, 0.000, 0.00, 3.25, 18.2, 'AMI_AUTOMATED', TRUE, 'Baseline verified [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-01 06:00:00+09', 145.320, 0.120, 3.50, 3.20, 18.1, 'AMI_AUTOMATED', TRUE, 'Morning peak [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-01 12:00:00+09', 145.450, 0.130, 2.10, 3.30, 18.5, 'AMI_AUTOMATED', TRUE, 'Midday usage [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-01 18:00:00+09', 145.750, 0.300, 8.40, 3.15, 18.4, 'AMI_AUTOMATED', TRUE, 'Evening bath / kitchen [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-02 00:00:00+09', 145.820, 0.070, 0.00, 3.35, 18.0, 'AMI_AUTOMATED', TRUE, 'Night minimum flow [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-15 00:00:00+09', 151.350, 5.530, 1.20, 3.22, 17.8, 'AMI_AUTOMATED', TRUE, 'Mid-month check [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-31 23:59:59+09', 158.400, 7.050, 0.00, 3.28, 17.5, 'AMI_AUTOMATED', TRUE, 'Monthly billing closure [SIMULATED]'),

    -- Residential Meter MTR-TK-889102 (Sato Yoko)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889102'), '2026-10-01 00:00:00+09', 88.400, 0.000, 0.00, 3.10, 18.0, 'AMI_AUTOMATED', TRUE, 'Baseline reading [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889102'), '2026-10-01 08:00:00+09', 88.580, 0.180, 5.20, 3.05, 18.1, 'AMI_AUTOMATED', TRUE, 'Morning normal [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889102'), '2026-10-15 00:00:00+09', 93.900, 5.320, 0.50, 3.12, 17.9, 'AMI_AUTOMATED', TRUE, 'Mid-month reading [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889102'), '2026-10-31 23:59:59+09', 99.850, 5.950, 0.00, 3.15, 17.3, 'AMI_AUTOMATED', TRUE, 'Monthly closure [SIMULATED]'),

    -- Commercial Industrial Meter MTR-TK-889103 (Tokyo Precision Dynamics)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-10-01 00:00:00+09', 14200.000, 0.000, 45.00, 4.80, 19.1, 'AMI_AUTOMATED', TRUE, 'Industrial continuous baseline [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-10-01 12:00:00+09', 14285.500, 85.500, 120.00, 4.60, 19.4, 'AMI_AUTOMATED', TRUE, 'Shift 1 production cycle [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-10-02 00:00:00+09', 14370.200, 84.700, 60.00, 4.85, 18.9, 'AMI_AUTOMATED', TRUE, 'Shift 2 cooling load [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-10-15 00:00:00+09', 15450.000, 1079.800, 85.00, 4.70, 18.5, 'AMI_AUTOMATED', TRUE, 'Mid-cycle volume check [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-10-31 23:59:59+09', 16720.500, 1270.500, 40.00, 4.75, 17.8, 'AMI_AUTOMATED', TRUE, 'Monthly industrial closure [SIMULATED]'),

    -- Industrial Heavy Line MTR-TK-889105 (Nippon Clean Tech Yokohama)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), '2026-10-01 00:00:00+09', 42800.000, 0.000, 95.00, 5.20, 18.8, 'AMI_AUTOMATED', TRUE, 'Baseline heavy line [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), '2026-10-01 12:00:00+09', 42980.000, 180.000, 240.00, 4.95, 19.0, 'AMI_AUTOMATED', TRUE, 'Treatment chemical wash [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), '2026-10-15 00:00:00+09', 45100.000, 2120.000, 150.00, 5.10, 18.2, 'AMI_AUTOMATED', TRUE, 'Mid-month audit [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), '2026-10-31 23:59:59+09', 47650.000, 2550.000, 110.00, 5.15, 17.6, 'AMI_AUTOMATED', TRUE, 'Monthly audit closure [SIMULATED]'),

    -- Municipal Facility MTR-TK-889106 (Tokyo Metropolitan Government Station)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889106'), '2026-10-01 00:00:00+09', 3120.000, 0.000, 10.00, 4.10, 18.0, 'AMI_AUTOMATED', TRUE, 'Government facility baseline [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889106'), '2026-10-15 00:00:00+09', 3380.000, 260.000, 22.00, 4.05, 18.1, 'AMI_AUTOMATED', TRUE, 'Mid-month municipal [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889106'), '2026-10-31 23:59:59+09', 3650.000, 270.000, 12.00, 4.15, 17.4, 'AMI_AUTOMATED', TRUE, 'Municipal month close [SIMULATED]'),

    -- Commercial Hospitality MTR-TK-889107 (Kabukicho Plaza)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), '2026-10-01 00:00:00+09', 1980.000, 0.000, 15.00, 3.80, 18.4, 'AMI_AUTOMATED', TRUE, 'Commercial initial [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), '2026-10-15 00:00:00+09', 2210.000, 230.000, 35.00, 3.75, 18.2, 'AMI_AUTOMATED', TRUE, 'Commercial mid check [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), '2026-10-31 23:59:59+09', 2490.000, 280.000, 18.00, 3.85, 17.6, 'AMI_AUTOMATED', TRUE, 'Commercial close [SIMULATED]'),

    -- Landmark High-Rise MTR-TK-889109 (Yokohama Landmark Trust)
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889109'), '2026-10-01 00:00:00+09', 8400.000, 0.000, 28.00, 4.50, 18.2, 'AMI_AUTOMATED', TRUE, 'Landmark initial [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889109'), '2026-10-15 00:00:00+09', 8850.000, 450.000, 42.00, 4.40, 18.0, 'AMI_AUTOMATED', TRUE, 'Landmark mid [SIMULATED]'),
    ((SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889109'), '2026-10-31 23:59:59+09', 9320.000, 470.000, 30.00, 4.45, 17.5, 'AMI_AUTOMATED', TRUE, 'Landmark close [SIMULATED]')
ON CONFLICT (meter_id, reading_timestamp) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 6. WATER BILLS (Calculated utility charges in JPY simulation)
-- -----------------------------------------------------------------------------
INSERT INTO water_bills (bill_number, customer_id, meter_id, billing_period_start, billing_period_end, base_fee, volumetric_rate, consumption_m3, consumption_charge, tax_amount, total_amount, due_date, status)
VALUES
    ('BILL-202609-0001', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0001'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-09-01', '2026-09-30', 1200.00, 180.0000, 12.800, 2304.00, 350.40, 3854.40, '2026-10-25', 'PAID'),
    ('BILL-202610-0001', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0001'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), '2026-10-01', '2026-10-31', 1200.00, 180.0000, 13.200, 2376.00, 357.60, 3933.60, '2026-11-25', 'ISSUED'),
    ('BILL-202609-0002', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0002'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889102'), '2026-09-01', '2026-09-30', 1200.00, 180.0000, 11.200, 2016.00, 321.60, 3537.60, '2026-10-25', 'PAID'),
    ('BILL-202610-0002', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0002'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889102'), '2026-10-01', '2026-10-31', 1200.00, 180.0000, 11.450, 2061.00, 326.10, 3587.10, '2026-11-25', 'ISSUED'),
    ('BILL-202609-0003', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0003'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-09-01', '2026-09-30', 15000.00, 240.0000, 2450.000, 588000.00, 60300.00, 663300.00, '2026-10-25', 'PAID'),
    ('BILL-202610-0003', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0003'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889103'), '2026-10-01', '2026-10-31', 15000.00, 240.0000, 2520.500, 604920.00, 61992.00, 681912.00, '2026-11-25', 'PARTIALLY_PAID'),
    ('BILL-202609-0004', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0004'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), '2026-09-01', '2026-09-30', 25000.00, 220.0000, 4600.000, 1012000.00, 103700.00, 1140700.00, '2026-10-25', 'PAID'),
    ('BILL-202610-0004', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0004'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), '2026-10-01', '2026-10-31', 25000.00, 220.0000, 4850.000, 1067000.00, 109200.00, 1201200.00, '2026-11-25', 'ISSUED'),
    ('BILL-202609-0005', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0005'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889106'), '2026-09-01', '2026-09-30', 5000.00, 200.0000, 520.000, 104000.00, 10900.00, 119900.00, '2026-10-25', 'PAID'),
    ('BILL-202610-0005', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0005'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889106'), '2026-10-01', '2026-10-31', 5000.00, 200.0000, 530.000, 106000.00, 11100.00, 122100.00, '2026-11-25', 'ISSUED'),
    ('BILL-202608-0006', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0006'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), '2026-08-01', '2026-08-31', 4000.00, 210.0000, 490.000, 102900.00, 10690.00, 117590.00, '2026-09-25', 'OVERDUE'),
    ('BILL-202609-0006', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0006'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), '2026-09-01', '2026-09-30', 4000.00, 210.0000, 500.000, 105000.00, 10900.00, 119900.00, '2026-10-25', 'OVERDUE'),
    ('BILL-202610-0006', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0006'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), '2026-10-01', '2026-10-31', 4000.00, 210.0000, 510.000, 107100.00, 11110.00, 122210.00, '2026-11-25', 'ISSUED'),
    ('BILL-202610-0007', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0007'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889108'), '2026-10-01', '2026-10-31', 8000.00, 220.0000, 850.000, 187000.00, 19500.00, 214500.00, '2026-11-25', 'ISSUED'),
    ('BILL-202610-0008', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0008'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889109'), '2026-10-01', '2026-10-31', 10000.00, 225.0000, 920.000, 207000.00, 21700.00, 238700.00, '2026-11-25', 'ISSUED'),
    ('BILL-202610-0009', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0009'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889110'), '2026-10-01', '2026-10-31', 12000.00, 230.0000, 1150.000, 264500.00, 27650.00, 304150.00, '2026-11-25', 'PAID'),
    ('BILL-202610-0010', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0010'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889111'), '2026-10-01', '2026-10-31', 1200.00, 180.0000, 9.500, 1710.00, 291.00, 3201.00, '2026-11-25', 'ISSUED'),
    ('BILL-202609-0011', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0003'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889104'), '2026-09-01', '2026-09-30', 5000.00, 210.0000, 380.000, 79800.00, 8480.00, 93280.00, '2026-10-25', 'PAID'),
    ('BILL-202610-0011', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0003'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889104'), '2026-10-01', '2026-10-31', 5000.00, 210.0000, 395.000, 82950.00, 8795.00, 96745.00, '2026-11-25', 'ISSUED'),
    ('BILL-202610-0012', (SELECT id FROM customers WHERE customer_number = 'CUST-TK-2026-0004'), (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889112'), '2026-10-01', '2026-10-31', 6000.00, 215.0000, 420.000, 90300.00, 9630.00, 105930.00, '2026-11-25', 'ISSUED')
ON CONFLICT (bill_number) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 7. PAYMENTS (Multiple payments per bill supported)
-- -----------------------------------------------------------------------------
INSERT INTO payments (payment_reference, bill_id, payment_date, amount, payment_method, transaction_status, notes)
VALUES
    ('PAY-202610-0001', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202609-0001'), '2026-10-18 10:15:00+09', 3854.40, 'DIRECT_DEBIT', 'COMPLETED', 'Automatic bank draft [SIMULATED]'),
    ('PAY-202610-0002', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202609-0002'), '2026-10-20 14:30:00+09', 3537.60, 'CONVENIENCE_STORE_PAY', 'COMPLETED', 'Lawson store terminal payment [SIMULATED]'),
    ('PAY-202610-0003', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202609-0003'), '2026-10-22 09:00:00+09', 663300.00, 'BANK_TRANSFER', 'COMPLETED', 'Mizuho corporate electronic transfer [SIMULATED]'),
    ('PAY-202610-0004', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202609-0004'), '2026-10-24 16:45:00+09', 1140700.00, 'BANK_TRANSFER', 'COMPLETED', 'MUFG commercial wire [SIMULATED]'),
    ('PAY-202610-0005', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202609-0005'), '2026-10-25 11:20:00+09', 119900.00, 'BANK_TRANSFER', 'COMPLETED', 'Municipal inter-agency wire [SIMULATED]'),
    ('PAY-202610-0006', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202610-0009'), '2026-11-01 10:00:00+09', 304150.00, 'CREDIT_CARD', 'COMPLETED', 'Corporate visa settlement [SIMULATED]'),
    -- Partial payments on BILL-202610-0003 (Total: 681,912.00 JPY)
    ('PAY-202611-0001', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202610-0003'), '2026-11-01 15:30:00+09', 300000.00, 'BANK_TRANSFER', 'COMPLETED', 'Installment 1 of 2 [SIMULATED]'),
    ('PAY-202611-0002', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202610-0003'), '2026-11-01 17:00:00+09', 100000.00, 'BANK_TRANSFER', 'COMPLETED', 'Installment 2 partial [SIMULATED]'),
    -- Additional completed payments
    ('PAY-202610-0007', (SELECT id FROM water_bills WHERE bill_number = 'BILL-202609-0011'), '2026-10-22 09:05:00+09', 93280.00, 'BANK_TRANSFER', 'COMPLETED', 'Sub-meter settlement [SIMULATED]')
ON CONFLICT (payment_reference) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 8. WATER QUALITY RECORDS (Simulated Japanese Drinking Water Quality Standards)
-- Standards: pH 5.8-8.6, Turbidity < 2 NTU, Residual Chlorine >= 0.1 mg/L
-- -----------------------------------------------------------------------------
INSERT INTO water_quality_records (sample_code, location_id, sample_timestamp, ph_level, turbidity_ntu, residual_chlorine_mg_l, dissolved_oxygen_mg_l, conductivity_us_cm, water_temperature_celsius, potability_status, sampled_by)
VALUES
    ('WQS-202610-001', (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-001'), '2026-10-05 08:30:00+09', 7.40, 0.120, 0.450, 9.20, 185.00, 17.5, 'POTABLE', 'S. Takahashi (Lab Tech 4) [SIMULATED]'),
    ('WQS-202610-002', (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-002'), '2026-10-05 09:45:00+09', 7.35, 0.150, 0.420, 9.10, 190.50, 17.8, 'POTABLE', 'S. Takahashi (Lab Tech 4) [SIMULATED]'),
    ('WQS-202610-003', (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-003'), '2026-10-05 11:15:00+09', 7.20, 0.380, 0.310, 8.80, 210.00, 18.2, 'POTABLE', 'S. Takahashi (Lab Tech 4) [SIMULATED]'),
    ('WQS-202610-004', (SELECT id FROM locations WHERE location_code = 'LOC-TK-MIN-001'), '2026-10-06 08:30:00+09', 7.50, 0.090, 0.500, 9.40, 178.00, 17.2, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]'),
    ('WQS-202610-005', (SELECT id FROM locations WHERE location_code = 'LOC-TK-MIN-002'), '2026-10-06 10:00:00+09', 7.42, 0.110, 0.480, 9.30, 182.00, 17.4, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]'),
    ('WQS-202610-006', (SELECT id FROM locations WHERE location_code = 'LOC-TK-CHI-001'), '2026-10-07 09:00:00+09', 7.60, 0.080, 0.550, 9.60, 172.00, 16.9, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]'),
    ('WQS-202610-007', (SELECT id FROM locations WHERE location_code = 'LOC-TK-CHI-002'), '2026-10-07 10:30:00+09', 7.55, 0.095, 0.520, 9.50, 175.50, 17.0, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]'),
    ('WQS-202610-008', (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-001'), '2026-10-08 09:15:00+09', 7.30, 0.220, 0.380, 8.90, 205.00, 18.0, 'POTABLE', 'M. Ito (Senior Analyst) [SIMULATED]'),
    ('WQS-202610-009', (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-002'), '2026-10-08 11:30:00+09', 7.15, 0.650, 0.280, 8.50, 245.00, 18.6, 'POTABLE', 'M. Ito (Senior Analyst) [SIMULATED]'),
    ('WQS-202610-010', (SELECT id FROM locations WHERE location_code = 'LOC-OS-KIT-001'), '2026-10-09 09:30:00+09', 7.38, 0.140, 0.440, 9.00, 195.00, 17.7, 'POTABLE', 'H. Nakamura (Quality Lead) [SIMULATED]'),
    ('WQS-202610-011', (SELECT id FROM locations WHERE location_code = 'LOC-OS-KIT-002'), '2026-10-09 11:00:00+09', 7.45, 0.130, 0.460, 9.15, 192.00, 17.5, 'POTABLE', 'H. Nakamura (Quality Lead) [SIMULATED]'),
    ('WQS-202610-012', (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-003'), '2026-10-20 14:00:00+09', 6.95, 1.450, 0.180, 7.80, 310.00, 19.5, 'POTABLE_MONITORING_REQUIRED', 'S. Takahashi (Lab Tech 4) [SIMULATED]'),
    ('WQS-202610-013', (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-002'), '2026-10-22 10:15:00+09', 7.10, 0.580, 0.300, 8.60, 240.00, 18.4, 'POTABLE', 'M. Ito (Senior Analyst) [SIMULATED]'),
    ('WQS-202610-014', (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-001'), '2026-10-28 08:30:00+09', 7.42, 0.110, 0.460, 9.30, 184.00, 17.3, 'POTABLE', 'S. Takahashi (Lab Tech 4) [SIMULATED]'),
    ('WQS-202610-015', (SELECT id FROM locations WHERE location_code = 'LOC-TK-MIN-001'), '2026-10-28 10:00:00+09', 7.48, 0.085, 0.510, 9.45, 176.00, 17.1, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]'),
    ('WQS-202610-016', (SELECT id FROM locations WHERE location_code = 'LOC-TK-CHI-001'), '2026-10-29 09:00:00+09', 7.58, 0.075, 0.560, 9.65, 170.00, 16.8, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]'),
    ('WQS-202610-017', (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-001'), '2026-10-29 11:00:00+09', 7.32, 0.190, 0.400, 9.05, 201.00, 17.9, 'POTABLE', 'M. Ito (Senior Analyst) [SIMULATED]'),
    ('WQS-202610-018', (SELECT id FROM locations WHERE location_code = 'LOC-OS-KIT-001'), '2026-10-30 09:30:00+09', 7.40, 0.135, 0.450, 9.10, 193.00, 17.6, 'POTABLE', 'H. Nakamura (Quality Lead) [SIMULATED]'),
    ('WQS-202610-019', (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-004'), '2026-10-30 14:00:00+09', 7.38, 0.120, 0.440, 9.25, 188.00, 17.4, 'POTABLE', 'S. Takahashi (Lab Tech 4) [SIMULATED]'),
    ('WQS-202610-020', (SELECT id FROM locations WHERE location_code = 'LOC-TK-MIN-002'), '2026-10-31 10:30:00+09', 7.44, 0.105, 0.490, 9.35, 180.00, 17.2, 'POTABLE', 'K. Watanabe (Chief Chemist) [SIMULATED]')
ON CONFLICT (sample_code) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 9. LEAK ALERTS (Simulated IoT & SCADA anomaly detections)
-- -----------------------------------------------------------------------------
INSERT INTO leak_alerts (alert_code, meter_id, location_id, severity, alert_type, detected_at, estimated_loss_rate_m3_h, status, resolution_notes, resolved_at)
VALUES
    ('ALK-202610-0001', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-003'), 'HIGH', 'CONTINUOUS_FLOW', '2026-10-18 03:15:00+09', 1.850, 'WORK_ORDER_DISPATCHED', 'Night minimum flow did not drop below 18 LPM [SIMULATED]', NULL),
    ('ALK-202610-0002', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-002'), 'MEDIUM', 'PRESSURE_DROP', '2026-10-21 14:22:00+09', 0.650, 'INVESTIGATING', 'Secondary valve pressure dropped 0.8 bar [SIMULATED]', NULL),
    ('ALK-202610-0003', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889104'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-002'), 'CRITICAL', 'BURST_PIPE_SUSPECTED', '2026-10-24 02:40:00+09', 4.500, 'RESOLVED', 'Joint gasket ruptured; isolated and replaced [SIMULATED]', '2026-10-24 07:15:00+09'),
    ('ALK-202610-0004', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-004'), 'LOW', 'CONTINUOUS_FLOW', '2026-10-27 04:00:00+09', 0.080, 'ACKNOWLEDGED', 'Minor internal plumbing fixture trickle suspected [SIMULATED]', NULL),
    ('ALK-202610-0005', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889109'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-001'), 'MEDIUM', 'ACOUSTIC_LEAK_SIGNATURE', '2026-10-29 01:10:00+09', 0.420, 'NEW', 'Acoustic logger detected 1.2 kHz correlation peak [SIMULATED]', NULL)
ON CONFLICT (alert_code) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 10. APP USERS (Security identities with BCrypt hashes - NO plain text)
-- Password for all seed users is '$2a$12$K8j7mZq01...' (simulated bcrypt hash)
-- -----------------------------------------------------------------------------
INSERT INTO app_users (username, email, password_hash, full_name, role, department, status)
VALUES
    ('admin_yamada', 'yamada.admin.sim@water.metro.tokyo.jp', '$2a$12$e80yq7zJ1kXF4qWc4Q8iYeN2qN3mJ7kF9aL8yU6oP1bZ5xC3vB7mK', 'Yamada Hiroshi [SIMULATED]', 'ADMIN', 'Information Systems Dept', 'ACTIVE'),
    ('engineer_suzuki', 'suzuki.eng.sim@water.metro.tokyo.jp', '$2a$12$e80yq7zJ1kXF4qWc4Q8iYeN2qN3mJ7kF9aL8yU6oP1bZ5xC3vB7mK', 'Suzuki Kenichi [SIMULATED]', 'UTILITY_ENGINEER', 'Network Hydraulics Engineering', 'ACTIVE'),
    ('tech_kato', 'kato.field.sim@water.metro.tokyo.jp', '$2a$12$e80yq7zJ1kXF4qWc4Q8iYeN2qN3mJ7kF9aL8yU6oP1bZ5xC3vB7mK', 'Kato Ryu [SIMULATED]', 'FIELD_TECHNICIAN', 'Emergency Maintenance Div', 'ACTIVE'),
    ('tech_matsuda', 'matsuda.field.sim@water.metro.tokyo.jp', '$2a$12$e80yq7zJ1kXF4qWc4Q8iYeN2qN3mJ7kF9aL8yU6oP1bZ5xC3vB7mK', 'Matsuda Taro [SIMULATED]', 'FIELD_TECHNICIAN', 'Acoustic Leak Detection Team', 'ACTIVE'),
    ('billing_ogawa', 'ogawa.billing.sim@water.metro.tokyo.jp', '$2a$12$e80yq7zJ1kXF4qWc4Q8iYeN2qN3mJ7kF9aL8yU6oP1bZ5xC3vB7mK', 'Ogawa Mai [SIMULATED]', 'BILLING_OFFICER', 'Customer Tariff & Revenue Dept', 'ACTIVE'),
    ('auditor_honda', 'honda.audit.sim@water.metro.tokyo.jp', '$2a$12$e80yq7zJ1kXF4qWc4Q8iYeN2qN3mJ7kF9aL8yU6oP1bZ5xC3vB7mK', 'Honda Kazuki [SIMULATED]', 'AUDITOR', 'Water Quality Compliance Board', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 11. MAINTENANCE REQUESTS (Connected to meters and leak alerts)
-- -----------------------------------------------------------------------------
INSERT INTO maintenance_requests (request_number, meter_id, alert_id, location_id, assigned_technician_id, priority, category, title, description, status, scheduled_start, actual_completion, resolution_summary, parts_cost, labor_hours)
VALUES
    ('WO-202610-001', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889104'), (SELECT id FROM leak_alerts WHERE alert_code = 'ALK-202610-0003'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-002'), (SELECT id FROM app_users WHERE username = 'tech_kato'), 'EMERGENCY', 'LEAK_REPAIR', 'Emergency Flange Gasket Replacement [SIMULATED]', 'High-pressure bypass valve burst at 02:40. Isolated line and replaced high-tensile rubber gasket.', 'COMPLETED', '2026-10-24 03:30:00+09', '2026-10-24 07:15:00+09', 'Replaced 50mm EPDM gasket and torqued bolts to 85 Nm. Pressure restored to 4.8 bar.', 45000.00, 3.75),
    ('WO-202610-002', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889107'), (SELECT id FROM leak_alerts WHERE alert_code = 'ALK-202610-0001'), (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-003'), (SELECT id FROM app_users WHERE username = 'tech_matsuda'), 'HIGH', 'LEAK_REPAIR', 'Kabukicho Commercial Vault Acoustic Inspection [SIMULATED]', 'Continuous night-flow alert flagged 1.85 m3/h baseline flow. Inspect internal sub-meter branches.', 'IN_PROGRESS', '2026-10-28 09:00:00+09', NULL, 'Ground microphone confirms sound signature at branch 2B. Excavation permit requested.', 12000.00, 5.50),
    ('WO-202610-003', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889105'), (SELECT id FROM leak_alerts WHERE alert_code = 'ALK-202610-0002'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-002'), (SELECT id FROM app_users WHERE username = 'tech_kato'), 'MEDIUM', 'PRESSURE_CHECK', 'Yokohama Daikoku Pressure Regulator Inspection [SIMULATED]', 'Pressure oscillation between 4.1 and 5.2 bar detected by AMI telemetry. Calibrate PRV pilot valve.', 'ASSIGNED', '2026-11-03 10:00:00+09', NULL, NULL, 0.00, 0.00),
    ('WO-202610-004', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889109'), (SELECT id FROM leak_alerts WHERE alert_code = 'ALK-202610-0005'), (SELECT id FROM locations WHERE location_code = 'LOC-KN-YOK-001'), (SELECT id FROM app_users WHERE username = 'tech_matsuda'), 'MEDIUM', 'ROUTINE_INSPECTION', 'Acoustic Correlation Pinpointing at Minatomirai [SIMULATED]', 'Confirm whether 1.2 kHz correlation peak represents municipal main or HVAC cooling tower return.', 'OPEN', '2026-11-04 13:30:00+09', NULL, NULL, 0.00, 0.00),
    ('WO-202610-005', (SELECT id FROM water_meters WHERE serial_number = 'MTR-TK-889101'), NULL, (SELECT id FROM locations WHERE location_code = 'LOC-TK-SHIN-004'), (SELECT id FROM app_users WHERE username = 'tech_matsuda'), 'LOW', 'CALIBRATION', 'Annual Ultrasonic Transducer Verification [SIMULATED]', 'Routine verification of pulse travel time and battery health on Aichi Tokei SU20.', 'COMPLETED', '2026-10-15 11:00:00+09', '2026-10-15 11:45:00+09', 'Transducer signals optimal. Zero flow calibrated. Battery reading 94.5%.', 0.00, 0.75)
ON CONFLICT (request_number) DO NOTHING;
