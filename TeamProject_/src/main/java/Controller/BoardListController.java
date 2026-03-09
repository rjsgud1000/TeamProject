package Controller;

import Dao.BoardDAO;
import Vo.BoardPostVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/board/list")
public class BoardListController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String categoryParam = request.getParameter("category");
        List<BoardPostVO> list;
        String boardTitle;

        if ("all".equals(categoryParam) || categoryParam == null || categoryParam.trim().isEmpty()) {
            list = boardDAO.selectAllBoardList();   // 1~3만 조회
            boardTitle = "전체보기";
            request.setAttribute("category", "all");
        } else {
            int category;
            try {
                category = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                category = 1;
            }

            list = boardDAO.selectBoardList(category);

            switch (category) {
                case 0: boardTitle = "공지사항"; break;
                case 1: boardTitle = "자유 게시판"; break;
                case 2: boardTitle = "질문과 답변"; break;
                case 3: boardTitle = "파티원 모집"; break;
                default: boardTitle = "게시판";
            }

            request.setAttribute("category", category);
        }

        request.setAttribute("boardTitle", boardTitle);
        request.setAttribute("boardList", list);
        request.setAttribute("center", "/boardList.jsp");
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}