package Controller;

import Dao.BoardDAO;
import Vo.MemberVO;
import Dto.PostReportDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/postReportList")
public class AdminPostReportListController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        MemberVO loginUser = (session != null) ? (MemberVO) session.getAttribute("loginMember") : null;

        if (loginUser == null || !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        List<PostReportDTO> reportList = boardDAO.getPostReportList();
        request.setAttribute("reportList", reportList);
        request.setAttribute("center", "adminPostReportList.jsp");

        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}