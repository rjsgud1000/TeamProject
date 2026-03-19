package Controller;

import Dao.BoardDAO;
import Dto.BoardDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/party/toggle")
public class PartyRecruitToggleController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String loginId = (String) session.getAttribute("loginId");
        boolean isAdmin = "admin".equalsIgnoreCase(loginId);

        int postId;
        try {
            postId = Integer.parseInt(request.getParameter("postId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=3");
            return;
        }

        String category = request.getParameter("category");
        String page = request.getParameter("page");
        if (category == null || category.isBlank()) category = "3";
        if (page == null || page.isBlank()) page = "1";

        BoardDTO post = boardDAO.selectPostById(postId);
        if (post == null || !"3".equals(post.getCategory())) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=3");
            return;
        }

        boolean isWriter = loginId.equals(post.getMemberId());
        if (!isWriter && !isAdmin) {
            response.sendRedirect(
                request.getContextPath()
                + "/board/detail?postId=" + postId
                + "&category=" + category
                + "&page=" + page
            );
            return;
        }

        boardDAO.togglePartyRecruitStatus(postId);

        response.sendRedirect(
            request.getContextPath()
            + "/board/detail?postId=" + postId
            + "&category=" + category
            + "&page=" + page
        );
    }
}