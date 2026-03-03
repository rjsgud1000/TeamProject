package service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dao.MemberDAO;
import vo.Member;

public class MemberService {

    private final MemberDAO memberDAO = new MemberDAO();

    public void register(String loginId, String password, String nickname, String email) {
        if (isBlank(loginId) || isBlank(password) || isBlank(nickname) || isBlank(email)) {
            throw new IllegalArgumentException("필수값을 입력하세요.");
        }

        String id = loginId.trim();
        String nn = nickname.trim();
        String em = email.trim();

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