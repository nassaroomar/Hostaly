package databaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.DatabaseConnection;
import database.DatabaseException;
import database.DatabaseManager;

class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
        DatabaseConnection.getInstance().setUrl(TestDatabase.IN_MEMORY_URL);
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void schemaCreatesAllEntityTables() throws SQLException {
        DatabaseManager.createSchema();
        Set<String> tables = tableNames();
        Set<String> expected = Set.of("users", "rooms", "room_features", "seasons", "extra_services",
                "bookings", "booking_service_lines", "invoices", "invoice_lines");
        assertEquals(expected, tables);
    }

    @Test
    void schemaCanBeExecutedTwiceWithoutErrors() throws SQLException {
        DatabaseManager.createSchema();
        DatabaseManager.createSchema();
        assertEquals(9, tableNames().size());
    }

    @Test
    void missingResourceThrowsDatabaseException() {
        assertThrows(DatabaseException.class, () -> DatabaseManager.executeScript("/database_embedded/missing.sql"));
    }

    private Set<String> tableNames() throws SQLException {
        Set<String> names = new HashSet<>();
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%'")) {
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        }
        return names;
    }
}
