package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProfileImageUtil {

    private ProfileImageUtil() {}

    public static String safeExt(String contentType) {
        if (contentType == null) return null;
        contentType = contentType.toLowerCase();
        if (contentType.contains("png")) return "png";
        if (contentType.contains("jpeg") || contentType.contains("jpg")) return "jpg";
        if (contentType.contains("gif")) return "gif";
        if (contentType.contains("webp")) return "webp";
        return null;
    }

    public static String sha256Hex(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
