-- WP-02: Database Foundation
-- Creates one PostgreSQL schema per bounded context (ADR-004, ADR-009, ADR-018;
-- Architecture v1.2 SS6a, SS12, SS13). This migration is infrastructure-only: no
-- business tables are created here. Each module's own tables land in that module's
-- future migrations, in the work package that introduces its first persistence adapter.
--
-- Naming note (disclosed deviation -- see WP-02 PR description / report):
-- Architecture v1.2 SS6a/SS12 and ADR-018 literally name the Order module's schema
-- "order". This migration instead uses "ordering", because ORDER is a reserved SQL
-- keyword (ORDER BY) and an unquoted "order" schema forces every reference anywhere
-- in the codebase and in ad-hoc SQL to be double-quoted ("order".table) or it fails to
-- parse. This is a naming-only deviation: the schema-per-module isolation model,
-- ownership and grant boundaries required by ADR-018 are otherwise implemented exactly
-- as decided. Recommend a short ADR-018 addendum (or superseding record) formally
-- renaming "order" to "ordering" in the architecture documents.
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS catalog;
CREATE SCHEMA IF NOT EXISTS cart;
CREATE SCHEMA IF NOT EXISTS ordering;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS payment;
