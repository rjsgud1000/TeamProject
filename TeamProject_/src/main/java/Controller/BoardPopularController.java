package Controller;

import Dao.BoardDAO;
import Vo.BoardPostVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/board/popular")
public class BoardPopularController extends HttpServlet {

    private BoardDAO boardDAO;

    @Override
    public void init() throws ServletException {
        boardDAO = new BoardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<BoardPostVO> likeTopList = boardDAO.selectHotPostsByLike();
        List<BoardPostVO> viewTopList = boardDAO.selectHotPostsByView();
        List<BoardPostVO> commentTopList = boardDAO.selectHotPostsByComment(5);

        request.setAttribute("likeTopList", likeTopList);
        request.setAttribute("viewTopList", viewTopList);
        request.setAttribute("commentTopList", commentTopList);

        request.setAttribute("center", "popularBoard.jsp");
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}