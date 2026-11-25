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
import dao.InvoiceDao;
import dao.RoomDao;
import dao.UserDao;
import databaseTest.TestDatabase;
import enums.BookingStatus;
import enums.InvoiceStatus;
import enums.RoomStatus;
import enums.RoomType;
import model.Booking;
import model.Cliente;
import model.Invoice;
import model.Room;

class InvoiceDaoTest {

    private InvoiceDao dao;
    private long guestId;
    private long bookingId;

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
        dao = InvoiceDao.getInstance();
        guestId = UserDao.getInstance().insert(new Cliente("mario", "hash", "Mario Rossi", null));
        long roomId = RoomDao.getInstance().insert(
                new Room("101", 1, RoomType.SINGLE, new BigDecimal("80"), 1, RoomStatus.DISPONIBILE, null));
        bookingId = BookingDao.getInstance().insert(new Booking(guestId, "Mario Rossi", roomId,
                LocalDate.of(2027, 6, 10), LocalDate.of(2027, 6, 12), BookingStatus.CHECKED_OUT,
                LocalDateTime.of(2026, 11, 23, 10, 0), null));
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void insertAndFindKeepsTotalsAndStatus() {
        Invoice invoice = newInvoice(new BigDecimal("160.00"));
        long id = dao.insert(invoice);

        Invoice found = dao.findById(id).orElseThrow();
        assertEquals(bookingId, found.getBookingId());
        assertEquals(0, new BigDecimal("160.00").compareTo(found.getTotalAmount()));
        assertEquals(InvoiceStatus.EMESSA, found.getStatus());
        assertEquals(LocalDate.of(2027, 6, 10), found.getCheckIn());
        assertNull(found.getPaidAt());
    }

    @Test
    void updateRegistersPayment() {
        Invoice invoice = newInvoice(new BigDecimal("160.00"));
        dao.insert(invoice);

        invoice.setStatus(InvoiceStatus.PAGATA);
        invoice.setPaidAt(LocalDateTime.of(2027, 6, 12, 11, 0));
        dao.update(invoice);

        Invoice reloaded = dao.findById(invoice.getId()).orElseThrow();
        assertEquals(InvoiceStatus.PAGATA, reloaded.getStatus());
        assertEquals(LocalDateTime.of(2027, 6, 12, 11, 0), reloaded.getPaidAt());
    }

    @Test
    void findByGuestIdReturnsOnlyThatGuestInvoices() {
        dao.insert(newInvoice(new BigDecimal("100.00")));
        long otherGuest = UserDao.getInstance().insert(new Cliente("luca", "hash", "Luca", null));
        dao.insert(new Invoice(bookingId, otherGuest, "Luca", "101", LocalDate.of(2027, 6, 10),
                LocalDate.of(2027, 6, 12), LocalDateTime.of(2027, 6, 12, 10, 0), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("50.00"), InvoiceStatus.EMESSA));

        assertEquals(1, dao.findByGuestId(guestId).size());
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void deleteRemovesInvoice() {
        Invoice invoice = newInvoice(new BigDecimal("90.00"));
        long id = dao.insert(invoice);

        assertTrue(dao.delete(id));
        assertFalse(dao.findById(id).isPresent());
    }

    private Invoice newInvoice(BigDecimal total) {
        return new Invoice(bookingId, guestId, "Mario Rossi", "101", LocalDate.of(2027, 6, 10),
                LocalDate.of(2027, 6, 12), LocalDateTime.of(2027, 6, 12, 10, 0), BigDecimal.ZERO,
                BigDecimal.ZERO, total, InvoiceStatus.EMESSA);
    }
}
