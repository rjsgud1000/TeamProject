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
        int postId;

        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        BoardDTO post = boardService.getPostById(postId);
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        boardService.increaseViewCount(postId);

        List<CommentDTO> comments;
        try {
            comments = commentDAO.getCommentsByPostId(postId);
            if (comments == null) comments = Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            comments = Collections.emptyList();
        }

        request.setAttribute("post", post);
        request.setAttribute("comments", comments);
        request.setAttribute("center", "boardDetail.jsp");

        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        MemberVO loginMember = (session != null) ? (MemberVO) session.getAttribute("loginMember") : null;

        if (loginMember == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        int postId = 0;
        String postIdStr = request.getParameter("postId");

        if (postIdStr != null) {
            postId = Integer.parseInt(postIdStr);
        }

        switch (action) {

            case "insert":

                String content = request.getParameter("content");

                Integer parentId = request.getParameter("parentCommentId") != null
                        ? Integer.valueOf(request.getParameter("parentCommentId"))
                        : null;

                CommentDTO comment = new CommentDTO();
                comment.setPostId(postId);
                comment.setMemberId(loginMember.getMemberId());
                comment.setContent(content);
                comment.setParentCommentId(parentId);

                commentDAO.insertComment(comment);

                break;

            case "update":

                int updateId = Integer.parseInt(request.getParameter("commentId"));
                String newContent = request.getParameter("content");

                commentDAO.updateComment(updateId, newContent);

                postId = commentDAO.getPostIdByCommentId(updateId);

                break;

            case "delete":

                int deleteId = Integer.parseInt(request.getParameter("commentId"));

                commentDAO.deleteComment(deleteId);

                postId = commentDAO.getPostIdByCommentId(deleteId);

                break;

            case "like":

                int likeId = Integer.parseInt(request.getParameter("commentId"));

                commentDAO.likeComment(likeId, loginMember.getMemberId());

                postId = commentDAO.getPostIdByCommentId(likeId);

                break;

            case "dislike":

                int dislikeId = Integer.parseInt(request.getParameter("commentId"));

                commentDAO.dislikeComment(dislikeId, loginMember.getMemberId());

                postId = commentDAO.getPostIdByCommentId(dislikeId);

                break;

            case "report":

                int reportId = Integer.parseInt(request.getParameter("commentId"));

                String reason = request.getParameter("reason");

                commentDAO.reportComment(reportId, loginMember.getMemberId(), reason);

                postId = commentDAO.getPostIdByCommentId(reportId);

                break;
        }

        response.sendRedirect(request.getContextPath() + "/board/detail?postId=" + postId);
    }
}