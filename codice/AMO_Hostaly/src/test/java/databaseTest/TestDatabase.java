package databaseTest;

import database.DatabaseConnection;
import database.DatabaseManager;

public final class TestDatabase {

    public static final String IN_MEMORY_URL = "jdbc:sqlite::memory:";

    private TestDatabase() {
    }

    public static void useInMemoryDatabase() {
        DatabaseConnection.getInstance().setUrl(IN_MEMORY_URL);
        DatabaseManager.createSchema();
    }

    public static void reset() {
        DatabaseConnection.getInstance().close();
    }
}
