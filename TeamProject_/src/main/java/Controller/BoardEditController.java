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

        // ★ 로그인 체크
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        boolean isAdmin = "admin".equalsIgnoreCase(memberId); // ★ 관리자 아이디 기준

        int postId = Integer.parseInt(request.getParameter("postId"));
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        BoardDTO post = boardDAO.selectPostById(postId);

        // ★ 게시글이 없으면 목록으로
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        // ★ 공지사항(category=0)은 관리자만 수정 가능
        if ("0".equals(post.getCategory()) && !isAdmin) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/board/detail?postId=" + postId
                    + "&category=" + category
                    + "&page=" + page
            );
            return;
        }

        request.setAttribute("post", post);
        request.setAttribute("category", category);
        request.setAttribute("page", page);

        request.setAttribute("center", "boardEdit.jsp");
        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // ★ 로그인 체크
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        boolean isAdmin = "admin".equalsIgnoreCase(memberId); // ★ 관리자 아이디 기준

        int postId = Integer.parseInt(request.getParameter("postId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String category = request.getParameter("category");
        String page = request.getParameter("page");

        if (category == null) category = "1";
        if (page == null) page = "1";

        // ★ 게시글 다시 조회해서 공지사항 여부 확인
        BoardDTO post = boardDAO.selectPostById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        // ★ 공지사항(category=0)은 관리자만 수정 가능
        if ("0".equals(post.getCategory()) && !isAdmin) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/board/detail?postId=" + postId
                    + "&category=" + category
                    + "&page=" + page
            );
            return;
        }

        boardDAO.updatePost(postId, title, content);

        response.sendRedirect(
                request.getContextPath()
                + "/board/detail?postId=" + postId
                + "&category=" + category
                + "&page=" + page
        );
    }
}