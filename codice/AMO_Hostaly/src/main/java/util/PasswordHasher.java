package util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(plainPassword, salt);
        return HexFormat.of().formatHex(salt) + ":" + HexFormat.of().formatHex(hash);
    }

    public static boolean verify(String plainPassword, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = HexFormat.of().parseHex(parts[0]);
        byte[] expected = HexFormat.of().parseHex(parts[1]);
        return MessageDigest.isEqual(expected, derive(plainPassword, salt));
    }

    private static byte[] derive(String plainPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Hashing della password non disponibile", e);
        }
    }
}
