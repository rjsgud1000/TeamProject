package Service;

import Dao.MemberDAO;
import Vo.MemberVO;

public class MemberService {
	private final MemberDAO memberDAO = new MemberDAO();

	public MemberVO login(String username, String password) {
		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			return null;
		}
		return memberDAO.login(username.trim(), password);
	}
}