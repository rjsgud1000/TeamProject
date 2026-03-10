package Controller;

import Dao.BoardDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/delete")
public class BoardDeleteController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int postId = Integer.parseInt(request.getParameter("postId"));
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        boardDAO.deletePost(postId);

        response.sendRedirect(
                request.getContextPath() +
                "/board/list?category=" + category + "&page=" + page
        );
    }
}