package Controller;

import Dao.BoardDAO;
import Dto.BoardDTO;

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

        HttpSession session = request.getSession(false);

        // 로그인 안 한 경우 차단
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        boolean isAdmin = "admin".equalsIgnoreCase(memberId);

        int postId = Integer.parseInt(request.getParameter("postId"));
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        // 게시글 정보 조회
        BoardDTO post = boardDAO.selectPostById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        // ★ 공지사항(category=0)은 관리자만 삭제 가능
        if ("0".equals(post.getCategory())) {
            if (!isAdmin) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/board/detail?postId=" + postId
                        + "&category=" + category
                        + "&page=" + page
                );
                return;
            }
        }

        boardDAO.deletePost(postId);

        response.sendRedirect(
                request.getContextPath()
                + "/board/list?category=" + category + "&page=" + page
        );
    }
}