package daoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.ServiceDao;
import databaseTest.TestDatabase;
import enums.ServiceCategory;
import model.Service;

class ServiceDaoTest {

    private ServiceDao dao;

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
        dao = ServiceDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void insertAndFindKeepsCategoryAndPrice() {
        Service service = new Service("Minibar", ServiceCategory.MINIBAR, new BigDecimal("8.00"), "Consumazioni");
        long id = dao.insert(service);

        Service found = dao.findById(id).orElseThrow();
        assertEquals("Minibar", found.getName());
        assertEquals(ServiceCategory.MINIBAR, found.getCategory());
        assertEquals(0, new BigDecimal("8.00").compareTo(found.getPrice()));
        assertTrue(found.isActive());
    }

    @Test
    void updateCanSuspendService() {
        Service service = new Service("Spa", ServiceCategory.SPA, new BigDecimal("60"), "Ingresso");
        dao.insert(service);

        service.setActive(false);
        service.setPrice(new BigDecimal("65"));
        dao.update(service);

        Service reloaded = dao.findById(service.getId()).orElseThrow();
        assertFalse(reloaded.isActive());
        assertEquals(0, new BigDecimal("65").compareTo(reloaded.getPrice()));
    }

    @Test
    void findAllReturnsAllServices() {
        dao.insert(new Service("Colazione", ServiceCategory.BREAKFAST, new BigDecimal("12"), null));
        dao.insert(new Service("Parcheggio", ServiceCategory.PARKING, new BigDecimal("10"), null));
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void deleteRemovesService() {
        long id = dao.insert(new Service("Lavanderia", ServiceCategory.LAUNDRY, new BigDecimal("15"), null));
        assertTrue(dao.delete(id));
        assertFalse(dao.findById(id).isPresent());
    }
}
