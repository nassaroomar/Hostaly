package databaseTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import util.PasswordHasher;

class PasswordHasherTest {

    @Test
    void verifyAcceptsCorrectPassword() {
        String stored = PasswordHasher.hash("segreta123");
        assertTrue(PasswordHasher.verify("segreta123", stored));
    }

    @Test
    void verifyRejectsWrongPassword() {
        String stored = PasswordHasher.hash("segreta123");
        assertFalse(PasswordHasher.verify("altra456", stored));
    }

    @Test
    void samePasswordGetsDifferentSalts() {
        assertNotEquals(PasswordHasher.hash("uguale"), PasswordHasher.hash("uguale"));
    }

    @Test
    void malformedStoredHashIsRejected() {
        assertFalse(PasswordHasher.verify("qualsiasi", "non-valido"));
    }
}
