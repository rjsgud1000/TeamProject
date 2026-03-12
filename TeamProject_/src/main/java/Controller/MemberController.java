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
import Service.ReportService;
import Vo.MemberVO;
import Vo.CommentReportVO;
import util.RecaptchaUtil;

@WebServlet("/member/*")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String RECOVERY_MEMBER_ID_SESSION_KEY = "passwordRecoveryMemberId";
	private static final String RECOVERY_EMAIL_SESSION_KEY = "passwordRecoveryEmail";
	private static final String RECOVERY_CODE_SESSION_KEY = "passwordRecoveryCode";
	private static final String RECOVERY_EXPIRE_AT_SESSION_KEY = "passwordRecoveryExpireAt";
	private static final long RECOVERY_CODE_EXPIRE_MILLIS = 5L * 60L * 1000L;
	private static final String JOIN_EMAIL_SESSION_KEY = "joinEmailVerificationEmail";
	private static final String JOIN_EMAIL_CODE_SESSION_KEY = "joinEmailVerificationCode";
	private static final String JOIN_EMAIL_EXPIRE_AT_SESSION_KEY = "joinEmailVerificationExpireAt";
	private static final String JOIN_EMAIL_VERIFIED_SESSION_KEY = "joinEmailVerificationVerified";
	private static final long JOIN_EMAIL_CODE_EXPIRE_MILLIS = 5L * 60L * 1000L;
	private static final String PROFILE_EMAIL_SESSION_KEY = "profileEmailVerificationEmail";
	private static final String PROFILE_EMAIL_CODE_SESSION_KEY = "profileEmailVerificationCode";
	private static final String PROFILE_EMAIL_EXPIRE_AT_SESSION_KEY = "profileEmailVerificationExpireAt";
	private static final String PROFILE_EMAIL_VERIFIED_SESSION_KEY = "profileEmailVerificationVerified";
	private static final long PROFILE_EMAIL_CODE_EXPIRE_MILLIS = 5L * 60L * 1000L;
	private final MemberService memberService = new MemberService();
	private final ReportService reportService = new ReportService();
	private final RecaptchaUtil recaptchaUtil = new RecaptchaUtil();

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
		case "/admin/updateStatus.me":
			updateAdminMemberStatus(request, response);
			return;
		case "/admin/reportList.me":
			showAdminReportBoard(request, response);
			return;
		case "/admin/report/process.me":
			processAdminReport(request, response);
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
		case "/findPasswordForm.me":
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		case "/findPassword.me":
			sendPasswordRecoveryCode(request, response);
			return;
		case "/verifyPasswordCode.me":
			verifyPasswordRecoveryCode(request, response);
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
		case "/sendJoinEmailCode.me":
			sendJoinEmailCode(request, response);
			return;
		case "/verifyJoinEmailCode.me":
			verifyJoinEmailCode(request, response);
			return;
		case "/sendProfileEmailCode.me":
			sendProfileEmailCode(request, response);
			return;
		case "/verifyProfileEmailCode.me":
			verifyProfileEmailCode(request, response);
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
		String recaptchaToken = request.getParameter("g-recaptcha-response");

		RecaptchaUtil.VerificationResult recaptchaResult = recaptchaUtil.verify(recaptchaToken, request.getRemoteAddr());
		if (!recaptchaResult.success) {
			populateLoginViewAttributes(request);
			request.setAttribute("loginError", recaptchaResult.message);
			request.setAttribute("loginIdValue", id == null ? "" : id);
			request.setAttribute("center", "members/login.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		Service.MemberService.LoginResult result = memberService.loginWithReason(id, pass);
		MemberVO loginMember = result.member;
		if (loginMember == null) {
			populateLoginViewAttributes(request);
			request.setAttribute("loginError", result.error != null ? result.error : "아이디 또는 비밀번호가 올바르지 않습니다.");
			request.setAttribute("loginIdValue", id == null ? "" : id);
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
		session.setAttribute("loginFlash", (result.flash != null && !result.flash.isBlank()) ? result.flash : "로그인하셨습니다.");

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
		String email = request.getParameter("email");
		if (!isJoinEmailVerified(request.getSession(false), email)) {
			request.setAttribute("joinError", "이메일 인증을 완료해 주세요.");
			request.setAttribute("center", "members/join.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		String zipcode = request.getParameter("address1");
		String addr1 = request.getParameter("address2");
		String addr2 = request.getParameter("address3");
		String addr3 = request.getParameter("address4");
		String addr4 = request.getParameter("address5");

		String gender = request.getParameter("gender");
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
		clearJoinEmailVerificationSession(session);
		session.setAttribute("joinFlash", "회원가입에 성공하셨습니다");
		response.sendRedirect(request.getContextPath() + "/member/login.me");
	}

	private void sendJoinEmailCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		String email = emptyToNull(request.getParameter("email"));
		try {
			String code = memberService.sendJoinEmailVerificationCode(email);
			HttpSession session = request.getSession(true);
			session.setAttribute(JOIN_EMAIL_SESSION_KEY, email);
			session.setAttribute(JOIN_EMAIL_CODE_SESSION_KEY, code);
			session.setAttribute(JOIN_EMAIL_EXPIRE_AT_SESSION_KEY, System.currentTimeMillis() + JOIN_EMAIL_CODE_EXPIRE_MILLIS);
			session.setAttribute(JOIN_EMAIL_VERIFIED_SESSION_KEY, Boolean.FALSE);
			response.getWriter().write("{\"ok\":true,\"message\":\"인증번호를 메일로 발송했습니다.\"}");
		} catch (IllegalArgumentException e) {
			response.getWriter().write(toJsonError(e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write(toJsonError(e.getMessage() != null ? e.getMessage() : "인증번호 발송에 실패했습니다."));
		}
	}

	private void verifyJoinEmailCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		HttpSession session = request.getSession(false);
		String email = emptyToNull(request.getParameter("email"));
		String code = emptyToNull(request.getParameter("verificationCode"));
		if (email == null) {
			response.getWriter().write(toJsonError("이메일을 입력해 주세요."));
			return;
		}
		if (code == null) {
			response.getWriter().write(toJsonError("인증번호를 입력해 주세요."));
			return;
		}
		if (session == null) {
			response.getWriter().write(toJsonError("인증 세션이 만료되었습니다. 다시 요청해 주세요."));
			return;
		}

		String sessionEmail = (String) session.getAttribute(JOIN_EMAIL_SESSION_KEY);
		String sessionCode = (String) session.getAttribute(JOIN_EMAIL_CODE_SESSION_KEY);
		Long expireAt = (Long) session.getAttribute(JOIN_EMAIL_EXPIRE_AT_SESSION_KEY);
		if (sessionEmail == null || sessionCode == null || expireAt == null) {
			response.getWriter().write(toJsonError("인증번호를 먼저 요청해 주세요."));
			return;
		}
		if (!sessionEmail.equals(email)) {
			session.setAttribute(JOIN_EMAIL_VERIFIED_SESSION_KEY, Boolean.FALSE);
			response.getWriter().write(toJsonError("인증 요청한 이메일과 현재 이메일이 다릅니다."));
			return;
		}
		if (System.currentTimeMillis() > expireAt.longValue()) {
			clearJoinEmailVerificationSession(session);
			response.getWriter().write(toJsonError("인증번호 유효시간이 만료되었습니다. 다시 요청해 주세요."));
			return;
		}
		if (!sessionCode.equals(code)) {
			session.setAttribute(JOIN_EMAIL_VERIFIED_SESSION_KEY, Boolean.FALSE);
			response.getWriter().write(toJsonError("인증번호가 올바르지 않습니다."));
			return;
		}

		session.setAttribute(JOIN_EMAIL_VERIFIED_SESSION_KEY, Boolean.TRUE);
		response.getWriter().write("{\"ok\":true,\"message\":\"이메일 인증이 완료되었습니다.\"}");
	}

	private void sendProfileEmailCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		MemberVO sessionMember = getSessionLoginMember(request);
		if (sessionMember == null) {
			response.getWriter().write(toJsonError("로그인 후 이용해 주세요."));
			return;
		}
		String email = emptyToNull(request.getParameter("email"));
		try {
			String currentEmail = emptyToNull(sessionMember.getEmail());
			if (email == null) {
				throw new IllegalArgumentException("이메일을 입력해 주세요.");
			}
			if (currentEmail != null && currentEmail.equalsIgnoreCase(email)) {
				throw new IllegalArgumentException("현재 사용 중인 이메일과 동일합니다. 다른 이메일을 입력해 주세요.");
			}
			String code = memberService.sendJoinEmailVerificationCode(email);
			HttpSession session = request.getSession(true);
			session.setAttribute(PROFILE_EMAIL_SESSION_KEY, email);
			session.setAttribute(PROFILE_EMAIL_CODE_SESSION_KEY, code);
			session.setAttribute(PROFILE_EMAIL_EXPIRE_AT_SESSION_KEY, System.currentTimeMillis() + PROFILE_EMAIL_CODE_EXPIRE_MILLIS);
			session.setAttribute(PROFILE_EMAIL_VERIFIED_SESSION_KEY, Boolean.FALSE);
			response.getWriter().write("{\"ok\":true,\"message\":\"인증번호를 메일로 발송했습니다.\"}");
		} catch (IllegalArgumentException e) {
			response.getWriter().write(toJsonError(e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write(toJsonError(e.getMessage() != null ? e.getMessage() : "인증번호 발송에 실패했습니다."));
		}
	}

	private void verifyProfileEmailCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		HttpSession session = request.getSession(false);
		MemberVO sessionMember = getSessionLoginMember(request);
		if (sessionMember == null) {
			response.getWriter().write(toJsonError("로그인 후 이용해 주세요."));
			return;
		}
		String email = emptyToNull(request.getParameter("email"));
		String code = emptyToNull(request.getParameter("verificationCode"));
		if (email == null) {
			response.getWriter().write(toJsonError("이메일을 입력해 주세요."));
			return;
		}
		if (code == null) {
			response.getWriter().write(toJsonError("인증번호를 입력해 주세요."));
			return;
		}
		if (session == null) {
			response.getWriter().write(toJsonError("인증 세션이 만료되었습니다. 다시 요청해 주세요."));
			return;
		}

		String sessionEmail = (String) session.getAttribute(PROFILE_EMAIL_SESSION_KEY);
		String sessionCode = (String) session.getAttribute(PROFILE_EMAIL_CODE_SESSION_KEY);
		Long expireAt = (Long) session.getAttribute(PROFILE_EMAIL_EXPIRE_AT_SESSION_KEY);
		if (sessionEmail == null || sessionCode == null || expireAt == null) {
			response.getWriter().write(toJsonError("인증번호를 먼저 요청해 주세요."));
			return;
		}
		if (!sessionEmail.equals(email)) {
			session.setAttribute(PROFILE_EMAIL_VERIFIED_SESSION_KEY, Boolean.FALSE);
			response.getWriter().write(toJsonError("인증 요청한 이메일과 현재 이메일이 다릅니다."));
			return;
		}
		if (System.currentTimeMillis() > expireAt.longValue()) {
			clearProfileEmailVerificationSession(session);
			response.getWriter().write(toJsonError("인증번호 유효시간이 만료되었습니다. 다시 요청해 주세요."));
			return;
		}
		if (!sessionCode.equals(code)) {
			session.setAttribute(PROFILE_EMAIL_VERIFIED_SESSION_KEY, Boolean.FALSE);
			response.getWriter().write(toJsonError("인증번호가 올바르지 않습니다."));
			return;
		}

		session.setAttribute(PROFILE_EMAIL_VERIFIED_SESSION_KEY, Boolean.TRUE);
		response.getWriter().write("{\"ok\":true,\"message\":\"이메일 인증이 완료되었습니다.\"}");
	}

	private boolean isJoinEmailVerified(HttpSession session, String email) {
		String mail = emptyToNull(email);
		if (session == null || mail == null) {
			return false;
		}
		String verifiedEmail = (String) session.getAttribute(JOIN_EMAIL_SESSION_KEY);
		Object verifiedObj = session.getAttribute(JOIN_EMAIL_VERIFIED_SESSION_KEY);
		Long expireAt = (Long) session.getAttribute(JOIN_EMAIL_EXPIRE_AT_SESSION_KEY);
		if (!(verifiedObj instanceof Boolean) || !((Boolean) verifiedObj).booleanValue()) {
			return false;
		}
		if (verifiedEmail == null || !mail.equals(verifiedEmail)) {
			return false;
		}
		if (expireAt == null || System.currentTimeMillis() > expireAt.longValue()) {
			clearJoinEmailVerificationSession(session);
			return false;
		}
		return true;
	}

	private boolean isProfileEmailVerified(HttpSession session, String email) {
		String mail = emptyToNull(email);
		if (session == null || mail == null) {
			return false;
		}
		String verifiedEmail = (String) session.getAttribute(PROFILE_EMAIL_SESSION_KEY);
		Object verifiedObj = session.getAttribute(PROFILE_EMAIL_VERIFIED_SESSION_KEY);
		Long expireAt = (Long) session.getAttribute(PROFILE_EMAIL_EXPIRE_AT_SESSION_KEY);
		if (!(verifiedObj instanceof Boolean) || !((Boolean) verifiedObj).booleanValue()) {
			return false;
		}
		if (verifiedEmail == null || !mail.equals(verifiedEmail)) {
			return false;
		}
		if (expireAt == null || System.currentTimeMillis() > expireAt.longValue()) {
			clearProfileEmailVerificationSession(session);
			return false;
		}
		return true;
	}

	private void clearJoinEmailVerificationSession(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(JOIN_EMAIL_SESSION_KEY);
		session.removeAttribute(JOIN_EMAIL_CODE_SESSION_KEY);
		session.removeAttribute(JOIN_EMAIL_EXPIRE_AT_SESSION_KEY);
		session.removeAttribute(JOIN_EMAIL_VERIFIED_SESSION_KEY);
	}

	private void clearProfileEmailVerificationSession(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(PROFILE_EMAIL_SESSION_KEY);
		session.removeAttribute(PROFILE_EMAIL_CODE_SESSION_KEY);
		session.removeAttribute(PROFILE_EMAIL_EXPIRE_AT_SESSION_KEY);
		session.removeAttribute(PROFILE_EMAIL_VERIFIED_SESSION_KEY);
	}

	private String toJsonError(String message) {
		String safe = message == null ? "요청 처리에 실패했습니다." : message.replace("\\", "\\\\").replace("\"", "\\\"");
		return "{\"ok\":false,\"message\":\"" + safe + "\"}";
	}

	private void sendPasswordRecoveryCode(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String memberId = emptyToNull(request.getParameter("findId"));
		String email = emptyToNull(request.getParameter("findEmail"));
		MemberService.PasswordRecoveryCandidate candidate = memberService.validatePasswordRecoveryTarget(memberId, email);
		if (candidate.error != null || candidate.member == null) {
			request.setAttribute("passwordFindError", candidate.error != null ? candidate.error : "회원 정보를 찾을 수 없습니다.");
			request.setAttribute("passwordFindId", memberId == null ? "" : memberId);
			request.setAttribute("passwordFindEmail", email == null ? "" : email);
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		try {
			String code = memberService.sendPasswordRecoveryCode(candidate.member.getEmail());
			HttpSession session = request.getSession(true);
			session.setAttribute(RECOVERY_MEMBER_ID_SESSION_KEY, candidate.member.getMemberId());
			session.setAttribute(RECOVERY_EMAIL_SESSION_KEY, candidate.member.getEmail());
			session.setAttribute(RECOVERY_CODE_SESSION_KEY, code);
			session.setAttribute(RECOVERY_EXPIRE_AT_SESSION_KEY, System.currentTimeMillis() + RECOVERY_CODE_EXPIRE_MILLIS);
			request.setAttribute("passwordFindMessage", "인증번호를 메일로 발송했습니다. 5분 안에 인증을 완료해 주세요.");
			request.setAttribute("passwordFindId", candidate.member.getMemberId());
			request.setAttribute("passwordFindEmail", candidate.member.getEmail());
			request.setAttribute("passwordCodeSent", true);
		} catch (IllegalArgumentException e) {
			request.setAttribute("passwordFindError", e.getMessage());
			request.setAttribute("passwordFindId", memberId == null ? "" : memberId);
			request.setAttribute("passwordFindEmail", email == null ? "" : email);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("passwordFindError", e.getMessage() != null ? e.getMessage() : "메일 발송에 실패했습니다.");
			request.setAttribute("passwordFindId", memberId == null ? "" : memberId);
			request.setAttribute("passwordFindEmail", email == null ? "" : email);
		}
		request.setAttribute("center", "members/findPassword.jsp");
		forward(request, response, "/main.jsp");
	}

	private void verifyPasswordRecoveryCode(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String memberId = emptyToNull(request.getParameter("findId"));
		String email = emptyToNull(request.getParameter("findEmail"));
		String inputCode = emptyToNull(request.getParameter("verificationCode"));
		request.setAttribute("passwordCodeSent", true);
		request.setAttribute("passwordFindId", memberId == null ? "" : memberId);
		request.setAttribute("passwordFindEmail", email == null ? "" : email);

		if (inputCode == null) {
			request.setAttribute("passwordFindError", "인증번호를 입력해 주세요.");
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		HttpSession session = request.getSession(false);
		if (session == null) {
			request.setAttribute("passwordFindError", "인증 세션이 만료되었습니다. 다시 시도해 주세요.");
			request.setAttribute("passwordCodeSent", false);
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		String sessionMemberId = (String) session.getAttribute(RECOVERY_MEMBER_ID_SESSION_KEY);
		String sessionEmail = (String) session.getAttribute(RECOVERY_EMAIL_SESSION_KEY);
		String sessionCode = (String) session.getAttribute(RECOVERY_CODE_SESSION_KEY);
		Long expireAt = (Long) session.getAttribute(RECOVERY_EXPIRE_AT_SESSION_KEY);
		if (sessionMemberId == null || sessionEmail == null || sessionCode == null || expireAt == null) {
			request.setAttribute("passwordFindError", "인증 정보가 없습니다. 다시 인증번호를 요청해 주세요.");
			request.setAttribute("passwordCodeSent", false);
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}
		if (System.currentTimeMillis() > expireAt.longValue()) {
			clearPasswordRecoverySession(session);
			request.setAttribute("passwordFindError", "인증번호 유효시간이 만료되었습니다. 다시 요청해 주세요.");
			request.setAttribute("passwordCodeSent", false);
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}
		if (!sessionMemberId.equals(memberId) || !sessionEmail.equals(email)) {
			request.setAttribute("passwordFindError", "인증 요청 정보와 입력값이 일치하지 않습니다.");
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}
		if (!sessionCode.equals(inputCode)) {
			request.setAttribute("passwordFindError", "인증번호가 올바르지 않습니다.");
			request.setAttribute("center", "members/findPassword.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		try {
			memberService.issueTemporaryPassword(memberId, email);
			clearPasswordRecoverySession(session);
			request.setAttribute("passwordCodeSent", false);
			request.setAttribute("passwordFindMessage", "인증이 완료되었습니다. 임시 비밀번호를 메일로 발송했습니다.");
		} catch (IllegalArgumentException e) {
			request.setAttribute("passwordFindError", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("passwordFindError", e.getMessage() != null ? e.getMessage() : "임시 비밀번호 발급에 실패했습니다.");
		}

		request.setAttribute("center", "members/findPassword.jsp");
		forward(request, response, "/main.jsp");
	}

	private void clearPasswordRecoverySession(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(RECOVERY_MEMBER_ID_SESSION_KEY);
		session.removeAttribute(RECOVERY_EMAIL_SESSION_KEY);
		session.removeAttribute(RECOVERY_CODE_SESSION_KEY);
		session.removeAttribute(RECOVERY_EXPIRE_AT_SESSION_KEY);
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

		String newEmail = emptyToNull(vo.getEmail());
		String currentEmail = emptyToNull(sessionMember.getEmail());
		boolean emailChanged = (currentEmail == null && newEmail != null) || (currentEmail != null && !currentEmail.equals(newEmail));
		if (emailChanged && !isProfileEmailVerified(request.getSession(false), newEmail)) {
			request.setAttribute("profileError", "이메일을 변경한 경우 이메일 인증을 완료해 주세요.");
			request.setAttribute("memberDetail", mergeProfileForView(sessionMember, vo));
			request.setAttribute("center", "members/editProfile.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		String error = memberService.updateProfile(vo, request.getParameter("newPassword"));
		if (error != null) {
			request.setAttribute("profileError", error);
			request.setAttribute("memberDetail", mergeProfileForView(sessionMember, vo));
			request.setAttribute("center", "members/editProfile.jsp");
			forward(request, response, "/main.jsp");
			return;
		}

		if (emailChanged) {
			clearProfileEmailVerificationSession(request.getSession(false));
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

	private void updateAdminMemberStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MemberVO admin = requireAdminMember(request, response);
		if (admin == null) {
			return;
		}
		String memberId = emptyToNull(request.getParameter("memberId"));
		String status = emptyToNull(request.getParameter("status"));
		String sanctionReason = emptyToNull(request.getParameter("sanctionReason"));
		String sanctionEndAt = emptyToNull(request.getParameter("sanctionEndAt"));
		String error = memberService.updateMemberStatusByAdmin(admin.getMemberId(), memberId, status, sanctionReason, sanctionEndAt);
		HttpSession session = request.getSession();
		if (error != null) {
			session.setAttribute("adminMemberFlash", error);
		} else {
			session.setAttribute("adminMemberFlash", "회원 상태가 변경되었습니다.");
		}
		response.sendRedirect(request.getContextPath() + "/member/admin/detail.me?memberId=" + java.net.URLEncoder.encode(memberId == null ? "" : memberId, "UTF-8"));
	}

	private void showAdminReportBoard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO admin = requireAdminMember(request, response);
		if (admin == null) {
			return;
		}
		List<CommentReportVO> reports = reportService.getCommentReports();
		int pendingCount = 0;
		int completedCount = 0;
		for (CommentReportVO report : reports) {
			if (report.isProcessed()) {
				completedCount++;
			} else {
				pendingCount++;
			}
		}
		request.setAttribute("adminMember", admin);
		request.setAttribute("commentReportList", reports);
		request.setAttribute("reportCount", reports.size());
		request.setAttribute("pendingReportCount", pendingCount);
		request.setAttribute("completedReportCount", completedCount);
		request.setAttribute("center", "admin/reportBoard.jsp");
		forward(request, response, "/main.jsp");
	}

	private void processAdminReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MemberVO admin = requireAdminMember(request, response);
		if (admin == null) {
			return;
		}
		int reportId = 0;
		try {
			reportId = Integer.parseInt(request.getParameter("reportId"));
		} catch (Exception ignore) {
		}
		reportService.processCommentReport(reportId);
		response.sendRedirect(request.getContextPath() + "/member/admin/reportList.me");
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
		statusLabelMap.put("WARNING", "경고");
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
		request.setAttribute("recaptchaSiteKey", recaptchaUtil.getSiteKey());
		request.setAttribute("recaptchaEnabled", Boolean.valueOf(recaptchaUtil.isConfigured()));
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
