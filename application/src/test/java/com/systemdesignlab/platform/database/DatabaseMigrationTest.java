package com.systemdesignlab.platform.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.systemdesignlab.platform.TestcontainersConfiguration;

/**
 * WP-02 database foundation: proves Flyway migrates a real, empty PostgreSQL
 * Testcontainers instance from zero and that every module schema exists afterwards
 * (ADR-004, ADR-009, ADR-012; Architecture v1.2 SS6a, SS13, SS14).
 *
 * <p>{@link TestcontainersConfiguration} starts a fresh {@code postgres:16-alpine}
 * container per test-context, so context startup itself is the "migrate from an empty
 * database" scenario: if Flyway did not run cleanly, {@code @SpringBootTest} context
 * startup would fail before any {@code @Test} method executes.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DatabaseMigrationTest {

    private static final Set<String> EXPECTED_MODULE_SCHEMAS =
            Set.of("customer", "catalog", "cart", "ordering", "inventory", "payment");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    @Test
    void migratesCleanlyAndValidatesWithNoPendingMigrations() {
        flyway.validate();

        List<MigrationInfo> applied = List.of(flyway.info().applied());
        assertThat(applied).hasSize(2);
        assertThat(applied).allSatisfy(info -> assertThat(info.getState().isApplied()).isTrue());

        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    void createsExactlyTheExpectedModuleSchemas() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> allSchemas =
                jdbcTemplate.queryForList("SELECT schema_name FROM information_schema.schemata", String.class);

        assertThat(allSchemas).containsAll(EXPECTED_MODULE_SCHEMAS);
        // "order" (unquoted) is a reserved keyword; confirm it was deliberately not used.
        assertThat(allSchemas).doesNotContain("order");
    }
}
