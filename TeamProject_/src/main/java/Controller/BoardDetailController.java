package Controller;

import Dao.BoardDAO;
import Dto.BoardDTO;
import Service.BoardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/detail")
public class BoardDetailController extends HttpServlet {

    private BoardService boardService;

    @Override
    public void init() throws ServletException {
        boardService = new BoardService(new BoardDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String postIdStr = request.getParameter("postId");

        if (postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        int postId;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        boardService.increaseViewCount(postId);
        BoardDTO post = boardService.getPostById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        request.setAttribute("post", post);
        request.setAttribute("center", "boardDetail.jsp");
        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }
}