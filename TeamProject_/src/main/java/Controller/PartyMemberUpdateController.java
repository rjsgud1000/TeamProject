package Controller;

import Dao.BoardDAO;
import Dto.BoardDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/party/update")
public class PartyMemberUpdateController extends HttpServlet {

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
        if (category == null || category.trim().isEmpty()) category = "3";
        if (page == null || page.trim().isEmpty()) page = "1";

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

        int currentMembers;
        int maxMembers;

        try {
            currentMembers = Integer.parseInt(request.getParameter("currentMembers"));
        } catch (Exception e) {
            currentMembers = (post.getCurrentMembers() != null) ? post.getCurrentMembers() : 1;
        }

        try {
            maxMembers = Integer.parseInt(request.getParameter("maxMembers"));
        } catch (Exception e) {
            maxMembers = (post.getMaxMembers() != null) ? post.getMaxMembers() : 1;
        }

        if (currentMembers < 1) currentMembers = 1;
        if (maxMembers < 1) maxMembers = 1;
        if (currentMembers > maxMembers) currentMembers = maxMembers;

        boardDAO.updatePartyMembers(postId, currentMembers, maxMembers);

        response.sendRedirect(
            request.getContextPath()
            + "/board/detail?postId=" + postId
            + "&category=" + category
            + "&page=" + page
        );
    }
}