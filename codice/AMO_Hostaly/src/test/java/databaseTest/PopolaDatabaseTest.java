package databaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.RoomDao;
import dao.SeasonDao;
import dao.ServiceDao;
import dao.UserDao;
import database.DatabaseConnection;
import database.PopolaDatabase;
import enums.Role;
import model.Utente;
import util.PasswordHasher;

class PopolaDatabaseTest {

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void firstRunPopulatesDemoData() {
        assertTrue(PopolaDatabase.populateIfEmpty());
        assertEquals(3, UserDao.getInstance().count());
        assertEquals(10, RoomDao.getInstance().findAll().size());
        assertEquals(3, SeasonDao.getInstance().findAll().size());
        assertEquals(7, ServiceDao.getInstance().findAll().size());
    }

    @Test
    void secondRunDoesNotDuplicateData() {
        PopolaDatabase.populateIfEmpty();
        assertFalse(PopolaDatabase.populateIfEmpty());
        assertEquals(3, UserDao.getInstance().count());
        assertEquals(10, RoomDao.getInstance().findAll().size());
    }

    @Test
    void demoAccountsHaveExpectedRolesAndHashedPasswords() {
        PopolaDatabase.populateIfEmpty();
        Utente admin = UserDao.getInstance().findByUsername("admin").orElseThrow();
        Utente gestore = UserDao.getInstance().findByUsername("gestore").orElseThrow();
        Utente mario = UserDao.getInstance().findByUsername("mario").orElseThrow();

        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(Role.GESTORE, gestore.getRole());
        assertEquals(Role.CLIENT, mario.getRole());
        assertTrue(PasswordHasher.verify("admin123", admin.getPasswordHash()));
        assertFalse(admin.getPasswordHash().contains("admin123"));
    }

    @Test
    void seededDataRespectsRoomsAndServicesCatalog() {
        PopolaDatabase.populateIfEmpty();
        assertEquals(10, RoomDao.getInstance().findAll().size());
        assertEquals(7, ServiceDao.getInstance().findAll().stream().filter(s -> s.isActive()).count());
    }

    @Test
    void schemaTablesAreInPlaceAfterSeeding() throws SQLException {
        PopolaDatabase.populateIfEmpty();
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users")) {
            assertTrue(rs.next());
            assertEquals(3, rs.getInt(1));
        }
    }
}
