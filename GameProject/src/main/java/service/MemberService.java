package service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import dao.MemberDAO;
import vo.Member;

/**
 * [역할] 회원 관련 비즈니스 로직(Service)
 *
 * - Controller에서 받은 입력을 "정책"에 따라 검증하고 DAO를 호출합니다.
 * - 클라이언트(JSP/JS) 검증은 우회될 수 있으므로, 가입/수정 같은 "쓰기" 작업의 최종 검증은 반드시 Service에서 수행합니다.
 *
 * [유지보수 포인트]
 * - 회원가입 정책(비밀번호 규칙, 이메일 규칙, 차단/제재 정책 등)이 바뀌면 이 클래스가 1차 변경 지점입니다.
 */
public class MemberService {

    private final MemberDAO memberDAO = new MemberDAO();

    // 이메일 형식 검증용(일반적인 케이스 커버; 완전한 RFC 구현은 아님)
    // - 프론트에서도 검증하지만 최종 방어선은 서버(Service)입니다.
    // - 너무 과한 RFC 규칙을 넣으면 정상 사용자도 차단될 수 있어 "현실적인" 규칙으로 둡니다.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@" +
            "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?" +
            "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$"
    );

    /**
     * 회원가입
     *
     * [검증 순서]
     * 1) 필수값
     * 2) 이메일 형식(우회 방지)
     * 3) 중복 체크(아이디/닉네임/이메일)
     * 4) 저장(비밀번호는 SHA-256 해시로 저장)
     *
     * @throws IllegalArgumentException 검증 실패 시(컨트롤러에서 캐치하여 join.jsp에 메시지 출력)
     */
    public void register(String loginId, String password, String nickname, String email) {
        if (isBlank(loginId) || isBlank(password) || isBlank(nickname) || isBlank(email)) {
            throw new IllegalArgumentException("필수값을 입력하세요.");
        }

        String id = loginId.trim();
        String nn = nickname.trim();
        String em = email.trim();

        if (!isValidEmail(em)) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }

        if (memberDAO.existsLoginId(id)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberDAO.existsNickname(nn)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (memberDAO.existsEmail(em)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member m = new Member();
        m.setLoginId(id);
        m.setPasswordHash(sha256(password));
        m.setNickname(nn);
        m.setEmail(em);
        m.setRole(isAdminSeed(id) ? "ADMIN" : "USER");
        m.setStatus("ACTIVE");
        m.setSanctionUntil(null);

        memberDAO.insert(m);
    }

    // 기존 시그니처 호출부 호환용
    public void register(String loginId, String password, String nickname) {
        register(loginId, password, nickname, null);
    }

    public LoginResult loginDetailed(String loginId, String password) {
        if (isBlank(loginId) || isBlank(password)) return LoginResult.invalid();

        Member m = memberDAO.findByLoginId(loginId.trim());
        if (m == null) return LoginResult.invalid();
        if (!"ACTIVE".equals(m.getStatus())) return LoginResult.invalid();

        LocalDateTime until = m.getSanctionUntil();
        if (until != null && until.isAfter(LocalDateTime.now())) {
            return LoginResult.blockedUntil(until);
        }

        String hash = sha256(password);
        if (!hash.equals(m.getPasswordHash())) return LoginResult.invalid();
        return LoginResult.success(m);
    }

    public String formatBlockedUntil(LocalDateTime until) {
        if (until == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return until.format(fmt);
    }

    public Member login(String loginId, String password) {
        LoginResult r = loginDetailed(loginId, password);
        return r.getStatus() == LoginResult.Status.SUCCESS ? r.getMember() : null;
    }

    public Member getByLoginId(String loginId) {
        if (isBlank(loginId)) return null;
        return memberDAO.findByLoginId(loginId.trim());
    }

    public void updateProfile(String loginId, String nickname, String newPasswordOrBlank) {
        if (isBlank(loginId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        if (isBlank(nickname)) {
            throw new IllegalArgumentException("닉네임을 입력하세요.");
        }

        String pwHash = null;
        if (!isBlank(newPasswordOrBlank)) {
            pwHash = sha256(newPasswordOrBlank);
        }

        memberDAO.updateProfile(loginId.trim(), nickname.trim(), pwHash);
    }

    public void updateProfileImage(String loginId, String profileImageOrNull) {
        if (isBlank(loginId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        memberDAO.updateProfileImage(loginId.trim(), profileImageOrNull);
    }

    public Member getPublicProfile(String loginId) {
        // 현재는 Member 객체 그대로 리턴(비밀번호는 해시지만 화면에 노출하지 않도록 JSP에서 주의)
        return getByLoginId(loginId);
    }

    public boolean isLoginIdAvailable(String loginId) {
        if (isBlank(loginId)) return false;
        return !memberDAO.existsLoginId(loginId.trim());
    }

    public boolean isNicknameAvailable(String nickname) {
        if (isBlank(nickname)) return false;
        return !memberDAO.existsNickname(nickname.trim());
    }

    public boolean isEmailAvailable(String email) {
        if (isBlank(email)) return false;
        return !memberDAO.existsEmail(email.trim());
    }

    /**
     * 이메일 형식 검증(서버 최종 검증)
     * - 길이 제한: 일반적으로 이메일 전체 길이는 320자(로컬 64 + @ + 도메인 255) 정도로 취급
     */
    private boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        String em = email.trim();
        // 너무 긴 이메일은 차단(일반적으로 local 64, domain 255; 전체 320)
        if (em.length() > 320) return false;
        return EMAIL_PATTERN.matcher(em).matches();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isAdminSeed(String loginId) {
        return "admin1".equals(loginId) || "admin2".equals(loginId) || "admin3".equals(loginId);
    }

    private String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
