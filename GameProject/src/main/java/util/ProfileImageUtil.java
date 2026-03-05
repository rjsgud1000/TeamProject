package util;

/**
 * [역할] 프로필 이미지 업로드 보조 유틸
 *
 * - safeExt(): Content-Type 기반으로 허용 확장자만 통과(간단 방어)
 *   주의: Content-Type은 위조될 수 있으므로, 필요하면 파일 시그니처(매직넘버) 검사로 강화해야 합니다.
 * - sha256Hex(): 파일명 생성 등에서 충돌을 줄이기 위한 해시 문자열 생성
 */
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