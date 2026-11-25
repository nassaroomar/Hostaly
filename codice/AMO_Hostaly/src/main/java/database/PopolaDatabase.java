package database;

import java.sql.Connection;
import java.sql.SQLException;

import dao.UserDao;
import model.Admin;
import model.Cliente;
import model.Gestore;
import util.PasswordHasher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PopolaDatabase {

    private static final Logger LOGGER = LogManager.getLogger(PopolaDatabase.class);

    private PopolaDatabase() {
    }

    public static boolean populateIfEmpty() {
        if (UserDao.getInstance().count() > 0) {
            LOGGER.info("event=SEED_SKIPPED reason=database_already_populated");
            return false;
        }
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            UserDao users = UserDao.getInstance();
            users.insert(new Admin("admin", PasswordHasher.hash("admin123"), "Amministratore Hotel", "admin@hostaly.it"));
            users.insert(new Gestore("gestore", PasswordHasher.hash("gestore123"), "Gestore Reception", "gestore@hostaly.it"));
            users.insert(new Cliente("mario", PasswordHasher.hash("mario123"), "Mario Rossi", "mario@hostaly.it"));
            DatabaseManager.executeScript(DatabaseManager.DATA_PATH);
            connection.commit();
            LOGGER.info("event=SEED_COMPLETED");
            return true;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw new DatabaseException("Popolamento del database fallito", e);
        } finally {
            restoreAutoCommit(connection);
        }
    }

    private static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            LOGGER.error("event=SEED_ROLLBACK_FAILED", e);
        }
    }

    private static void restoreAutoCommit(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.error("event=SEED_AUTOCOMMIT_RESTORE_FAILED", e);
        }
    }
}
