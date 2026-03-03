package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.CommentService;

@WebServlet("/comment/*")
public class CommentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CommentService commentService = new CommentService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/write.do": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                long postId = Long.parseLong(request.getParameter("postId"));
                String content = request.getParameter("content");
                String loginId = (String) request.getSession().getAttribute("id");

                commentService.write(postId, loginId, content);
                response.sendRedirect(request.getContextPath() + "/community/detail.do?id=" + postId);
                break;
            }
            case "/update.do": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                long postId = Long.parseLong(request.getParameter("postId"));
                long commentId = Long.parseLong(request.getParameter("commentId"));
                String content = request.getParameter("content");
                String loginId = (String) request.getSession().getAttribute("id");

                boolean ok;
                try {
                    ok = commentService.update(commentId, loginId, content);
                } catch (IllegalArgumentException ex) {
                    ok = false;
                }

                if (!ok) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                response.sendRedirect(request.getContextPath() + "/community/detail.do?id=" + postId);
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void ensureLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("id") == null) {
            response.sendRedirect(request.getContextPath() + "/member/loginForm.me");
        }
    }
}