# System Design Lab — Order & Payment Platform

Phase 1: a production-quality **modular monolith** (DDD + Hexagonal Architecture + PostgreSQL/ACID) for a Global Order & Payment Platform. See [`docs/architecture`](docs/architecture) for the architecture baseline, [`docs/adr`](docs/adr) for the accepted Architecture Decision Records, and [`docs/implementation`](docs/implementation) for the work-package delivery plan.

Phase 1 deliberately excludes Kafka, Redis, microservices, Kubernetes, Saga, CQRS, Event Sourcing and multi-region deployment — see `docs/architecture` §20 (Evolution Triggers) for when each becomes justified.

## Repository layout

```
docs/
  architecture/     Phase-1 HLD/LLD (current: v1.3)
  adr/               ADR-001..023 (current: v1.2)
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

## Current status

Work package **WP-00 (Repository & engineering baseline)** — see `docs/implementation`. Architecture skeleton, module boundaries and fitness tests land in WP-01.
