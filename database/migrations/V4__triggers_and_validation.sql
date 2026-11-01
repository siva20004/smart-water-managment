-- =============================================================================
-- Migration: V4__triggers_and_validation.sql
-- Description: Business Rules, Cumulative Reading Sequence Validation, & Audit Triggers
-- Module: 2 - Advanced PostgreSQL Database Engineering
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Function: fn_validate_meter_reading()
-- Enforces monotonic cumulative readings unless flagged as authorized METER_RESET
-- Computes incremental consumption automatically when omitted
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_validate_meter_reading()
RETURNS TRIGGER AS $$
DECLARE
    v_prev_reading RECORD;
BEGIN
    -- Query the chronologically previous reading for this meter
    SELECT id, reading_timestamp, cumulative_reading_m3
    INTO v_prev_reading
    FROM meter_readings
    WHERE meter_id = NEW.meter_id
      AND reading_timestamp < NEW.reading_timestamp
    ORDER BY reading_timestamp DESC
    LIMIT 1;

    IF FOUND THEN
        -- Case 1: Cumulative reading decreased
        IF NEW.cumulative_reading_m3 < v_prev_reading.cumulative_reading_m3 THEN
            -- Check if this was an authorized meter rollover or physical replacement/reset
            IF NEW.validation_notes IS NOT NULL AND (
                NEW.validation_notes ILIKE '%METER_RESET%' OR 
                NEW.validation_notes ILIKE '%ROLLOVER%' OR
                NEW.validation_notes ILIKE '%REPLACEMENT%'
            ) THEN
                -- Authorized reset: accept and set incremental consumption as the new reading baseline
                IF NEW.incremental_consumption_m3 = 0.000 THEN
                    NEW.incremental_consumption_m3 := NEW.cumulative_reading_m3;
                END IF;
            ELSE
                RAISE EXCEPTION 'Integrity Violation: Cumulative meter reading decreased from % m3 to % m3 for Meter ID % at % without authorized METER_RESET flag in validation_notes',
                    v_prev_reading.cumulative_reading_m3, NEW.cumulative_reading_m3, NEW.meter_id, NEW.reading_timestamp
                    USING ERRCODE = 'check_violation';
            END IF;
        ELSE
            -- Normal monotonic reading: compute incremental consumption if default 0.000
            IF NEW.incremental_consumption_m3 = 0.000 THEN
                NEW.incremental_consumption_m3 := ROUND((NEW.cumulative_reading_m3 - v_prev_reading.cumulative_reading_m3), 3);
            END IF;
        END IF;
    ELSE
        -- First initial reading for this meter: incremental is the initial baseline reading
        IF NEW.incremental_consumption_m3 = 0.000 THEN
            NEW.incremental_consumption_m3 := NEW.cumulative_reading_m3;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach trigger to meter_readings table
DROP TRIGGER IF EXISTS trg_validate_meter_reading ON meter_readings;
CREATE TRIGGER trg_validate_meter_reading
    BEFORE INSERT OR UPDATE ON meter_readings
    FOR EACH ROW
    EXECUTE FUNCTION fn_validate_meter_reading();


-- -----------------------------------------------------------------------------
-- Function: fn_update_timestamp()
-- Automatically updates updated_at column on row modifications
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply updated_at trigger across all stateful entities
DROP TRIGGER IF EXISTS trg_regions_updated_at ON regions;
CREATE TRIGGER trg_regions_updated_at BEFORE UPDATE ON regions FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_locations_updated_at ON locations;
CREATE TRIGGER trg_locations_updated_at BEFORE UPDATE ON locations FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_customers_updated_at ON customers;
CREATE TRIGGER trg_customers_updated_at BEFORE UPDATE ON customers FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_meters_updated_at ON water_meters;
CREATE TRIGGER trg_meters_updated_at BEFORE UPDATE ON water_meters FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_bills_updated_at ON water_bills;
CREATE TRIGGER trg_bills_updated_at BEFORE UPDATE ON water_bills FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_alerts_updated_at ON leak_alerts;
CREATE TRIGGER trg_alerts_updated_at BEFORE UPDATE ON leak_alerts FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_users_updated_at ON app_users;
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON app_users FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

DROP TRIGGER IF EXISTS trg_mr_updated_at ON maintenance_requests;
CREATE TRIGGER trg_mr_updated_at BEFORE UPDATE ON maintenance_requests FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
