package databaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.DatabaseConnection;

class DatabaseConnectionTest {

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void defaultUrlPointsToLocalHostalyFile() {
        assertEquals("jdbc:sqlite:hostaly.db", DatabaseConnection.DEFAULT_URL);
    }

    @Test
    void testUrlIsInMemory() {
        assertEquals(TestDatabase.IN_MEMORY_URL, DatabaseConnection.getInstance().getUrl());
    }

    @Test
    void getConnectionReturnsSameOpenConnection() throws SQLException {
        Connection first = DatabaseConnection.getInstance().getConnection();
        Connection second = DatabaseConnection.getInstance().getConnection();
        assertSame(first, second);
        assertFalse(first.isClosed());
    }

    @Test
    void foreignKeysAreEnabled() throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA foreign_keys")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    void closeClosesConnection() throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        DatabaseConnection.getInstance().close();
        assertTrue(connection.isClosed());
    }

    @Test
    void connectionIsReopenedAfterClose() throws SQLException {
        DatabaseConnection.getInstance().close();
        Connection reopened = DatabaseConnection.getInstance().getConnection();
        assertNotNull(reopened);
        assertFalse(reopened.isClosed());
    }
}
