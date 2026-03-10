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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int postId = Integer.parseInt(request.getParameter("postId"));
        String category = request.getParameter("category");

        BoardDTO post = boardDAO.selectPostById(postId);

        request.setAttribute("post", post);
        request.setAttribute("category", category);
        request.setAttribute("center", "boardEdit.jsp");

        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int postId = Integer.parseInt(request.getParameter("postId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        boardDAO.updatePost(postId, title, content);

        response.sendRedirect(
        		
                request.getContextPath() +
                "/board/detail?postId=" + postId + "&category=" + category + "&page=" + page
        );
    }
}