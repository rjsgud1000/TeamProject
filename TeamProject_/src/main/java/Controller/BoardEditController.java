package Controller;

import Dao.BoardDAO;
import Dto.BoardDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/edit")
public class BoardEditController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    private boolean canEdit(BoardDTO post, String loginId, boolean isAdmin) {
        if (post == null || loginId == null) return false;
        if (isAdmin) return true;
        return loginId.equals(post.getMemberId());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String loginId = (String) session.getAttribute("loginId");
        boolean isAdmin = "admin".equalsIgnoreCase(loginId);

        int postId = Integer.parseInt(request.getParameter("postId"));
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        BoardDTO post = boardDAO.selectPostById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        if (!canEdit(post, loginId, isAdmin)) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/board/detail?postId=" + postId
                    + "&category=" + category
                    + "&page=" + page
            );
            return;
        }

        request.setAttribute("post", post);
        request.setAttribute("category", category);
        request.setAttribute("page", page);

        request.setAttribute("center", "boardEdit.jsp");
        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

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

        int postId = Integer.parseInt(request.getParameter("postId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        if (category == null) category = "1";
        if (page == null) page = "1";

        BoardDTO post = boardDAO.selectPostById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        if (!canEdit(post, loginId, isAdmin)) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/board/detail?postId=" + postId
                    + "&category=" + category
                    + "&page=" + page
            );
            return;
        }

        Integer recruitStatus = post.getRecruitStatus();
        Integer currentMembers = post.getCurrentMembers();
        Integer maxMembers = post.getMaxMembers();

        if ("3".equals(post.getCategory())) {
            try {
                recruitStatus = Integer.parseInt(request.getParameter("recruitStatus"));
            } catch (Exception ignored) {}

            try {
                currentMembers = Integer.parseInt(request.getParameter("currentMembers"));
            } catch (Exception ignored) {}

            try {
                maxMembers = Integer.parseInt(request.getParameter("maxMembers"));
            } catch (Exception ignored) {}

            if (currentMembers == null || currentMembers < 1) currentMembers = 1;
            if (maxMembers == null || maxMembers < 1) maxMembers = 1;
            if (currentMembers > maxMembers) currentMembers = maxMembers;

            if (currentMembers >= maxMembers) {
                recruitStatus = 0;
            } else if (recruitStatus == null) {
                recruitStatus = 1;
            }
        } else {
            recruitStatus = null;
            currentMembers = null;
            maxMembers = null;
        }

        boardDAO.updatePost(postId, title, content, recruitStatus, currentMembers, maxMembers);

        response.sendRedirect(
                request.getContextPath()
                + "/board/detail?postId=" + postId
                + "&category=" + category
                + "&page=" + page
        );
    }
}