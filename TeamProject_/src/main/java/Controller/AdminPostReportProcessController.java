package Controller;

import Dao.BoardDAO;
import Vo.MemberVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/postReportProcess")
public class AdminPostReportProcessController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        MemberVO loginUser = (session != null) ? (MemberVO) session.getAttribute("loginMember") : null;

        if (loginUser == null || !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        int reportId = Integer.parseInt(request.getParameter("reportId"));
        int postId = Integer.parseInt(request.getParameter("postId"));
        String status = request.getParameter("status");

        if (!"RESOLVED".equals(status) && !"REJECTED".equals(status)) {
            response.sendRedirect(request.getContextPath() + "/admin/postReportList");
            return;
        }

        boolean reportUpdated = boardDAO.updatePostReportStatus(reportId, status, loginUser.getMemberId());

        if (reportUpdated && "RESOLVED".equals(status)) {
            boardDAO.blindPost(postId);
        }

        response.sendRedirect(request.getContextPath() + "/admin/postReportList");
    }
}