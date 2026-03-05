package Controller;

import Dao.QnaDAO;
import Vo.QnaPostVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/qna/list")
public class QnaListController extends HttpServlet {

    private final QnaDAO qnaDAO = new QnaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        String p = request.getParameter("page");
        if (p != null) {
            try { page = Integer.parseInt(p); } catch (NumberFormatException ignore) {}
            if (page < 1) page = 1;
        }

        int limit = 10;
        int offset = (page - 1) * limit;

        // accepted_comment_id 컬럼이 있으면 true로
        boolean hasAcceptedColumn = true;

        List<QnaPostVO> qnaList = qnaDAO.selectQnaList(offset, limit, hasAcceptedColumn);

        request.setAttribute("qnaList", qnaList);
        request.setAttribute("page", page);

        // 너희 레이아웃 방식(main.jsp + contentPage include)
        request.setAttribute("contentPage", "/center/qnaList.jsp");
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}