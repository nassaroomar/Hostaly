package daoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.BookingDao;
import dao.RoomDao;
import dao.UserDao;
import databaseTest.TestDatabase;
import enums.BookingStatus;
import enums.RoomStatus;
import enums.RoomType;
import model.Booking;
import model.Cliente;
import model.Room;

class BookingDaoTest {

    private BookingDao dao;
    private long guestId;
    private long roomId;

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
        dao = BookingDao.getInstance();
        guestId = UserDao.getInstance().insert(new Cliente("mario", "hash", "Mario Rossi", null));
        roomId = RoomDao.getInstance().insert(
                new Room("101", 1, RoomType.SINGLE, new BigDecimal("80"), 1, RoomStatus.DISPONIBILE, null));
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void insertAndFindKeepsDatesAndStatus() {
        Booking booking = newBooking(LocalDate.of(2027, 6, 10), LocalDate.of(2027, 6, 12));
        long id = dao.insert(booking);

        Booking found = dao.findById(id).orElseThrow();
        assertEquals(LocalDate.of(2027, 6, 10), found.getCheckIn());
        assertEquals(LocalDate.of(2027, 6, 12), found.getCheckOut());
        assertEquals(BookingStatus.PENDING, found.getStatus());
        assertEquals(guestId, found.getGuestId());
        assertEquals(roomId, found.getRoomId());
        assertNull(found.getConfirmedAt());
    }

    @Test
    void updateStoresConfirmationAndCancellationData() {
        Booking booking = newBooking(LocalDate.of(2027, 7, 1), LocalDate.of(2027, 7, 3));
        dao.insert(booking);

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.of(2027, 6, 20, 9, 30));
        booking.setCancelledBy("CLIENT");
        booking.setCancellationPenalty(new BigDecimal("80"));
        dao.update(booking);

        Booking reloaded = dao.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.CANCELLED, reloaded.getStatus());
        assertEquals(LocalDateTime.of(2027, 6, 20, 9, 30), reloaded.getCancelledAt());
        assertEquals("CLIENT", reloaded.getCancelledBy());
        assertEquals(0, new BigDecimal("80").compareTo(reloaded.getCancellationPenalty()));
    }

    @Test
    void findByGuestIdReturnsOnlyOwnBookings() {
        dao.insert(newBooking(LocalDate.of(2027, 8, 1), LocalDate.of(2027, 8, 2)));
        dao.insert(newBooking(LocalDate.of(2027, 8, 5), LocalDate.of(2027, 8, 6)));
        long otherGuest = UserDao.getInstance().insert(new Cliente("luca", "hash", "Luca", null));
        dao.insert(new Booking(otherGuest, "Luca", roomId, LocalDate.of(2027, 9, 1), LocalDate.of(2027, 9, 2),
                BookingStatus.PENDING, LocalDateTime.of(2026, 11, 23, 10, 0), null));

        assertEquals(2, dao.findByGuestId(guestId).size());
        assertEquals(3, dao.findAll().size());
    }

    @Test
    void deleteRemovesBooking() {
        Booking booking = newBooking(LocalDate.of(2027, 10, 1), LocalDate.of(2027, 10, 2));
        long id = dao.insert(booking);

        assertTrue(dao.delete(id));
        assertFalse(dao.findById(id).isPresent());
    }

    private Booking newBooking(LocalDate checkIn, LocalDate checkOut) {
        return new Booking(guestId, "Mario Rossi", roomId, checkIn, checkOut,
                BookingStatus.PENDING, LocalDateTime.of(2026, 11, 23, 10, 0), null);
    }
}
