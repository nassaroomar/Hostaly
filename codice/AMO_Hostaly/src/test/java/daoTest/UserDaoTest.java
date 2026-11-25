package daoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.UserDao;
import database.DatabaseException;
import databaseTest.TestDatabase;
import enums.Role;
import model.Admin;
import model.Cliente;
import model.Gestore;
import model.Utente;

class UserDaoTest {

    private UserDao dao;

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
        dao = UserDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void insertAssignsIdAndPersistsRole() {
        Utente client = new Cliente("mario", "hash", "Mario Rossi", "mario@hostaly.it");
        long id = dao.insert(client);

        Utente found = dao.findById(id).orElseThrow();
        assertEquals("mario", found.getUsername());
        assertEquals(Role.CLIENT, found.getRole());
        assertTrue(found instanceof Cliente);
    }

    @Test
    void findByUsernameReturnsRightSubclass() {
        dao.insert(new Gestore("gestore", "hash", "Gestore Reception", "g@hostaly.it"));
        dao.insert(new Admin("admin", "hash", "Amministratore", "a@hostaly.it"));

        assertTrue(dao.findByUsername("gestore").orElseThrow() instanceof Gestore);
        assertTrue(dao.findByUsername("admin").orElseThrow() instanceof Admin);
    }

    @Test
    void findByUsernameReturnsEmptyWhenMissing() {
        assertFalse(dao.findByUsername("nessuno").isPresent());
    }

    @Test
    void usernameMustBeUnique() {
        dao.insert(new Cliente("mario", "hash", "Mario Rossi", null));
        assertThrows(DatabaseException.class, () -> dao.insert(new Cliente("mario", "altro", "Altro", null)));
    }

    @Test
    void findAllReturnsEveryUser() {
        dao.insert(new Cliente("a", "h", "A", null));
        dao.insert(new Cliente("b", "h", "B", null));
        List<Utente> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void updateChangesMutableFields() {
        Utente user = new Cliente("luca", "hash", "Luca", "old@hostaly.it");
        dao.insert(user);

        user.setFullName("Luca Bianchi");
        user.setEmail("new@hostaly.it");
        user.setActive(false);
        dao.update(user);

        Utente reloaded = dao.findById(user.getId()).orElseThrow();
        assertEquals("Luca Bianchi", reloaded.getFullName());
        assertEquals("new@hostaly.it", reloaded.getEmail());
        assertFalse(reloaded.isActive());
    }

    @Test
    void deleteRemovesUser() {
        Utente user = new Cliente("temp", "hash", "Temp", null);
        long id = dao.insert(user);

        assertTrue(dao.delete(id));
        assertFalse(dao.findById(id).isPresent());
        assertFalse(dao.delete(id));
    }

    @Test
    void countReflectsInserts() {
        assertEquals(0, dao.count());
        dao.insert(new Cliente("x", "h", "X", null));
        assertEquals(1, dao.count());
    }

}
