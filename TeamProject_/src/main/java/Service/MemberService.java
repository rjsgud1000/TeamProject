package Service;

import Dao.MemberDAO;
import Vo.MemberVO;
import util.NaverMailSend;
import util.PasswordUtil;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class MemberService {
	private final MemberDAO memberDAO = new MemberDAO();
	private final NaverMailSend mailSender = new NaverMailSend();

	public static class LoginResult {
		public final MemberVO member;
		public final String error;
		public final String flash;

		public LoginResult(MemberVO member, String error, String flash) {
			this.member = member;
			this.error = error;
			this.flash = flash;
		}
	}

	public static class PasswordRecoveryCandidate {
		public final MemberVO member;
		public final String error;

		public PasswordRecoveryCandidate(MemberVO member, String error) {
			this.member = member;
			this.error = error;
		}
	}

	public LoginResult loginWithReason(String memberId, String password) {
		if (memberId == null || memberId.isBlank() || password == null || password.isBlank()) {
			return new LoginResult(null, "아이디 또는 비밀번호가 올바르지 않습니다.", null);
		}
		String id = memberId.trim();

		MemberVO member = memberDAO.findByMemberId(id);
		if (member == null) {
			return new LoginResult(null, "아이디 또는 비밀번호가 올바르지 않습니다.", null);
		}

		String flash = null;
		String status = member.getStatus();
		if (status != null) {
			String s = status.trim().toUpperCase();
			if ("BANNED".equals(s)) {
				if (memberDAO.isLatestBannedSanctionExpired(id)) {
					memberDAO.endActiveSanctions(id);
					memberDAO.activateMember(id);
					member.setStatus("ACTIVE");
				} else {
					Dao.MemberDAO.SanctionInfo info = memberDAO.findLatestBannedSanction(id);
					String reason = (info != null && info.reason != null && !info.reason.isBlank()) ? info.reason : "(사유 없음)";
					String until = "";
					if (info != null && info.endAt != null) {
						DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						until = info.endAt.format(fmt);
					}
					String msg = "제재계정입니다.";
					msg += "\n제재사유: " + reason;
					if (!until.isEmpty()) {
						msg += "\n제재기간: ~ " + until;
					}
					return new LoginResult(null, msg, null);
				}
			}
			if ("WITHDRAWN".equals(s)) {
				return new LoginResult(null, "탈퇴한 계정입니다.", null);
			}
			if ("WARNING".equals(s)) {
				int warningCount = memberDAO.countSanctions(id)[0];
				memberDAO.endWarningSanctions(id);
				memberDAO.activateMember(id);
				member.setStatus("ACTIVE");
				flash = "관리자로 부터 경고를 받았습니다. 경고 누적시 제재당할수도 있으니 주의하십시오. 경고누적: " + warningCount + "회";
			}
			if (!"ACTIVE".equals(member.getStatus()) && !"INACTIVE".equals(member.getStatus()) && !"WARNING".equals(member.getStatus())) {
				return new LoginResult(null, "현재 계정 상태로는 로그인할 수 없습니다. (상태: " + member.getStatus() + ")", null);
			}
		}

		// 비밀번호 검증(기존 로직 유지)
		String stored = member.getPasswordHash();
		boolean ok;
		if (PasswordUtil.isHashed(stored)) {
			ok = PasswordUtil.matches(password, stored);
		} else {
			ok = password.equals(stored);
			if (ok) {
				String newHash = PasswordUtil.hash(password);
				memberDAO.updatePasswordHash(id, newHash);
				member.setPasswordHash(newHash);
			}
		}

		return ok ? new LoginResult(memberDAO.findByMemberId(id), null, flash) : new LoginResult(null, "아이디 또는 비밀번호가 올바르지 않습니다.", null);
	}

	public MemberVO login(String memberId, String password) {
		// 기존 호출부 호환: 이유 없이 member만 반환
		LoginResult r = loginWithReason(memberId, password);
		return r.member;
	}

	public String join(MemberVO vo) {
		if (vo == null || vo.getMemberId() == null || vo.getMemberId().isBlank()) {
			return "아이디는 필수입니다.";
		}
		if (vo.getPasswordHash() == null || vo.getPasswordHash().isBlank()) {
			return "비밀번호는 필수입니다.";
		}
		if (vo.getUsername() == null || vo.getUsername().isBlank()) {
			return "이름은 필수입니다.";
		}
		if (vo.getNickname() == null || vo.getNickname().isBlank()) {
			return "닉네임은 필수입니다.";
		}

		String memberIdTrim = vo.getMemberId().trim();
		String usernameTrim = vo.getUsername().trim();
		String nicknameTrim = vo.getNickname().trim();
		String email = trimToNull(vo.getEmail());
		String hp = trimToNull(vo.getPhone());
		vo.setMemberId(memberIdTrim);
		vo.setUsername(usernameTrim);
		vo.setNickname(nicknameTrim);
		vo.setEmail(email);
		vo.setPhone(hp);

		// password_hash 컬럼에는 해시를 저장
		String rawPassword = vo.getPasswordHash();
		vo.setPasswordHash(PasswordUtil.hash(rawPassword));

		if (vo.getRole() == null || vo.getRole().isBlank()) {
			vo.setRole("USER");
		}
		if (vo.getStatus() == null || vo.getStatus().isBlank()) {
			vo.setStatus("ACTIVE");
		}

		// 서버 측 형식 검증(클라 우회 방지)
		if (email != null && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			return "이메일 형식이 올바르지 않습니다.";
		}
		if (hp != null && !hp.matches("^\\d{10,11}$")) {
			return "핸드폰 번호는 숫자만 10~11자리로 입력해 주세요.";
		}

		if (memberDAO.existsMemberId(memberIdTrim)) {
			return "이미 사용 중인 아이디입니다.";
		}
		if (memberDAO.existsNickname(nicknameTrim)) {
			return "이미 사용 중인 닉네임입니다.";
		}
		if (email != null && memberDAO.existsEmail(email)) {
			return "이미 사용 중인 이메일입니다.";
		}
		if (hp != null && memberDAO.existsPhone(hp)) {
			return "이미 사용 중인 전화번호입니다.";
		}

		int inserted = memberDAO.insertMember(vo);
		return inserted == 1 ? null : "회원가입에 실패했습니다.";
	}

	public MemberVO getMemberDetail(String memberId) {
		String id = trimToNull(memberId);
		if (id == null) {
			return null;
		}
		return memberDAO.findByMemberId(id);
	}

	public String updateProfile(MemberVO vo, String newPassword) {
		if (vo == null) {
			return "잘못된 요청입니다.";
		}
		String memberId = trimToNull(vo.getMemberId());
		String nickname = trimToNull(vo.getNickname());
		if (memberId == null) {
			return "로그인 정보가 올바르지 않습니다.";
		}
		if (nickname == null) {
			return "닉네임은 필수입니다.";
		}

		vo.setMemberId(memberId);
		vo.setNickname(nickname);
		vo.setZipcode(trimToNull(vo.getZipcode()));
		vo.setAddr1(trimToNull(vo.getAddr1()));
		vo.setAddr2(trimToNull(vo.getAddr2()));
		vo.setAddr3(trimToNull(vo.getAddr3()));
		vo.setAddr4(trimToNull(vo.getAddr4()));
		vo.setGender(trimToNull(vo.getGender()));
		vo.setEmail(trimToNull(vo.getEmail()));
		vo.setPhone(trimToNull(vo.getPhone()));

		if (vo.getEmail() != null && !vo.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			return "이메일 형식이 올바르지 않습니다.";
		}
		if (vo.getPhone() != null && !vo.getPhone().matches("^\\d{10,11}$")) {
			return "핸드폰 번호는 숫자만 10~11자리로 입력해 주세요.";
		}
		if (memberDAO.existsNicknameExceptMemberId(nickname, memberId)) {
			return "이미 사용 중인 닉네임입니다.";
		}
		if (vo.getEmail() != null && memberDAO.existsEmailExceptMemberId(vo.getEmail(), memberId)) {
			return "이미 사용 중인 이메일입니다.";
		}
		if (vo.getPhone() != null && memberDAO.existsPhoneExceptMemberId(vo.getPhone(), memberId)) {
			return "이미 사용 중인 전화번호입니다.";
		}

		int updated = memberDAO.updateProfile(vo);
		if (updated != 1) {
			return "회원정보 수정에 실패했습니다.";
		}

		String password = trimToNull(newPassword);
		if (password != null) {
			if (password.length() < 4) {
				return "새 비밀번호는 4자 이상 입력해 주세요.";
			}
			int pwUpdated = memberDAO.updatePasswordHash(memberId, PasswordUtil.hash(password));
			if (pwUpdated != 1) {
				return "비밀번호 변경에 실패했습니다.";
			}
		}

		return null;
	}

	public String withdrawMember(String memberId, String password) {
		String id = trimToNull(memberId);
		String rawPassword = trimToNull(password);
		if (id == null) {
			return "로그인 정보가 올바르지 않습니다.";
		}
		if (rawPassword == null) {
			return "비밀번호를 입력해 주세요.";
		}

		MemberVO member = memberDAO.findByMemberId(id);
		if (member == null) {
			return "회원 정보를 찾을 수 없습니다.";
		}
		if ("WITHDRAWN".equalsIgnoreCase(trimToNull(member.getStatus()))) {
			return "이미 탈퇴 처리된 계정입니다.";
		}

		String stored = member.getPasswordHash();
		boolean ok;
		if (PasswordUtil.isHashed(stored)) {
			ok = PasswordUtil.matches(rawPassword, stored);
		} else {
			ok = rawPassword.equals(stored);
		}
		if (!ok) {
			return "비밀번호가 올바르지 않습니다.";
		}

		int updated = memberDAO.withdrawMember(id);
		return updated == 1 ? null : "회원탈퇴 처리에 실패했습니다.";
	}

	public List<MemberVO> getMembersForAdmin(String keyword, String status) {
		return memberDAO.findMembers(trimToNull(keyword), normalizeStatusFilter(status));
	}

	public Map<String, Integer> getMemberStatusSummary() {
		Map<String, Integer> summary = new LinkedHashMap<>();
		summary.put("ALL", memberDAO.countMembers());
		summary.put("ACTIVE", memberDAO.countMembersByStatus("ACTIVE"));
		summary.put("WARNING", memberDAO.countMembersByStatus("WARNING"));
		summary.put("INACTIVE", memberDAO.countMembersByStatus("INACTIVE"));
		summary.put("BANNED", memberDAO.countMembersByStatus("BANNED"));
		summary.put("WITHDRAWN", memberDAO.countMembersByStatus("WITHDRAWN"));
		return summary;
	}

	public MemberVO getMemberDetailForAdmin(String memberId) {
		String id = trimToNull(memberId);
		if (id == null) {
			return null;
		}
		return memberDAO.findMemberDetailForAdmin(id);
	}

	public String updateMemberStatusByAdmin(String adminMemberId, String targetMemberId, String status, String sanctionReason, String sanctionEndAtText) {
		String adminId = trimToNull(adminMemberId);
		String memberId = trimToNull(targetMemberId);
		String nextStatus = normalizeAdminStatus(status);
		String reason = trimToNull(sanctionReason);
		if (adminId == null) {
			return "관리자 정보가 올바르지 않습니다.";
		}
		if (memberId == null) {
			return "변경할 회원 정보를 찾을 수 없습니다.";
		}
		if (nextStatus == null) {
			return "변경할 회원 상태가 올바르지 않습니다.";
		}
		if (adminId.equals(memberId)) {
			return "관리자 본인 상태는 변경할 수 없습니다.";
		}

		MemberVO target = memberDAO.findByMemberId(memberId);
		if (target == null) {
			return "회원 정보를 찾을 수 없습니다.";
		}
		if ("ADMIN".equalsIgnoreCase(trimToNull(target.getRole()))) {
			return "관리자 계정은 상태를 변경할 수 없습니다.";
		}
		String currentStatus = trimToNull(target.getStatus());
		if ("WITHDRAWN".equalsIgnoreCase(currentStatus) && !"WITHDRAWN".equals(nextStatus)) {
			return "탈퇴 회원은 다른 상태로 변경할 수 없습니다.";
		}
		if (nextStatus.equalsIgnoreCase(currentStatus)) {
			return null;
		}
		if (("WARNING".equals(nextStatus) || "BANNED".equals(nextStatus)) && reason == null) {
			return "경고 또는 제재 처리 시 사유를 입력해 주세요.";
		}

		String sanctionType = null;
		java.time.LocalDateTime startAt = null;
		java.time.LocalDateTime endAt = null;
		boolean endPreviousSanctions = false;
		if ("WARNING".equals(nextStatus) || "BANNED".equals(nextStatus)) {
			sanctionType = "WARNING".equals(nextStatus) ? "WARN" : "BAN";
			startAt = java.time.LocalDateTime.now();
			if ("WARNING".equals(nextStatus)) {
				endAt = startAt;
			} else {
				endAt = parseDateTimeLocal(trimToNull(sanctionEndAtText));
				if (endAt == null) {
					return "제재 종료일시를 입력해 주세요.";
				}
				if (!endAt.isAfter(startAt)) {
					return "제재 종료일시는 현재 시각보다 늦어야 합니다.";
				}
			}
		}
		if ("ACTIVE".equals(nextStatus) && ("WARNING".equalsIgnoreCase(currentStatus) || "BANNED".equalsIgnoreCase(currentStatus))) {
			endPreviousSanctions = true;
		}

		boolean updated = memberDAO.updateMemberStatusWithSanction(memberId, adminId, nextStatus, sanctionType, reason, startAt, endAt, endPreviousSanctions);
		return updated ? null : "회원 상태 변경 또는 SANCTION 처리에 실패했습니다.";
	}

	public PasswordRecoveryCandidate validatePasswordRecoveryTarget(String memberId, String email) {
		String id = trimToNull(memberId);
		String mail = trimToNull(email);
		if (id == null) {
			return new PasswordRecoveryCandidate(null, "아이디를 입력해 주세요.");
		}
		if (mail == null) {
			return new PasswordRecoveryCandidate(null, "이메일을 입력해 주세요.");
		}
		if (!mail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			return new PasswordRecoveryCandidate(null, "이메일 형식이 올바르지 않습니다.");
		}

		MemberVO member = memberDAO.findByMemberIdAndEmail(id, mail);
		if (member == null) {
			return new PasswordRecoveryCandidate(null, "입력한 아이디와 이메일이 일치하는 계정을 찾을 수 없습니다.");
		}

		String status = trimToNull(member.getStatus());
		if ("WITHDRAWN".equalsIgnoreCase(status)) {
			return new PasswordRecoveryCandidate(null, "탈퇴한 계정은 비밀번호를 찾을 수 없습니다.");
		}

		return new PasswordRecoveryCandidate(member, null);
	}

	public String sendPasswordRecoveryCode(String email) throws Exception {
		String mail = trimToNull(email);
		if (mail == null) {
			throw new IllegalArgumentException("이메일 정보가 올바르지 않습니다.");
		}
		String code = mailSender.makeAuthenticationCode();
		mailSender.sendVerificationCode(mail, code);
		return code;
	}

	public String issueTemporaryPassword(String memberId, String email) throws Exception {
		PasswordRecoveryCandidate candidate = validatePasswordRecoveryTarget(memberId, email);
		if (candidate.error != null || candidate.member == null) {
			throw new IllegalArgumentException(candidate.error != null ? candidate.error : "회원 정보를 찾을 수 없습니다.");
		}

		String tempPassword = mailSender.makeTemporaryPassword();
		int updated = memberDAO.updatePasswordHash(candidate.member.getMemberId(), PasswordUtil.hash(tempPassword));
		if (updated != 1) {
			throw new IllegalStateException("임시 비밀번호 저장에 실패했습니다.");
		}
		mailSender.sendTemporaryPassword(candidate.member.getEmail(), tempPassword);
		return tempPassword;
	}

	public PasswordRecoveryCandidate validateJoinEmailTarget(String email) {
		String mail = trimToNull(email);
		if (mail == null) {
			return new PasswordRecoveryCandidate(null, "이메일을 입력해 주세요.");
		}
		if (!mail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			return new PasswordRecoveryCandidate(null, "이메일 형식이 올바르지 않습니다.");
		}
		if (memberDAO.existsEmail(mail)) {
			return new PasswordRecoveryCandidate(null, "이미가입된 이메일입니다.");
		}
		return new PasswordRecoveryCandidate(null, null);
	}

	public String sendJoinEmailVerificationCode(String email) throws Exception {
		PasswordRecoveryCandidate candidate = validateJoinEmailTarget(email);
		if (candidate.error != null) {
			throw new IllegalArgumentException(candidate.error);
		}
		String mail = trimToNull(email);
		String code = mailSender.makeAuthenticationCode();
		mailSender.sendJoinVerificationCode(mail, code);
		return code;
	}

	private java.time.LocalDateTime parseDateTimeLocal(String value) {
		if (value == null) {
			return null;
		}
		try {
			return java.time.LocalDateTime.parse(value);
		} catch (Exception e) {
			return null;
		}
	}

	private String normalizeStatusFilter(String status) {
		String value = trimToNull(status);
		if (value == null) {
			return "ALL";
		}
		String normalized = value.toUpperCase();
		switch (normalized) {
		case "ALL":
		case "ACTIVE":
		case "WARNING":
		case "INACTIVE":
		case "BANNED":
		case "WITHDRAWN":
			return normalized;
		default:
			return null;
		}
	}

	private String normalizeAdminStatus(String status) {
		String value = trimToNull(status);
		if (value == null) {
			return null;
		}
		String normalized = value.toUpperCase();
		switch (normalized) {
		case "ACTIVE":
		case "WARNING":
		case "BANNED":
		case "WITHDRAWN":
			return normalized;
		default:
			return null;
		}
	}

	private static String trimToNull(String s) {
		if (s == null)
			return null;
		String v = s.trim();
		return v.isEmpty() ? null : v;
	}
}