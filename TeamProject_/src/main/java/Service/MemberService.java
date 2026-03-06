package Service;

import Dao.MemberDAO;
import Vo.MemberVO;
import util.PasswordUtil;
import java.time.format.DateTimeFormatter;

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
			if ("INACTIVE".equals(s)) {
				return new LoginResult(null, "휴면계정입니다. 고객센터에 문의해 주세요.");
			}
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
			if (!"ACTIVE".equals(s)) {
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
		vo.setMemberId(memberIdTrim);
		vo.setUsername(usernameTrim);
		vo.setNickname(nicknameTrim);

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
		String email = trimToNull(vo.getEmail());
		if (email != null && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			return "이메일 형식이 올바르지 않습니다.";
		}
		String hp = trimToNull(vo.getPhone());
		if (hp != null && !hp.matches("^\\d{10,11}$")) {
			return "핸드폰 번호는 숫자만 10~11자리로 입력해 주세요.";
		}

		if (memberDAO.existsMemberId(memberIdTrim)) {
			return "이미 사용 중인 아이디입니다.";
		}
		// 동명이인(이름/username) 가입 허용: 이름 중복 검사는 하지 않음
		if (memberDAO.existsNickname(nicknameTrim)) {
			return "이미 사용 중인 닉네임입니다.";
		}

		int inserted = memberDAO.insertMember(vo);
		return inserted == 1 ? null : "회원가입에 실패했습니다.";
	}

	private static String trimToNull(String s) {
		if (s == null)
			return null;
		String v = s.trim();
		return v.isEmpty() ? null : v;
	}
}