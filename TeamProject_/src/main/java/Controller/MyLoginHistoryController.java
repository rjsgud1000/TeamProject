package Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dao.MemberDAO;
import Vo.LoginHistoryVO;
import Vo.MemberVO;

@WebServlet("/mypage/login-history")
public class MyLoginHistoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int PAGE_SIZE = 10;
	private static final int PAGE_BLOCK_SIZE = 10;
	private static final DateTimeFormatter LOGIN_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final MemberDAO memberDAO = new MemberDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberVO loginMember = getLoginMember(request);
		if (loginMember == null) {
			response.sendRedirect(request.getContextPath() + "/member/login.me");
			return;
		}

		String memberId = loginMember.getMemberId();
		int loginHistoryCount = memberDAO.countLoginHistoriesByMemberId(memberId);
		int totalPage = Math.max(1, (int) Math.ceil(loginHistoryCount / (double) PAGE_SIZE));
		int currentPage = parsePage(request.getParameter("page"), totalPage);
		int offset = (currentPage - 1) * PAGE_SIZE;
		List<LoginHistoryVO> loginHistoryList = memberDAO.findLoginHistoriesByMemberId(memberId, offset, PAGE_SIZE);
		for (LoginHistoryVO history : loginHistoryList) {
			history.setUserAgentSummary(summarizeUserAgent(history.getUserAgent()));
			history.setLoginResultLabel(toLoginResultLabel(history.getLoginResult()));
			history.setFailReasonLabel(toFailReasonLabel(history.getFailReason(), history.getLoginResult()));
			history.setLoginAtText(formatLoginAt(history.getLoginAt()));
		}

		int startPage = ((currentPage - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;
		int endPage = Math.min(startPage + PAGE_BLOCK_SIZE - 1, totalPage);

		request.setAttribute("loginHistoryList", loginHistoryList);
		request.setAttribute("loginHistoryCount", loginHistoryCount);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("startPage", startPage);
		request.setAttribute("endPage", endPage);
		request.setAttribute("pageTitle", "로그인 기록 보기");
		request.setAttribute("center", "members/loginHistory.jsp");
		forward(request, response, "/main.jsp");
	}

	private String toLoginResultLabel(String loginResult) {
		if (loginResult == null) {
			return "알 수 없음";
		}
		switch (loginResult.trim().toUpperCase()) {
		case "SUCCESS":
			return "로그인 성공";
		case "FAIL":
			return "로그인 실패";
		case "BLOCKED":
			return "로그인 차단";
		default:
			return loginResult;
		}
	}

	private String toFailReasonLabel(String failReason, String loginResult) {
		if (failReason == null || failReason.isBlank()) {
			return "SUCCESS".equalsIgnoreCase(loginResult) ? "-" : "사유 없음";
		}
		switch (failReason.trim().toUpperCase()) {
		case "BAD_PASSWORD":
			return "비밀번호가 일치하지 않습니다.";
		case "ACCOUNT_BANNED":
			return "제재된 계정입니다.";
		case "WITHDRAWN_MEMBER":
			return "탈퇴한 계정입니다.";
		case "NOT_FOUND":
			return "존재하지 않는 아이디입니다.";
		case "INACTIVE_ACCOUNT":
			return "비활성 계정입니다.";
		default:
			return failReason;
		}
	}

	private String formatLoginAt(LocalDateTime loginAt) {
		if (loginAt == null) {
			return "-";
		}
		return loginAt.format(LOGIN_AT_FORMATTER);
	}

	private String summarizeUserAgent(String userAgent) {
		if (userAgent == null || userAgent.isBlank()) {
			return "알 수 없는 브라우저";
		}
		String ua = userAgent;
		String browser = detectBrowser(ua);
		String version = detectBrowserVersion(ua);
		String os = detectOs(ua);
		String browserText = version == null ? browser : browser + " " + version;
		return os == null ? browserText : browserText + " · " + os;
	}

	private String detectBrowser(String ua) {
		if (ua.contains("Edg/")) return "Edge";
		if (ua.contains("OPR/") || ua.contains("Opera")) return "Opera";
		if (ua.contains("Whale/")) return "Whale";
		if (ua.contains("Chrome/")) return "Chrome";
		if (ua.contains("Firefox/")) return "Firefox";
		if (ua.contains("Safari/") && ua.contains("Version/")) return "Safari";
		return "알 수 없는 브라우저";
	}

	private String detectBrowserVersion(String ua) {
		String token = null;
		if (ua.contains("Edg/")) token = "Edg/";
		else if (ua.contains("OPR/")) token = "OPR/";
		else if (ua.contains("Whale/")) token = "Whale/";
		else if (ua.contains("Chrome/")) token = "Chrome/";
		else if (ua.contains("Firefox/")) token = "Firefox/";
		else if (ua.contains("Version/")) token = "Version/";
		if (token == null) {
			return null;
		}
		int start = ua.indexOf(token);
		if (start < 0) {
			return null;
		}
		start += token.length();
		int end = ua.indexOf(' ', start);
		String version = end >= 0 ? ua.substring(start, end) : ua.substring(start);
		int dot = version.indexOf('.');
		return dot > 0 ? version.substring(0, dot) : version;
	}

	private String detectOs(String ua) {
		if (ua.contains("Windows NT 10.0")) return "Windows 10";
		if (ua.contains("Windows NT 6.3")) return "Windows 8.1";
		if (ua.contains("Windows NT 6.1")) return "Windows 7";
		if (ua.contains("Android")) return "Android";
		if (ua.contains("iPhone")) return "iPhone";
		if (ua.contains("iPad")) return "iPad";
		if (ua.contains("Mac OS X")) return "macOS";
		if (ua.contains("Linux")) return "Linux";
		return null;
	}

	private int parsePage(String pageParam, int totalPage) {
		int page = 1;
		try {
			page = Integer.parseInt(pageParam);
		} catch (Exception ignore) {
		}
		if (page < 1) {
			page = 1;
		}
		if (page > totalPage) {
			page = totalPage;
		}
		return page;
	}

	private MemberVO getLoginMember(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		Object loginMember = session.getAttribute("loginMember");
		return (loginMember instanceof MemberVO) ? (MemberVO) loginMember : null;
	}

	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}
}
