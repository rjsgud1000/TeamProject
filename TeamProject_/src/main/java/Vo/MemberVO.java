package Vo;

public class MemberVO {
	private String memberId;
	private String username;
	private String passwordHash;
	private String nickname;
	private String role;
	private String status;

	public MemberVO() {
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	// ===== 호환용 별칭 메서드 (기존 JSP/세션 코드 방어) =====
	public String getId() {
		return username;
	}

	public String getName() {
		return nickname;
	}
}