-- =============================================================================
-- Database Initialization Script
-- Usage: Run this script as the PostgreSQL superuser (e.g., postgres) to create
-- the dedicated user and database for the Smart Water Management System.
-- =============================================================================

-- 1. Create database user if it does not already exist
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'water_admin') THEN

      CREATE ROLE water_admin WITH LOGIN PASSWORD 'water_secure_password';
   END IF;
END
$do$;

-- 2. Create database owned by water_admin
SELECT 'CREATE DATABASE smart_water_management WITH OWNER water_admin ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'smart_water_management')\gexec

-- 3. Grant privileges
GRANT ALL PRIVILEGES ON DATABASE smart_water_management TO water_admin;
