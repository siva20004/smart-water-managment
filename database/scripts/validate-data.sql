-- =============================================================================
-- Script: validate-data.sql
-- Description: Automated Data Integrity & Water Utility Business Rules Validator
-- Target: PostgreSQL 14+ / Database: smart_water_management
-- Usage: psql -U postgres -d smart_water_management -f database/scripts/validate-data.sql
-- =============================================================================

\echo '====================================================================='
\echo '  STARTING DATA INTEGRITY & AUDIT VALIDATION SUITE'
\echo '  Smart Water Management System (Simulated Japanese Utility)'
\echo '====================================================================='

-- TEST 1: Check for duplicate timestamps on any water meter
\echo '---------------------------------------------------------------------'
\echo 'Test 1: Duplicate Meter Readings per Meter and Timestamp'
SELECT 
    meter_id, 
    reading_timestamp, 
    COUNT(*) AS duplicate_count
FROM meter_readings
GROUP BY meter_id, reading_timestamp
HAVING COUNT(*) > 1;

-- TEST 2: Sequence check for unflagged decreasing cumulative readings
\echo '---------------------------------------------------------------------'
\echo 'Test 2: Decreasing Cumulative Readings (Without METER_RESET Flag)'
WITH ordered_readings AS (
    SELECT 
        meter_id,
        reading_timestamp,
        cumulative_reading_m3,
        validation_notes,
        LAG(cumulative_reading_m3) OVER (
            PARTITION BY meter_id 
            ORDER BY reading_timestamp
        ) AS prev_cumulative_m3
    FROM meter_readings
)
SELECT 
    meter_id,
    reading_timestamp,
    prev_cumulative_m3,
    cumulative_reading_m3,
    (cumulative_reading_m3 - prev_cumulative_m3) AS delta_m3,
    validation_notes
FROM ordered_readings
WHERE prev_cumulative_m3 IS NOT NULL 
  AND cumulative_reading_m3 < prev_cumulative_m3
  AND (validation_notes IS NULL OR (
      validation_notes NOT ILIKE '%METER_RESET%' AND 
      validation_notes NOT ILIKE '%ROLLOVER%'
  ));

-- TEST 3: Check for Negative Values or Invalid Constraints
\echo '---------------------------------------------------------------------'
\echo 'Test 3: Negative Values in Consumption or Financial Balances'
SELECT 'Negative Consumption' AS check_type, COUNT(*) AS violations 
FROM meter_readings WHERE incremental_consumption_m3 < 0.000
UNION ALL
SELECT 'Negative Bill Total', COUNT(*) FROM water_bills WHERE total_amount < 0.00
UNION ALL
SELECT 'Negative Payment Amount', COUNT(*) FROM payments WHERE amount <= 0.00
UNION ALL
SELECT 'Negative Water Pressure', COUNT(*) FROM meter_readings WHERE water_pressure_bar < 0.00;

-- TEST 4: Integrity check on bill payments (Detect any overpaid bills)
\echo '---------------------------------------------------------------------'
\echo 'Test 4: Overpaid Bills Audit (Payments Sum > Total Billed)'
SELECT 
    b.id AS bill_id,
    b.bill_number,
    b.total_amount,
    SUM(p.amount) AS total_paid,
    ROUND((SUM(p.amount) - b.total_amount), 2) AS overpaid_excess
FROM water_bills b
JOIN payments p ON b.id = p.bill_id
WHERE p.transaction_status = 'COMPLETED'
GROUP BY b.id, b.bill_number, b.total_amount
HAVING SUM(p.amount) > b.total_amount;

-- TEST 5: Water Quality Compliance Thresholds (Japanese MHLW Standards)
\echo '---------------------------------------------------------------------'
\echo 'Test 5: Water Quality Standard Compliance Inspection'
SELECT 
    sample_code,
    location_id,
    sample_timestamp,
    ph_level,
    turbidity_ntu,
    residual_chlorine_mg_l,
    potability_status,
    CASE
        WHEN ph_level NOT BETWEEN 5.8 AND 8.6 THEN 'pH OUT OF SAFE RANGE (5.8 - 8.6)'
        WHEN turbidity_ntu > 2.000 THEN 'TURBIDITY EXCEEDS 2.0 NTU TARGET'
        WHEN residual_chlorine_mg_l < 0.100 THEN 'FREE CHLORINE BELOW 0.1 mg/L DISINFECTION MINIMUM'
        ELSE 'NORMAL'
    END AS compliance_flag
FROM water_quality_records
WHERE ph_level NOT BETWEEN 5.8 AND 8.6 
   OR turbidity_ntu > 2.000 
   OR residual_chlorine_mg_l < 0.100
   OR potability_status != 'POTABLE';

-- TEST 6: Table Record Counts and Verification Summary
\echo '---------------------------------------------------------------------'
\echo 'Test 6: Database Summary Entity Population Count'
SELECT 
    schemaname,
    relname AS table_name,
    n_live_tup AS approximate_row_count
FROM pg_stat_user_tables
WHERE schemaname = 'public'
ORDER BY relname;

\echo '====================================================================='
\echo '  DATA INTEGRITY SUITE COMPLETED'
\echo '====================================================================='
