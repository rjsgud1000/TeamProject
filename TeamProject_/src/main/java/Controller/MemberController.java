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
			forward(request, response, "/members/login.jsp");
			return;
		case "/loginPro.me":
			loginPro(request, response);
			return;
		case "/logout.me":
			logout(request, response);
			return;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	private void loginPro(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String id = request.getParameter("id");
		String pass = request.getParameter("pass");

		MemberVO loginMember = memberService.login(id, pass);
		if (loginMember == null) {
			request.setAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
			forward(request, response, "/members/login.jsp");
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("loginMember", loginMember);
		session.setAttribute("loginId", loginMember.getId());
		session.setAttribute("loginName", loginMember.getName());
		// 로그인 성공 후 메인에서 딱 1번만 보여줄 메시지
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
}