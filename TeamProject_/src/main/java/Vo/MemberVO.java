package Vo;

public class MemberVO {
	private String memberId;
	private String username;
	private String passwordHash;
	private String nickname;
	private String name;

	private String zipcode;
	private String addr1;
	private String addr2;
	private String addr3;
	private String addr4;
	private String gender;
	private String email;
	private String phone;

	private String role;
	private String status;

	// sanction info (when BANNED)
	private String sanctionReason;
	private java.time.LocalDateTime sanctionEndAt;

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

	public String getNameReal() {
		return name;
	}

	public void setNameReal(String name) {
		this.name = name;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public String getAddr3() {
		return addr3;
	}

	public void setAddr3(String addr3) {
		this.addr3 = addr3;
	}

	public String getAddr4() {
		return addr4;
	}

	public void setAddr4(String addr4) {
		this.addr4 = addr4;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getSanctionReason() {
		return sanctionReason;
	}

	public void setSanctionReason(String sanctionReason) {
		this.sanctionReason = sanctionReason;
	}

	public java.time.LocalDateTime getSanctionEndAt() {
		return sanctionEndAt;
	}

	public void setSanctionEndAt(java.time.LocalDateTime sanctionEndAt) {
		this.sanctionEndAt = sanctionEndAt;
	}

	// ===== 호환용 별칭 메서드 (기존 JSP/세션 코드 방어) =====
	public String getId() {
		// 로그인 식별자는 member_id로 통일
		return memberId;
	}

	public String getName() {
		// 상단/세션 표시 이름: name(실명) 우선, 없으면 nickname
		return (name != null && !name.isBlank()) ? name : nickname;
	}
}