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
	// 해시 접두어 상수
	private static final String PREFIX = "pbkdf2";
	// 해시 알고리즘 상수
	private static final String ALGO = "PBKDF2WithHmacSHA256";
	// 반복 횟수 상수
	private static final int ITERATIONS = 120_000;
	// 솔트 바이트 길이 상수
	private static final int SALT_BYTES = 16;
	// 해시 키 길이 상수
	private static final int KEY_LENGTH_BITS = 256;

	// 유틸 클래스 생성 방지 생성자
	private PasswordUtil() {}

	// 비밀번호 해시 생성 메소드
	public static String hash(String rawPassword) {
		if (rawPassword == null) throw new IllegalArgumentException("password is null");
		byte[] salt = new byte[SALT_BYTES];
		new SecureRandom().nextBytes(salt);
		byte[] dk = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
		return PREFIX + "$" + ITERATIONS + "$" + b64(salt) + "$" + b64(dk);
	}

	// 비밀번호 일치 여부 확인 메소드
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

	// 해시 문자열 여부 확인 메소드
	public static boolean isHashed(String stored) {
		return stored != null && stored.startsWith(PREFIX + "$");
	}

	// PBKDF2 해시 계산 메소드
	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBits) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBits);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
			return skf.generateSecret(spec).getEncoded();
		} catch (Exception e) {
			throw new RuntimeException("PBKDF2 hashing failed", e);
		}
	}

	// Base64 인코딩 메소드
	private static String b64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	// Base64 디코딩 메소드
	private static byte[] b64d(String s) {
		return Base64.getDecoder().decode(s);
	}
}