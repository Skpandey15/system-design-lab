-- WP-02: Database Foundation
-- Least-privilege, per-module database roles (ADR-018; Architecture v1.2 SS6a, SS17).
--
-- Each bounded context gets its own login role, granted USAGE + CREATE on its own
-- schema only. No role is granted any privilege on any other module's schema, and the
-- default per-schema privilege PUBLIC would otherwise receive is revoked first, so
-- cross-schema access is denied by the database itself -- not merely by application
-- convention (see ModuleSchemaIsolationTest, which proves this with real connections).
--
-- Phase-1 scope note: WP-02 is infrastructure-only and no persistence adapter exists
-- yet, so the running Spring application still connects with the single migration/admin
-- credential configured for its datasource (see application-dev.yml / Testcontainers).
-- These roles are provisioned and grant-tested now so the DB-level boundary ADR-018
-- requires is real and verifiable today; routing each module's future persistence
-- adapter to authenticate as its own role is deferred to the work package that
-- introduces that adapter (see WP-02 report for the recommended follow-up).
--
-- Credentials below are local-development/test-only placeholders, safe to keep in
-- version control (ephemeral Testcontainers databases and local Docker Compose only).
-- They must never be reused for any non-development environment.

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'customer_app') THEN
        CREATE ROLE customer_app LOGIN PASSWORD 'customer_app_dev_only_pw'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'catalog_app') THEN
        CREATE ROLE catalog_app LOGIN PASSWORD 'catalog_app_dev_only_pw'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'cart_app') THEN
        CREATE ROLE cart_app LOGIN PASSWORD 'cart_app_dev_only_pw'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'ordering_app') THEN
        CREATE ROLE ordering_app LOGIN PASSWORD 'ordering_app_dev_only_pw'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'inventory_app') THEN
        CREATE ROLE inventory_app LOGIN PASSWORD 'inventory_app_dev_only_pw'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'payment_app') THEN
        CREATE ROLE payment_app LOGIN PASSWORD 'payment_app_dev_only_pw'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
    END IF;
END
$$;

REVOKE ALL ON SCHEMA customer FROM PUBLIC;
REVOKE ALL ON SCHEMA catalog FROM PUBLIC;
REVOKE ALL ON SCHEMA cart FROM PUBLIC;
REVOKE ALL ON SCHEMA ordering FROM PUBLIC;
REVOKE ALL ON SCHEMA inventory FROM PUBLIC;
REVOKE ALL ON SCHEMA payment FROM PUBLIC;

GRANT USAGE, CREATE ON SCHEMA customer TO customer_app;
GRANT USAGE, CREATE ON SCHEMA catalog TO catalog_app;
GRANT USAGE, CREATE ON SCHEMA cart TO cart_app;
GRANT USAGE, CREATE ON SCHEMA ordering TO ordering_app;
GRANT USAGE, CREATE ON SCHEMA inventory TO inventory_app;
GRANT USAGE, CREATE ON SCHEMA payment TO payment_app;
