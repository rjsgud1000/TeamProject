package Service;

import Dao.MemberDAO;
import Vo.MemberVO;
import util.PasswordUtil;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class MemberService {
	private final MemberDAO memberDAO = new MemberDAO();

	public static class LoginResult {
		public final MemberVO member;
		public final String error;

		public LoginResult(MemberVO member, String error) {
			this.member = member;
			this.error = error;
		}
	}

	public LoginResult loginWithReason(String memberId, String password) {
		if (memberId == null || memberId.isBlank() || password == null || password.isBlank()) {
			return new LoginResult(null, "아이디 또는 비밀번호가 올바르지 않습니다.");
		}
		String id = memberId.trim();

		MemberVO member = memberDAO.findByMemberId(id);
		if (member == null) {
			return new LoginResult(null, "아이디 또는 비밀번호가 올바르지 않습니다.");
		}

		String status = member.getStatus();
		if (status != null) {
			String s = status.trim().toUpperCase();
			if ("BANNED".equals(s)) {
				Dao.MemberDAO.SanctionInfo info = memberDAO.findActiveSanction(id);
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
				return new LoginResult(null, msg);
			}
			if ("WITHDRAWN".equals(s)) {
				return new LoginResult(null, "탈퇴한 계정입니다.");
			}
			if (!"ACTIVE".equals(s) && !"INACTIVE".equals(s)) {
				return new LoginResult(null, "현재 계정 상태로는 로그인할 수 없습니다. (상태: " + s + ")");
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

		return ok ? new LoginResult(member, null) : new LoginResult(null, "아이디 또는 비밀번호가 올바르지 않습니다.");
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

	private String normalizeStatusFilter(String status) {
		String value = trimToNull(status);
		if (value == null) {
			return "ALL";
		}
		String normalized = value.toUpperCase();
		switch (normalized) {
		case "ALL":
		case "ACTIVE":
		case "INACTIVE":
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