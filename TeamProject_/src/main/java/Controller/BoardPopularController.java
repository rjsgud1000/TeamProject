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
    public void init() {
        boardDAO = new BoardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String period = request.getParameter("period");
        if (period == null || period.trim().isEmpty()) {
            period = "week"; // 기본값
        }

        int days = 7;
        if ("day".equals(period)) {
            days = 1;
        } else if ("month".equals(period)) {
            days = 30;
        } else {
            period = "week";
            days = 7;
        }

        List<BoardPostVO> likeTopList = boardDAO.selectHotPostsByLike(days, 5);
        List<BoardPostVO> viewTopList = boardDAO.selectHotPostsByView(days, 5);
        List<BoardPostVO> commentTopList = boardDAO.selectHotPostsByComment(days, 5);

        request.setAttribute("period", period);
        request.setAttribute("likeTopList", likeTopList);
        request.setAttribute("viewTopList", viewTopList);
        request.setAttribute("commentTopList", commentTopList);

        request.setAttribute("center", "popularBoard.jsp");
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}