package com.systemdesignlab.platform.database;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.systemdesignlab.platform.TestcontainersConfiguration;

/**
 * Proves the ADR-018 / Architecture v1.2 SS6a schema-isolation guarantee at the
 * PostgreSQL level: each module's least-privilege DB role (created by
 * {@code V002__create_module_roles_and_grants.sql}) can operate inside its own schema
 * and is denied, by the database itself, from touching any other module's schema.
 *
 * <p><b>What this actually proves</b> (do not overstate it): PostgreSQL denies
 * cross-schema DDL/DML for a role with no GRANT on that schema. It does not, by
 * itself, stop a future persistence adapter from being miswired to a different role's
 * credentials, and it says nothing about application-level boundaries -- those remain
 * {@link com.systemdesignlab.platform.architecture.ModularMonolithArchitectureTest}'s
 * job. The two mechanisms are complementary: ArchUnit stops the compiled Java code from
 * reaching another module's persistence classes; this test stops a raw SQL/JDBC
 * connection using a module's role from reaching another module's schema even if the
 * code-level rule were somehow bypassed.
 *
 * <p>Phase-1 scope note: no persistence adapter exists yet (WP-02 is
 * infrastructure-only), so today nothing in the running application actually connects
 * as these roles -- this test opens its own direct JDBC connections per role to prove
 * the grant boundary independently of the application's own (currently single,
 * admin-credentialed) datasource.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ModuleSchemaIsolationTest {

    @Autowired
    private DataSource dataSource;

    private String jdbcUrl;

    @BeforeEach
    void resolveJdbcUrl() throws SQLException {
        try (Connection adminConnection = dataSource.getConnection()) {
            jdbcUrl = adminConnection.getMetaData().getURL();
        }
    }

    @ParameterizedTest(name = "{0} role can create, use and drop a table inside its own schema")
    @MethodSource("everyModuleRole")
    void moduleRoleCanOperateWithinItsOwnSchema(ModuleDatabaseRole role) throws SQLException {
        String table = role.ownSchema + ".isolation_probe";
        try (Connection connection = connectAs(role);
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + table + " (id INT)");
            statement.execute("INSERT INTO " + table + " (id) VALUES (1)");
            statement.execute("DROP TABLE " + table);
        }
    }

    @ParameterizedTest(name = "{0} role is denied access to the {1} schema")
    @MethodSource("everyModuleRoleAgainstEveryForeignSchema")
    void moduleRoleCannotAccessAnyForeignSchema(ModuleDatabaseRole role, String foreignSchema) throws SQLException {
        try (Connection connection = connectAs(role);
                Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute(
                            "CREATE TABLE " + foreignSchema + ".isolation_probe (id INT)"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("permission denied");
        }
    }

    private Connection connectAs(ModuleDatabaseRole role) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, role.username, role.password);
    }

    static Stream<ModuleDatabaseRole> everyModuleRole() {
        return Stream.of(ModuleDatabaseRole.values());
    }

    static Stream<Arguments> everyModuleRoleAgainstEveryForeignSchema() {
        return Stream.of(ModuleDatabaseRole.values())
                .flatMap(role -> Stream.of(ModuleDatabaseRole.values())
                        .map(other -> other.ownSchema)
                        .filter(schema -> !schema.equals(role.ownSchema))
                        .map(foreignSchema -> Arguments.of(role, foreignSchema)));
    }
}
