# System Design Lab — Order & Payment Platform

Phase 1: a production-quality **modular monolith** (DDD + Hexagonal Architecture + PostgreSQL/ACID) for a Global Order & Payment Platform. See [`docs/architecture`](docs/architecture) for the architecture baseline, [`docs/adr`](docs/adr) for the accepted Architecture Decision Records, and [`docs/implementation`](docs/implementation) for the work-package delivery plan.

Phase 1 deliberately excludes Kafka, Redis, microservices, Kubernetes, Saga, CQRS, Event Sourcing and multi-region deployment — see `docs/architecture` §20 (Evolution Triggers) for when each becomes justified.

## Repository layout

```
docs/
  architecture/     Phase-1 HLD/LLD (current: v1.2)
  adr/               ADR-001..022 (current: v1.1)
  implementation/    Work-package delivery plan (current: v1.0)
application/         Spring Boot / Gradle application (Java 21+)
database/migrations/ Flyway migrations, one file set per module schema
docker/              docker-compose.yml for local PostgreSQL
```

## Prerequisites

- JDK 21+ (the Gradle toolchain will provision 21 automatically if not present)
- Docker (for local PostgreSQL and for Testcontainers-based tests)

## Running locally

Start PostgreSQL:

```bash
docker compose -f docker/docker-compose.yml up -d
```

Run the application against it:

```bash
cd application
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Check health (should report the datasource as `UP`):

```bash
curl http://localhost:8080/actuator/health
```

## Running tests

Tests use Testcontainers to spin up a real PostgreSQL instance automatically — no manual `docker compose` step required:

```bash
cd application
./gradlew test
```

## Database foundation (WP-02)

Flyway is the sole authoritative mechanism for schema evolution (ADR-009); Hibernate/JPA
never creates or modifies schema (`spring.jpa.hibernate.ddl-auto=none` in every profile).

- **Migrations** live in [`database/migrations`](database/migrations), versioned
  (`V001__...`, `V002__...`). Gradle's `processResources` task copies them into the
  classpath at build time so Flyway's default `classpath:db/migration` location finds
  them without moving the canonical files into the application module.
- **Module schemas**: `customer`, `catalog`, `cart`, `ordering`, `inventory`, `payment`
  — one per bounded context (ADR-018). The Order module's schema is named `ordering`,
  not `order`, solely to avoid the `ORDER` SQL reserved-keyword collision; this is a
  disclosed naming deviation from the literal text of ADR-018 / Architecture v1.2 §6a,
  not a change to the isolation model itself.
- **Role/ownership model**: each module has its own least-privilege PostgreSQL login
  role, granted `USAGE, CREATE` on its own schema only (`V002__create_module_roles_and_grants.sql`).
  Cross-schema access is denied at the database level. As of WP-02 no persistence
  adapter exists yet, so the running application still connects with a single
  migration/admin credential; per-module runtime connection routing is deferred to the
  work package that introduces each module's first persistence adapter. See
  `ModuleSchemaIsolationTest` for exactly what this does and does not guarantee.
- **Testing**: `DatabaseMigrationTest` and `ModuleSchemaIsolationTest` under
  `com.systemdesignlab.platform.database` use Testcontainers PostgreSQL (never H2) to
  prove migration-from-zero and schema-level grant isolation against a real database.

## Current status

Work package **WP-02 (Database foundation)** — see `docs/implementation`. Architecture
skeleton, module boundaries and fitness tests landed in WP-01; repository/engineering
baseline landed in WP-00.
