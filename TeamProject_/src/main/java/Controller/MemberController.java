package Controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

		populateLoginViewAttributes(request);

		switch (action) {
		case "/admin/list.me":
			showAdminMemberList(request, response);
			return;
		case "/admin/detail.me":
			showAdminMemberDetail(request, response);
			return;
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
			showMyPage(request, response);
			return;
		case "/editProfile.me":
			showEditProfile(request, response);
			return;
		case "/updateProfile.me":
			updateProfile(request, response);
			return;
		case "/withdraw.me":
			showWithdrawPage(request, response);
			return;
		case "/withdrawPro.me":
			withdrawPro(request, response);
			return;
		case "/checkId.me":
			checkId(request, response);
			return;
		case "/checkNickname.me":
			checkNickname(request, response);
			return;
		case "/checkProfileNickname.me":
			checkProfileNickname(request, response);
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

	private void checkProfileNickname(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		MemberVO sessionMember = getSessionLoginMember(request);
		if (sessionMember == null) {
			response.getWriter().write("{\"ok\":false,\"message\":\"로그인 후 이용해 주세요.\"}");
			return;
		}

		String nickname = emptyToNull(request.getParameter("nickname"));
		if (nickname == null) {
			response.getWriter().write("{\"ok\":false,\"message\":\"닉네임을 입력해 주세요.\"}");
			return;
		}

		Dao.MemberDAO dao = new Dao.MemberDAO();
		boolean exists = dao.existsNicknameExceptMemberId(nickname, sessionMember.getMemberId());
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
			populateLoginViewAttributes(request);
			request.setAttribute("loginError", result.error != null ? result.error : "아이디 또는 비밀번호가 올바르지 않습니다.");
			request.setAttribute("center", "members/login.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("loginMember", loginMember);
		session.setAttribute("loginId", loginMember.getId());
		session.setAttribute("loginName", loginMember.getNickname());
		session.setAttribute("loginRole", loginMember.getRole());
		session.setAttribute("isAdmin", isAdminRole(loginMember.getRole()));
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

	private void showMyPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO member = requireLoginMember(request, response);
		if (member == null) {
			return;
		}
		request.setAttribute("memberDetail", member);
		request.setAttribute("center", "members/mypage.jsp");
		forward(request, response, "/main.jsp");
	}

	private void showEditProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO member = requireLoginMember(request, response);
		if (member == null) {
			return;
		}
		request.setAttribute("memberDetail", member);
		request.setAttribute("center", "members/editProfile.jsp");
		forward(request, response, "/main.jsp");
	}

	private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		MemberVO sessionMember = getSessionLoginMember(request);
		if (sessionMember == null) {
			response.sendRedirect(request.getContextPath() + "/member/login.me");
			return;
		}

		MemberVO vo = new MemberVO();
		vo.setMemberId(sessionMember.getMemberId());
		vo.setNickname(request.getParameter("nickname"));
		vo.setZipcode(request.getParameter("zipcode"));
		vo.setAddr1(request.getParameter("addr1"));
		vo.setAddr2(request.getParameter("addr2"));
		vo.setAddr3(request.getParameter("addr3"));
		vo.setAddr4(request.getParameter("addr4"));
		vo.setGender(request.getParameter("gender"));
		vo.setEmail(request.getParameter("email"));
		vo.setPhone(request.getParameter("phone"));

		String error = memberService.updateProfile(vo, request.getParameter("newPassword"));
		if (error != null) {
			request.setAttribute("profileError", error);
			request.setAttribute("memberDetail", mergeProfileForView(sessionMember, vo));
			request.setAttribute("center", "members/editProfile.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		MemberVO refreshed = memberService.getMemberDetail(sessionMember.getMemberId());
		if (refreshed != null) {
			refreshLoginSession(request.getSession(), refreshed);
		}
		request.getSession().setAttribute("profileFlash", "회원정보가 수정되었습니다.");
		response.sendRedirect(request.getContextPath() + "/member/mypage.me");
	}

	private void showWithdrawPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO member = requireLoginMember(request, response);
		if (member == null) {
			return;
		}
		request.setAttribute("memberDetail", member);
		request.setAttribute("center", "members/withdraw.jsp");
		forward(request, response, "/main.jsp");
	}

	private void withdrawPro(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		MemberVO sessionMember = getSessionLoginMember(request);
		if (sessionMember == null) {
			response.sendRedirect(request.getContextPath() + "/member/login.me");
			return;
		}

		String confirm = emptyToNull(request.getParameter("confirmWithdraw"));
		if (confirm == null) {
			request.setAttribute("withdrawError", "회원탈퇴 확인에 동의해 주세요.");
			request.setAttribute("memberDetail", memberService.getMemberDetail(sessionMember.getMemberId()));
			request.setAttribute("center", "members/withdraw.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		String error = memberService.withdrawMember(sessionMember.getMemberId(), request.getParameter("password"));
		if (error != null) {
			request.setAttribute("withdrawError", error);
			request.setAttribute("memberDetail", memberService.getMemberDetail(sessionMember.getMemberId()));
			request.setAttribute("center", "members/withdraw.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.sendRedirect(request.getContextPath() + "/main.jsp");
	}

	private void showAdminMemberList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO admin = requireAdminMember(request, response);
		if (admin == null) {
			return;
		}
		String keyword = emptyToNull(request.getParameter("keyword"));
		String status = emptyToNull(request.getParameter("status"));
		List<MemberVO> members = memberService.getMembersForAdmin(keyword, status);
		Map<String, Integer> statusSummary = memberService.getMemberStatusSummary();

		request.setAttribute("adminMember", admin);
		request.setAttribute("memberList", members);
		request.setAttribute("statusSummary", statusSummary);
		request.setAttribute("roleLabelMap", buildRoleLabelMap());
		request.setAttribute("statusLabelMap", buildStatusLabelMap());
		request.setAttribute("keyword", keyword == null ? "" : keyword);
		request.setAttribute("selectedStatus", status == null ? "ALL" : status.toUpperCase());
		request.setAttribute("center", "admin/memberList.jsp");
		forward(request, response, "/main.jsp");
	}

	private void showAdminMemberDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO admin = requireAdminMember(request, response);
		if (admin == null) {
			return;
		}
		String memberId = emptyToNull(request.getParameter("memberId"));
		MemberVO detail = memberService.getMemberDetailForAdmin(memberId);
		if (detail == null) {
			request.getSession().setAttribute("adminMemberFlash", "회원 정보를 찾을 수 없습니다.");
			response.sendRedirect(request.getContextPath() + "/member/admin/list.me");
			return;
		}
		request.setAttribute("adminMember", admin);
		request.setAttribute("memberDetail", detail);
		request.setAttribute("roleLabelMap", buildRoleLabelMap());
		request.setAttribute("statusLabelMap", buildStatusLabelMap());
		request.setAttribute("memberCreatedAtText", formatDateTime(detail.getCreatedAt()));
		request.setAttribute("memberUpdatedAtText", formatDateTime(detail.getUpdatedAt()));
		request.setAttribute("memberSanctionEndAtText", formatDateTime(detail.getSanctionEndAt()));
		request.setAttribute("center", "admin/memberDetail.jsp");
		forward(request, response, "/main.jsp");
	}

	private Map<String, String> buildRoleLabelMap() {
		Map<String, String> roleLabelMap = new LinkedHashMap<>();
		roleLabelMap.put("USER", "유저");
		roleLabelMap.put("ADMIN", "관리자");
		return roleLabelMap;
	}

	private Map<String, String> buildStatusLabelMap() {
		Map<String, String> statusLabelMap = new LinkedHashMap<>();
		statusLabelMap.put("ALL", "전체");
		statusLabelMap.put("ACTIVE", "활성");
		statusLabelMap.put("INACTIVE", "휴면/비활성");
		statusLabelMap.put("BANNED", "제재");
		statusLabelMap.put("WITHDRAWN", "탈퇴");
		return statusLabelMap;
	}

	private String formatDateTime(java.time.LocalDateTime value) {
		if (value == null) {
			return "-";
		}
		return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}

	private MemberVO requireAdminMember(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MemberVO member = requireLoginMember(request, response);
		if (member == null) {
			return null;
		}
		if (!isAdminRole(member.getRole())) {
			response.sendRedirect(request.getContextPath() + "/main.jsp");
			return null;
		}
		return member;
	}

	private static String emptyToNull(String s) {
		if (s == null) return null;
		String v = s.trim();
		return v.isEmpty() ? null : v;
	}

	private void populateLoginViewAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.setAttribute("isAdmin", false);
			return;
		}

		Object loginMemberObj = session.getAttribute("loginMember");
		String loginRole = null;
		if (loginMemberObj instanceof MemberVO) {
			loginRole = ((MemberVO) loginMemberObj).getRole();
		}
		if (loginRole == null) {
			Object sessionRole = session.getAttribute("loginRole");
			if (sessionRole instanceof String) {
				loginRole = (String) sessionRole;
			}
		}

		request.setAttribute("loginRole", loginRole);
		request.setAttribute("isAdmin", isAdminRole(loginRole));
	}

	private boolean isAdminRole(String role) {
		return role != null && "ADMIN".equalsIgnoreCase(role.trim());
	}

	private MemberVO requireLoginMember(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MemberVO member = getSessionLoginMember(request);
		if (member == null) {
			response.sendRedirect(request.getContextPath() + "/member/login.me");
			return null;
		}
		MemberVO detail = memberService.getMemberDetail(member.getMemberId());
		if (detail == null) {
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/member/login.me");
			return null;
		}
		refreshLoginSession(request.getSession(), detail);
		return detail;
	}

	private MemberVO getSessionLoginMember(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		Object loginMemberObj = session.getAttribute("loginMember");
		return (loginMemberObj instanceof MemberVO) ? (MemberVO) loginMemberObj : null;
	}

	private void refreshLoginSession(HttpSession session, MemberVO member) {
		session.setAttribute("loginMember", member);
		session.setAttribute("loginId", member.getId());
		session.setAttribute("loginName", member.getNickname());
		session.setAttribute("loginRole", member.getRole());
		session.setAttribute("isAdmin", isAdminRole(member.getRole()));
	}

	private MemberVO mergeProfileForView(MemberVO base, MemberVO input) {
		MemberVO merged = new MemberVO();
		if (base != null) {
			merged.setMemberId(base.getMemberId());
			merged.setUsername(base.getUsername());
			merged.setNameReal(base.getNameReal());
			merged.setRole(base.getRole());
			merged.setStatus(base.getStatus());
		}
		if (input != null) {
			merged.setNickname(input.getNickname());
			merged.setZipcode(input.getZipcode());
			merged.setAddr1(input.getAddr1());
			merged.setAddr2(input.getAddr2());
			merged.setAddr3(input.getAddr3());
			merged.setAddr4(input.getAddr4());
			merged.setGender(input.getGender());
			merged.setEmail(input.getEmail());
			merged.setPhone(input.getPhone());
		}
		return merged;
	}
}