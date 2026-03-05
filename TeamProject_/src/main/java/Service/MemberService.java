package Service;

import Dao.MemberDAO;
import Vo.MemberVO;
import util.PasswordUtil;

public class MemberService {
	private final MemberDAO memberDAO = new MemberDAO();

	public MemberVO login(String memberId, String password) {
		if (memberId == null || memberId.isBlank() || password == null || password.isBlank()) {
			return null;
		}
		String id = memberId.trim();

		MemberVO member = memberDAO.findByMemberId(id);
		if (member == null)
			return null;
		if (member.getStatus() != null && !"ACTIVE".equalsIgnoreCase(member.getStatus()))
			return null;

		String stored = member.getPasswordHash();
		boolean ok;
		if (PasswordUtil.isHashed(stored)) {
			ok = PasswordUtil.matches(password, stored);
		} else {
			// 기존 데이터(평문) 호환
			ok = password.equals(stored);
			// 로그인 성공 시 해시로 업그레이드
			if (ok) {
				String newHash = PasswordUtil.hash(password);
				memberDAO.updatePasswordHash(id, newHash);
				member.setPasswordHash(newHash);
			}
		}

		return ok ? member : null;
	}

	public String join(MemberVO vo) {
		if (vo == null || vo.getMemberId() == null || vo.getMemberId().isBlank()) {
			return "아이디는 필수입니다.";
		}
		if (vo.getPasswordHash() == null || vo.getPasswordHash().isBlank()) {
			return "비밀번호는 필수입니다.";
		}
		if (vo.getUsername() == null || vo.getUsername().isBlank()) {
			return "닉네임은 필수입니다.";
		}

		String memberIdTrim = vo.getMemberId().trim();
		String usernameTrim = vo.getUsername().trim();
		vo.setMemberId(memberIdTrim);
		vo.setUsername(usernameTrim);

		// password_hash 컬럼에는 해시를 저장
		String rawPassword = vo.getPasswordHash();
		vo.setPasswordHash(PasswordUtil.hash(rawPassword));

		// nickname 컬럼은 프로젝트/DB 호환용으로 동일 값 사용
		if (vo.getNickname() == null || vo.getNickname().isBlank()) {
			vo.setNickname(usernameTrim);
		}
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
		if (memberDAO.existsUsername(usernameTrim) || memberDAO.existsNickname(usernameTrim)) {
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