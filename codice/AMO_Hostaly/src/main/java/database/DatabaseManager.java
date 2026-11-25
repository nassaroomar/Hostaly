package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DatabaseManager {

    public static final String SCHEMA_PATH = "/database_embedded/schema.sql";
    public static final String DATA_PATH = "/database_embedded/data.sql";

    private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

    private DatabaseManager() {
    }

    public static void createSchema() {
        executeScript(SCHEMA_PATH);
    }

    public static void executeScript(String resourcePath) {
        String sql = readResource(resourcePath);
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String part : sql.split(";")) {
                String sqlStatement = part.strip();
                if (!sqlStatement.isEmpty()) {
                    statement.execute(sqlStatement);
                }
            }
            LOGGER.info("event=SCRIPT_EXECUTED resource={}", resourcePath);
        } catch (SQLException e) {
            throw new DatabaseException("Esecuzione dello script fallita: " + resourcePath, e);
        }
    }

    private static String readResource(String resourcePath) {
        try (InputStream input = DatabaseManager.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new DatabaseException("Risorsa SQL non trovata: " + resourcePath, null);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DatabaseException("Lettura della risorsa SQL fallita: " + resourcePath, e);
        }
    }
}
