package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DatabaseConnection {

    public static final String DEFAULT_URL = "jdbc:sqlite:hostaly.db";

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConnection.class);
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private String url = DEFAULT_URL;
    private Connection connection;

    private DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url);
                try (Statement statement = connection.createStatement()) {
                    statement.execute("PRAGMA foreign_keys = ON");
                }
                LOGGER.info("event=DB_CONNECTION_OPENED url={}", url);
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Impossibile aprire la connessione al database", e);
        }
    }

    public synchronized void setUrl(String url) {
        close();
        this.url = url;
    }

    public synchronized String getUrl() {
        return url;
    }

    public synchronized void close() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
            LOGGER.info("event=DB_CONNECTION_CLOSED url={}", url);
        } catch (SQLException e) {
            LOGGER.warn("event=DB_CLOSE_FAILED url={}", url, e);
        } finally {
            connection = null;
        }
    }
}
