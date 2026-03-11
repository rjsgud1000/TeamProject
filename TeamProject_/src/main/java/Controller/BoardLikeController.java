package Controller;

import Dao.BoardDAO;
import Vo.MemberVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/like")
public class BoardLikeController extends HttpServlet {

    private BoardDAO boardDAO;

    @Override
    public void init() throws ServletException {
        boardDAO = new BoardDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        MemberVO loginMember = (session != null) ? (MemberVO) session.getAttribute("loginMember") : null;

        if (loginMember == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        if (category == null) category = "1";
        if (page == null) page = "1";

        boardDAO.toggleLike(postId, loginMember.getMemberId());

        response.sendRedirect(
                request.getContextPath()
                + "/board/detail?postId=" + postId
                + "&category=" + category
                + "&page=" + page
        );
    }
}