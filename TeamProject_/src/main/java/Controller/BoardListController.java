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
        String pageParam = request.getParameter("page");

        int currentPage = 1;
        int pageSize = 10;

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int startRow = (currentPage - 1) * pageSize;

        List<BoardPostVO> list;
        String boardTitle;
        int totalCount;

        if ("all".equals(categoryParam) || categoryParam == null || categoryParam.trim().isEmpty()) {
            list = boardDAO.selectAllBoardListPaging(startRow, pageSize);
            totalCount = boardDAO.getAllBoardCount();
            boardTitle = "전체보기";
            request.setAttribute("category", "all");
        } else {
            int category;
            try {
                category = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                category = 1;
            }

            list = boardDAO.selectBoardListPaging(category, startRow, pageSize);
            totalCount = boardDAO.getBoardCount(category);

            switch (category) {
                case 0: boardTitle = "공지사항"; break;
                case 1: boardTitle = "자유 게시판"; break;
                case 2: boardTitle = "질문과 답변"; break;
                case 3: boardTitle = "파티원 모집"; break;
                default: boardTitle = "게시판";
            }

            request.setAttribute("category", category);
        }

        int totalPage = (int) Math.ceil((double) totalCount / pageSize);

        int pageBlock = 5;
        int startPage = ((currentPage - 1) / pageBlock) * pageBlock + 1;
        int endPage = startPage + pageBlock - 1;

        if (endPage > totalPage) {
            endPage = totalPage;
        }

        request.setAttribute("boardTitle", boardTitle);
        request.setAttribute("boardList", list);

        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);

        request.setAttribute("center", "boardList.jsp");
        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }
}