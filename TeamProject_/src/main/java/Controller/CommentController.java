package Controller;

import Dao.BoardDAO;
import Dto.BoardDTO;
import Service.BoardService;
import Comment.CommentDAO;
import Comment.CommentDTO;
import Vo.MemberVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/board/detail")
public class BoardDetailController extends HttpServlet {

    private BoardService boardService;
    private CommentDAO commentDAO;

    @Override
    public void init() throws ServletException {
        boardService = new BoardService(new BoardDAO());
        commentDAO = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String postIdStr = request.getParameter("postId");
        int postId = 0;

        // 1️⃣ 파라미터 검증
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        // 2️⃣ 게시글 조회
        BoardDTO post = boardService.getPostById(postId);
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        // 3️⃣ 조회수 증가
        boardService.increaseViewCount(postId);

        // 4️⃣ 게시글별 댓글 조회
        List<CommentDTO> comments;
        try {
            comments = commentDAO.getCommentsByPostId(postId);
            if (comments == null) comments = Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            comments = Collections.emptyList();
        }

        // 5️⃣ 로그인 멤버 정보 가져오기 (세션)
        HttpSession session = request.getSession(false);
        MemberVO loginMember = null;
        if (session != null) {
            Object obj = session.getAttribute("loginMember");
            if (obj instanceof MemberVO) {
                loginMember = (MemberVO) obj;
            }
        }

        // 6️⃣ JSP에 데이터 전달
        request.setAttribute("post", post);
        request.setAttribute("comments", comments);
        request.setAttribute("loginMember", loginMember);
        request.setAttribute("center", "boardDetail.jsp");

        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    // POST 요청 (댓글 작성) 처리
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        Object obj = session.getAttribute("loginMember");
        if (!(obj instanceof MemberVO)) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }
        MemberVO loginMember = (MemberVO) obj;

        String postIdStr = request.getParameter("postId");
        String content = request.getParameter("content");
        int postId = 0;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        if (content != null && !content.isBlank()) {
            CommentDTO comment = new CommentDTO();
            comment.setPostId(postId);
            comment.setMemberId(loginMember.getMemberId());
            comment.setContent(content);

            commentDAO.insertComment(comment);
        }

        response.sendRedirect(request.getContextPath() + "/board/detail?postId=" + postId);
    }
}