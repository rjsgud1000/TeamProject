package Controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Service.MemberService;
import Vo.MemberVO;

@WebServlet("/member/*")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final MemberService memberService = new MemberService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = request.getPathInfo();
		if (action == null) action = "";

		switch (action) {
		case "/login.me":
			request.setAttribute("center", "members/login.jsp");
			forward(request, response, "/main.jsp");
			return;
		case "/loginPro.me":
			loginPro(request, response);
			return;
		case "/logout.me":
			logout(request, response);
			return;
		case "/join.me":
			request.setAttribute("center", "members/join.jsp");
			forward(request, response, "/main.jsp");
			return;
		case "/joinPro.me":
			joinPro(request, response);
			return;
		case "/mypage.me":
			request.setAttribute("center", "members/mypage.jsp");
			forward(request, response, "/main.jsp");
			return;
		case "/checkId.me":
			checkId(request, response);
			return;
		case "/checkNickname.me":
			checkNickname(request, response);
			return;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	private void checkId(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = emptyToNull(request.getParameter("id"));
		response.setContentType("application/json; charset=UTF-8");
		if (id == null) {
			response.getWriter().write("{\"ok\":false,\"message\":\"아이디를 입력해 주세요.\"}");
			return;
		}
		// join()에서 중복 체크를 하므로 여기선 간단히 DAO 조회만 사용
		boolean exists = new Dao.MemberDAO().existsMemberId(id);
		if (exists) {
			response.getWriter().write("{\"ok\":false,\"message\":\"이미 사용 중인 아이디입니다.\"}");
		} else {
			response.getWriter().write("{\"ok\":true}");
		}
	}

	private void checkNickname(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String nickname = emptyToNull(request.getParameter("nickname"));
		response.setContentType("application/json; charset=UTF-8");
		if (nickname == null) {
			response.getWriter().write("{\"ok\":false,\"message\":\"닉네임을 입력해 주세요.\"}");
			return;
		}
		Dao.MemberDAO dao = new Dao.MemberDAO();
		boolean exists = dao.existsNickname(nickname);
		if (exists) {
			response.getWriter().write("{\"ok\":false,\"message\":\"이미 사용 중인 닉네임입니다.\"}");
		} else {
			response.getWriter().write("{\"ok\":true}");
		}
	}

	private void loginPro(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String id = request.getParameter("id");
		String pass = request.getParameter("pass");

		Service.MemberService.LoginResult result = memberService.loginWithReason(id, pass);
		MemberVO loginMember = result.member;
		if (loginMember == null) {
			request.setAttribute("loginError", result.error != null ? result.error : "아이디 또는 비밀번호가 올바르지 않습니다.");
			request.setAttribute("center", "members/login.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("loginMember", loginMember);
		session.setAttribute("loginId", loginMember.getId());
		session.setAttribute("loginName", loginMember.getNickname());
		session.setAttribute("loginFlash", "로그인하셨습니다.");

		response.sendRedirect(request.getContextPath() + "/main.jsp");
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.sendRedirect(request.getContextPath() + "/main.jsp");
	}

	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	private void joinPro(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");

		String id = request.getParameter("id");
		String pass = request.getParameter("pass");
		String name = request.getParameter("name");
		String nickname = request.getParameter("nickname");

		String zipcode = request.getParameter("address1");
		String addr1 = request.getParameter("address2");
		String addr2 = request.getParameter("address3");
		String addr3 = request.getParameter("address4");
		String addr4 = request.getParameter("address5");

		String gender = request.getParameter("gender");
		String email = request.getParameter("email");
		String hp = request.getParameter("hp");

		MemberVO vo = new MemberVO();
		vo.setMemberId(id);
		vo.setPasswordHash(pass);
		vo.setNameReal(emptyToNull(name));
		vo.setUsername(emptyToNull(name));
		vo.setNickname(nickname);

		vo.setZipcode(emptyToNull(zipcode));
		vo.setAddr1(emptyToNull(addr1));
		vo.setAddr2(emptyToNull(addr2));
		vo.setAddr3(emptyToNull(addr3));
		vo.setAddr4(emptyToNull(addr4));		
		vo.setGender(emptyToNull(gender));
		vo.setEmail(emptyToNull(email));
		vo.setPhone(emptyToNull(hp));

		String error = memberService.join(vo);
		if (error != null) {
			request.setAttribute("joinError", error);
			request.setAttribute("center", "members/join.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("joinFlash", "회원가입에 성공하셨습니다");
		response.sendRedirect(request.getContextPath() + "/member/login.me");
	}

	private static String emptyToNull(String s) {
		if (s == null) return null;
		String v = s.trim();
		return v.isEmpty() ? null : v;
	}
}