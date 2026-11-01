-- =============================================================================
-- Migration: V5__schema_alignments_and_roles.sql
-- Description: Expand constraints for Modules 4-12 and seed primary role test accounts
-- [SIMULATED DATASET: Educational Simulation Only]
-- =============================================================================

-- 1. App Users role constraint expansion
ALTER TABLE app_users DROP CONSTRAINT IF EXISTS chk_users_role;
ALTER TABLE app_users ADD CONSTRAINT chk_users_role
    CHECK (role IN ('ADMIN', 'OPERATOR', 'TECHNICIAN', 'ANALYST', 'UTILITY_ENGINEER', 'FIELD_TECHNICIAN', 'BILLING_OFFICER', 'AUDITOR', 'CUSTOMER'));

-- 2. Customer type and status constraint expansion
ALTER TABLE customers DROP CONSTRAINT IF EXISTS chk_customers_type;
ALTER TABLE customers ADD CONSTRAINT chk_customers_type
    CHECK (customer_type IN ('INDIVIDUAL', 'CORPORATE', 'GOVERNMENT', 'RESIDENTIAL', 'COMMERCIAL', 'INDUSTRIAL'));

ALTER TABLE customers DROP CONSTRAINT IF EXISTS chk_customers_status;
ALTER TABLE customers ADD CONSTRAINT chk_customers_status
    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED', 'INACTIVE'));

-- 3. Water Meter status constraint expansion
ALTER TABLE water_meters DROP CONSTRAINT IF EXISTS chk_meters_status;
ALTER TABLE water_meters ADD CONSTRAINT chk_meters_status
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'FAULTY', 'RETIRED', 'REMOVED'));
ALTER TABLE water_meters ADD COLUMN IF NOT EXISTS last_communication_at TIMESTAMPTZ;

-- 4. Water Bills status constraint expansion
ALTER TABLE water_bills DROP CONSTRAINT IF EXISTS chk_bills_status;
ALTER TABLE water_bills ADD CONSTRAINT chk_bills_status
    CHECK (status IN ('ISSUED', 'PARTIALLY_PAID', 'PAID', 'UNPAID', 'OVERDUE', 'CANCELLED', 'DISPUTED'));

-- 5. Maintenance Requests status constraint expansion
ALTER TABLE maintenance_requests DROP CONSTRAINT IF EXISTS chk_mr_status;
ALTER TABLE maintenance_requests ADD CONSTRAINT chk_mr_status
    CHECK (status IN ('PENDING', 'OPEN', 'ASSIGNED', 'IN_PROGRESS', 'PENDING_PARTS', 'COMPLETED', 'CANCELLED'));

-- 6. Insert primary role accounts with BCrypt-hashed password ('Password123!')
INSERT INTO app_users (username, email, password_hash, full_name, role, department, status)
VALUES
    ('admin', 'admin@veloia.water.sim', '$2a$10$XEzNUPQHezjFva4LEELSjun0mfiMWRpSrOj7jAJiVtK8X3gzcTnVq', 'System Administrator [SIMULATED]', 'ADMIN', 'Executive IT & Operations', 'ACTIVE'),
    ('operator', 'operator@veloia.water.sim', '$2a$10$XEzNUPQHezjFva4LEELSjun0mfiMWRpSrOj7jAJiVtK8X3gzcTnVq', 'Field & Billing Operator [SIMULATED]', 'OPERATOR', 'Customer & Network Operations', 'ACTIVE'),
    ('technician', 'technician@veloia.water.sim', '$2a$10$XEzNUPQHezjFva4LEELSjun0mfiMWRpSrOj7jAJiVtK8X3gzcTnVq', 'Rapid Response Technician [SIMULATED]', 'TECHNICIAN', 'Field Maintenance & Diagnostics', 'ACTIVE'),
    ('analyst', 'analyst@veloia.water.sim', '$2a$10$XEzNUPQHezjFva4LEELSjun0mfiMWRpSrOj7jAJiVtK8X3gzcTnVq', 'Utility Analytics Officer [SIMULATED]', 'ANALYST', 'Hydraulic Planning & Analytics', 'ACTIVE')
ON CONFLICT (username) DO UPDATE
    SET password_hash = EXCLUDED.password_hash,
        role = EXCLUDED.role,
        status = 'ACTIVE';
