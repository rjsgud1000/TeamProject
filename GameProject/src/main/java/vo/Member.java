package vo;

/**
 * [역할] 회원 정보 VO(= DTO)
 *
 * - DB의 member 테이블 레코드를 자바 객체로 표현합니다.
 * - 화면(JSP)에서 사용되며, passwordHash는 저장용이므로 화면에 노출하지 않도록 주의합니다.
 *
 * [필드]
 * - role: ADMIN / USER
 * - status: ACTIVE / BLOCKED 등
 * - sanctionUntil: 제재 만료 시각(만료 전이면 로그인 제한)
 */
public class Member {
    private String loginId;
    private String passwordHash;
    private String nickname;
    private String email;
    private String role; // ADMIN / USER
    private String status; // ACTIVE / BLOCKED etc.
    private LocalDateTime sanctionUntil;
    private LocalDateTime createdAt;
    private String profileImage;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSanctionUntil() {
        return sanctionUntil;
    }

    public void setSanctionUntil(LocalDateTime sanctionUntil) {
        this.sanctionUntil = sanctionUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}