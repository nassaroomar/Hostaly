package daoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.RoomDao;
import database.DatabaseException;
import databaseTest.TestDatabase;
import enums.RoomStatus;
import enums.RoomType;
import model.Room;

class RoomDaoTest {

    private RoomDao dao;

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
        dao = RoomDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void insertAndFindKeepsAllFields() {
        Room room = new Room("301", 3, RoomType.SUITE, new BigDecimal("250.50"), 2, RoomStatus.DISPONIBILE, "Vista mare");
        long id = dao.insert(room);

        Room found = dao.findById(id).orElseThrow();
        assertEquals("301", found.getNumber());
        assertEquals(3, found.getFloor());
        assertEquals(RoomType.SUITE, found.getType());
        assertEquals(0, new BigDecimal("250.50").compareTo(found.getBasePrice()));
        assertEquals(2, found.getMaxGuests());
        assertEquals(RoomStatus.DISPONIBILE, found.getStatus());
        assertEquals("Vista mare", found.getNote());
    }

    @Test
    void roomNumberMustBeUnique() {
        dao.insert(new Room("101", 1, RoomType.SINGLE, new BigDecimal("80"), 1, RoomStatus.DISPONIBILE, null));
        assertThrows(DatabaseException.class, () ->
                dao.insert(new Room("101", 1, RoomType.DOUBLE, new BigDecimal("100"), 2, RoomStatus.DISPONIBILE, null)));
    }

    @Test
    void findAllReturnsRoomsOrderedByNumber() {
        dao.insert(new Room("202", 2, RoomType.DOUBLE, new BigDecimal("120"), 2, RoomStatus.DISPONIBILE, null));
        dao.insert(new Room("101", 1, RoomType.SINGLE, new BigDecimal("80"), 1, RoomStatus.DISPONIBILE, null));
        assertEquals("101", dao.findAll().get(0).getNumber());
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void updateChangesStatusAndPrice() {
        Room room = new Room("102", 1, RoomType.DOUBLE, new BigDecimal("110"), 2, RoomStatus.DISPONIBILE, null);
        dao.insert(room);

        room.setStatus(RoomStatus.DA_PULIRE);
        room.setBasePrice(new BigDecimal("125.00"));
        dao.update(room);

        Room reloaded = dao.findById(room.getId()).orElseThrow();
        assertEquals(RoomStatus.DA_PULIRE, reloaded.getStatus());
        assertEquals(0, new BigDecimal("125.00").compareTo(reloaded.getBasePrice()));
    }

    @Test
    void deleteRemovesRoom() {
        Room room = new Room("103", 1, RoomType.DOUBLE, new BigDecimal("110"), 2, RoomStatus.DISPONIBILE, null);
        long id = dao.insert(room);

        assertTrue(dao.delete(id));
        assertFalse(dao.findById(id).isPresent());
    }
}
