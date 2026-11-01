-- =============================================================================
-- Script: analytics-examples.sql
-- Description: Advanced PostgreSQL Analytics Suite for Smart Water Management
-- Techniques: JOIN, GROUP BY, HAVING, CTE, Subqueries, LAG, LEAD, Window Functions
-- =============================================================================

\echo '====================================================================='
\echo '  RUNNING ADVANCED SQL ANALYTICS EXAMPLES'
\echo '====================================================================='

-- -----------------------------------------------------------------------------
-- 1. MULTI-TABLE RELATIONAL JOIN
-- Customer Asset Portfolio: Combines Customer, Meter, Location, Region, and Bills
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 1. MULTI-TABLE RELATIONAL JOIN (Customer Asset & Meter Portfolio)'
SELECT 
    c.customer_number,
    c.full_name AS customer_name,
    c.customer_type,
    m.serial_number AS meter_serial,
    m.meter_model,
    m.diameter_mm,
    l.location_code,
    l.city,
    r.name AS region_name,
    COUNT(b.id) AS total_bills_generated,
    COALESCE(SUM(b.total_amount), 0.00) AS lifetime_billed_jpy
FROM customers c
JOIN water_meters m ON m.customer_id = c.id
JOIN locations l ON m.location_id = l.id
JOIN regions r ON l.region_id = r.id
LEFT JOIN water_bills b ON b.customer_id = c.id
GROUP BY 
    c.customer_number, 
    c.full_name, 
    c.customer_type, 
    m.serial_number, 
    m.meter_model, 
    m.diameter_mm, 
    l.location_code, 
    l.city, 
    r.name
ORDER BY lifetime_billed_jpy DESC;


-- -----------------------------------------------------------------------------
-- 2. GROUP BY & HAVING
-- Sector Consumption & Revenue Audit: Filter sectors with > 100 m3 consumption
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 2. GROUP BY & HAVING (High-Volume Regional Sectors)'
SELECT 
    r.code AS region_code,
    r.name AS region_name,
    r.prefecture,
    COUNT(DISTINCT m.id) AS active_meters,
    COUNT(DISTINCT c.id) AS served_customers,
    ROUND(SUM(b.consumption_m3), 2) AS total_billed_consumption_m3,
    ROUND(AVG(b.volumetric_rate), 2) AS avg_tariff_rate_jpy,
    ROUND(SUM(b.total_amount), 2) AS total_billed_revenue_jpy
FROM regions r
JOIN locations l ON l.region_id = r.id
JOIN water_meters m ON m.location_id = l.id
JOIN customers c ON m.customer_id = c.id
JOIN water_bills b ON b.customer_id = c.id
GROUP BY r.code, r.name, r.prefecture
HAVING SUM(b.consumption_m3) > 100.00
ORDER BY total_billed_consumption_m3 DESC;


-- -----------------------------------------------------------------------------
-- 3. COMMON TABLE EXPRESSIONS (CTE)
-- District Metered Area (DMA) Balance & Non-Revenue Water (NRW) Estimation
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 3. COMMON TABLE EXPRESSIONS (CTE: District Water Balance & Revenue Audit)'
WITH RegionalBilledVolume AS (
    SELECT 
        l.region_id,
        SUM(b.consumption_m3) AS total_customer_consumption_m3,
        SUM(b.total_amount) AS total_revenue_jpy
    FROM water_bills b
    JOIN customers c ON b.customer_id = c.id
    JOIN water_meters m ON b.meter_id = m.id
    JOIN locations l ON m.location_id = l.id
    WHERE b.billing_period_start >= '2026-10-01'
    GROUP BY l.region_id
),
RegionalAlertLoss AS (
    SELECT 
        l.region_id,
        COUNT(a.id) AS total_leak_alerts,
        COALESCE(SUM(a.estimated_loss_rate_m3_h * 24), 0.00) AS daily_leak_loss_m3
    FROM locations l
    LEFT JOIN leak_alerts a ON a.location_id = l.id
    GROUP BY l.region_id
)
SELECT 
    r.name AS region_name,
    r.prefecture,
    COALESCE(rb.total_customer_consumption_m3, 0.00) AS billed_consumption_m3,
    COALESCE(rb.total_revenue_jpy, 0.00) AS billed_revenue_jpy,
    COALESCE(ra.total_leak_alerts, 0) AS total_active_alerts,
    ROUND(COALESCE(ra.daily_leak_loss_m3, 0.00), 2) AS estimated_daily_leak_loss_m3,
    ROUND((COALESCE(ra.daily_leak_loss_m3 * 30, 0.00) / 
          NULLIF(COALESCE(rb.total_customer_consumption_m3, 0.00) + COALESCE(ra.daily_leak_loss_m3 * 30, 0.00), 0) * 100)::NUMERIC, 2) AS estimated_nrw_loss_percentage
FROM regions r
LEFT JOIN RegionalBilledVolume rb ON r.id = rb.region_id
LEFT JOIN RegionalAlertLoss ra ON r.id = ra.region_id
ORDER BY billed_consumption_m3 DESC;


-- -----------------------------------------------------------------------------
-- 4. SUBQUERIES (Scalar & Correlated)
-- A) Customers consuming above the utility-wide corporate/commercial average
-- B) Correlated EXISTS: Locations with active work orders and unresolved alerts
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 4A. SCALAR SUBQUERY (Accounts Exceeding Overall Average Consumption)'
SELECT 
    c.customer_number,
    c.full_name AS customer_name,
    c.customer_type,
    b.bill_number,
    b.consumption_m3,
    ROUND((SELECT AVG(consumption_m3) FROM water_bills WHERE billing_period_start = '2026-10-01'), 2) AS benchmark_avg_m3,
    ROUND(b.consumption_m3 - (SELECT AVG(consumption_m3) FROM water_bills WHERE billing_period_start = '2026-10-01'), 2) AS excess_above_avg_m3
FROM water_bills b
JOIN customers c ON b.customer_id = c.id
WHERE b.billing_period_start = '2026-10-01'
  AND b.consumption_m3 > (SELECT AVG(consumption_m3) FROM water_bills WHERE billing_period_start = '2026-10-01')
ORDER BY b.consumption_m3 DESC;

\echo ''
\echo '>>> 4B. CORRELATED SUBQUERY WITH EXISTS (Locations with Correlated Work Orders)'
SELECT 
    l.location_code,
    l.address_line1,
    l.city,
    a.alert_code,
    a.severity,
    a.alert_type
FROM locations l
JOIN leak_alerts a ON a.location_id = l.id
WHERE EXISTS (
    SELECT 1 
    FROM maintenance_requests mr 
    WHERE mr.location_id = l.id 
      AND mr.alert_id = a.id
      AND mr.status IN ('OPEN', 'ASSIGNED', 'IN_PROGRESS')
);


-- -----------------------------------------------------------------------------
-- 5. WINDOW FUNCTION: LAG()
-- Consecutive Reading Delta, Flow Spike & Consumption Velocity Detection
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 5. WINDOW FUNCTION LAG() (Consecutive Reading Delta & Time Acceleration)'
SELECT 
    r.meter_id,
    m.serial_number,
    r.reading_timestamp,
    r.cumulative_reading_m3,
    LAG(r.cumulative_reading_m3, 1) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp
    ) AS prev_cumulative_reading_m3,
    ROUND((r.cumulative_reading_m3 - LAG(r.cumulative_reading_m3, 1) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp
    )), 3) AS interval_consumption_delta_m3,
    r.water_flow_rate_lpm,
    ROUND((r.water_flow_rate_lpm - LAG(r.water_flow_rate_lpm, 1) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp
    )), 2) AS flow_rate_acceleration_lpm,
    r.water_pressure_bar
FROM meter_readings r
JOIN water_meters m ON r.meter_id = m.id
WHERE m.serial_number IN ('MTR-TK-889101', 'MTR-TK-889103')
ORDER BY r.meter_id, r.reading_timestamp;


-- -----------------------------------------------------------------------------
-- 6. WINDOW FUNCTION: LEAD()
-- Look-Ahead Flow Analysis: Compares Current Reading with Upcoming Interval
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 6. WINDOW FUNCTION LEAD() (Forward Telemetry Horizon & Drop Velocity)'
SELECT 
    r.meter_id,
    m.serial_number,
    r.reading_timestamp AS current_timestamp,
    r.water_pressure_bar AS current_pressure_bar,
    LEAD(r.reading_timestamp, 1) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp
    ) AS next_reading_timestamp,
    LEAD(r.water_pressure_bar, 1) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp
    ) AS upcoming_pressure_bar,
    ROUND((LEAD(r.water_pressure_bar, 1) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp
    ) - r.water_pressure_bar), 2) AS projected_pressure_change_bar
FROM meter_readings r
JOIN water_meters m ON r.meter_id = m.id
WHERE m.serial_number = 'MTR-TK-889103'
ORDER BY r.reading_timestamp;


-- -----------------------------------------------------------------------------
-- 7. ADVANCED WINDOW FUNCTIONS: RANK, DENSE_RANK, RUNNING TOTAL & MOVING AVERAGE
-- -----------------------------------------------------------------------------
\echo ''
\echo '>>> 7A. RANK() and DENSE_RANK() (Top Consumer Rankings by Customer Type)'
SELECT 
    c.customer_type,
    c.full_name AS customer_name,
    b.bill_number,
    b.consumption_m3,
    RANK() OVER (
        PARTITION BY c.customer_type 
        ORDER BY b.consumption_m3 DESC
    ) AS rank_within_segment,
    DENSE_RANK() OVER (
        PARTITION BY c.customer_type 
        ORDER BY b.consumption_m3 DESC
    ) AS dense_rank_within_segment
FROM water_bills b
JOIN customers c ON b.customer_id = c.id
WHERE b.billing_period_start = '2026-10-01'
ORDER BY c.customer_type, rank_within_segment;

\echo ''
\echo '>>> 7B. RUNNING TOTAL & 3-PERIOD MOVING AVERAGE PRESSURE'
SELECT 
    r.meter_id,
    m.serial_number,
    r.reading_timestamp,
    r.incremental_consumption_m3,
    -- Cumulative Running Total for the Month
    SUM(r.incremental_consumption_m3) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp 
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS cumulative_running_consumption_m3,
    r.water_pressure_bar,
    -- 3-Point Rolling Centered/Preceding Moving Average for Hydraulic Stability
    ROUND(AVG(r.water_pressure_bar) OVER (
        PARTITION BY r.meter_id 
        ORDER BY r.reading_timestamp 
        ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
    ), 3) AS rolling_3point_avg_pressure_bar
FROM meter_readings r
JOIN water_meters m ON r.meter_id = m.id
WHERE m.serial_number = 'MTR-TK-889101'
ORDER BY r.reading_timestamp;

\echo '====================================================================='
\echo '  ADVANCED ANALYTICS SUITE COMPLETED'
\echo '====================================================================='
