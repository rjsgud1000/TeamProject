package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.AdminService;
import vo.Member;

/**
 * Admin-only features.
 *
 * 관리자 계정: admin1/admin2/admin3 (DB에서 role=ADMIN으로 설정)
 *
 * URLs:
 *  - /admin/members.do (조회)
 *  - /admin/memberDetail.do?loginId=xxx
 *  - /admin/memberUpdate.do (POST)
 *  - /admin/memberDelete.do (POST)
 *  - /admin/memberSanction.do (POST)
 */
@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!ensureAdmin(request, response)) return;

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/members.do": {
                List<Member> members = adminService.listMembers();
                request.setAttribute("members", members);
                forward(request, response, "/WEB-INF/views/admin/members.jsp");
                break;
            }
            case "/memberDetail.do": {
                String loginId = request.getParameter("loginId");
                Member m = adminService.getMember(loginId);
                if (m == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("member", m);
                forward(request, response, "/WEB-INF/views/admin/memberDetail.jsp");
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!ensureAdmin(request, response)) return;

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/memberUpdate.do": {
                String loginId = request.getParameter("loginId");
                String nickname = request.getParameter("nickname");
                String role = request.getParameter("role");
                adminService.updateMember(loginId, nickname, role);
                response.sendRedirect(request.getContextPath() + "/admin/memberDetail.do?loginId=" + loginId);
                break;
            }
            case "/memberDelete.do": {
                String loginId = request.getParameter("loginId");
                adminService.deleteMember(loginId);
                response.sendRedirect(request.getContextPath() + "/admin/members.do");
                break;
            }
            case "/memberSanction.do": {
                String loginId = request.getParameter("loginId");
                int days = Integer.parseInt(request.getParameter("days"));
                adminService.sanctionMember(loginId, days);
                response.sendRedirect(request.getContextPath() + "/admin/memberDetail.do?loginId=" + loginId);
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean ensureAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) == null) {
            response.sendRedirect(request.getContextPath() + "/member/loginForm.me");
            return false;
        }
        String role = (String) request.getSession(false).getAttribute("role");
        if (!"ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(path);
        rd.forward(request, response);
    }
}
