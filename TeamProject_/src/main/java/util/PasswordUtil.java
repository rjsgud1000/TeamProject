package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PBKDF2 해시 유틸.
 * 저장 포맷: pbkdf2$iterations$saltBase64$hashBase64
 */
public class PasswordUtil {
	private static final String PREFIX = "pbkdf2";
	private static final String ALGO = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120_000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_LENGTH_BITS = 256;

	private PasswordUtil() {}

	public static String hash(String rawPassword) {
		if (rawPassword == null) throw new IllegalArgumentException("password is null");
		byte[] salt = new byte[SALT_BYTES];
		new SecureRandom().nextBytes(salt);
		byte[] dk = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
		return PREFIX + "$" + ITERATIONS + "$" + b64(salt) + "$" + b64(dk);
	}

	public static boolean matches(String rawPassword, String stored) {
		if (rawPassword == null || stored == null) return false;
		if (!stored.startsWith(PREFIX + "$")) return false;
		String[] parts = stored.split("\\$");
		if (parts.length != 4) return false;

		int iterations;
		try {
			iterations = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		byte[] salt = b64d(parts[2]);
		byte[] expected = b64d(parts[3]);
		byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations, expected.length * 8);
		return MessageDigest.isEqual(expected, actual);
	}

	public static boolean isHashed(String stored) {
		return stored != null && stored.startsWith(PREFIX + "$");
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBits) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBits);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
			return skf.generateSecret(spec).getEncoded();
		} catch (Exception e) {
			throw new RuntimeException("PBKDF2 hashing failed", e);
		}
	}

	private static String b64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	private static byte[] b64d(String s) {
		return Base64.getDecoder().decode(s);
	}
}
