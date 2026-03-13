package Controller;

import Dao.BoardDAO;
import Vo.MemberVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/unblindPost")
public class AdminUnblindPostController extends HttpServlet {

    private BoardDAO boardDAO;

    @Override
    public void init() {
        boardDAO = new BoardDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        MemberVO loginUser = (session != null) ? (MemberVO) session.getAttribute("loginMember") : null;

        if (loginUser == null || !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));

        boolean result = boardDAO.unblindPost(postId);

        if (result) {
            System.out.println("게시글 블라인드 해제 성공: " + postId);
        } else {
            System.out.println("게시글 블라인드 해제 실패: " + postId);
        }

        response.sendRedirect(request.getContextPath() + "/admin/postReportList");
    }
}