package daoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.SeasonDao;
import databaseTest.TestDatabase;
import model.Season;

class SeasonDaoTest {

    private SeasonDao dao;

    @BeforeEach
    void setUp() {
        TestDatabase.useInMemoryDatabase();
        dao = SeasonDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        TestDatabase.reset();
    }

    @Test
    void insertAndFindKeepsDatesAndFactor() {
        Season season = new Season("Alta stagione", LocalDate.of(2027, 6, 1), LocalDate.of(2027, 8, 31), new BigDecimal("1.30"));
        long id = dao.insert(season);

        Season found = dao.findById(id).orElseThrow();
        assertEquals("Alta stagione", found.getName());
        assertEquals(LocalDate.of(2027, 6, 1), found.getStartDate());
        assertEquals(LocalDate.of(2027, 8, 31), found.getEndDate());
        assertEquals(0, new BigDecimal("1.30").compareTo(found.getFactor()));
    }

    @Test
    void findAllOrdersByStartDate() {
        dao.insert(new Season("Estate", LocalDate.of(2027, 6, 1), LocalDate.of(2027, 8, 31), new BigDecimal("1.30")));
        dao.insert(new Season("Inverno", LocalDate.of(2027, 1, 7), LocalDate.of(2027, 3, 31), new BigDecimal("0.85")));
        assertEquals("Inverno", dao.findAll().get(0).getName());
    }

    @Test
    void updateChangesFactor() {
        Season season = new Season("Natale", LocalDate.of(2027, 12, 20), LocalDate.of(2028, 1, 6), new BigDecimal("1.20"));
        dao.insert(season);

        season.setFactor(new BigDecimal("1.50"));
        dao.update(season);

        assertEquals(0, new BigDecimal("1.50").compareTo(dao.findById(season.getId()).orElseThrow().getFactor()));
    }

    @Test
    void deleteRemovesSeason() {
        Season season = new Season("Temp", LocalDate.of(2027, 5, 1), LocalDate.of(2027, 5, 2), new BigDecimal("1.00"));
        long id = dao.insert(season);

        assertTrue(dao.delete(id));
        assertFalse(dao.findById(id).isPresent());
    }
}
