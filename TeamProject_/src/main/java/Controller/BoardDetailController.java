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
    private final BoardDAO boardDAO = new BoardDAO();

    // ============================
    // [추가] 댓글 최대 개수 상수
    // ============================
    private static final int MAX_COMMENT_COUNT = 50;

    @Override
    public void init() throws ServletException {
        boardService = new BoardService(new BoardDAO());
        commentDAO = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String postIdStr = request.getParameter("postId");
        String category = request.getParameter("category");
        String page = request.getParameter("page");
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

            for (CommentDTO c : comments) {
                c.setLikeCount(commentDAO.getCommentLikeCount(c.getCommentId()));
                c.setDislikeCount(commentDAO.getCommentDislikeCount(c.getCommentId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            comments = Collections.emptyList();
        }

        HttpSession session = request.getSession(false);
        MemberVO loginMember = (session != null) ? (MemberVO) session.getAttribute("loginMember") : null;

        int likeCount = boardDAO.getLikeCount(postId);
        boolean liked = false;

        if (loginMember != null) {
            liked = boardDAO.isLikedByMember(postId, loginMember.getMemberId());
        }

        // ============================
        // [추가] 현재 댓글 수 조회
        // - JSP에서 입력창 숨김/안내문 출력용
        // ============================
        int commentCount = commentDAO.getCommentCountByPostId(postId);

        request.setAttribute("likeCount", likeCount);
        request.setAttribute("liked", liked);

        // ============================
        // [추가] 댓글 수 / 최대 댓글 수 전달
        // ============================
        request.setAttribute("commentCount", commentCount);
        request.setAttribute("maxCommentCount", MAX_COMMENT_COUNT);

        request.setAttribute("post", post);
        request.setAttribute("comments", comments);
        request.setAttribute("category", category);
        request.setAttribute("page", page);
        request.setAttribute("loginMember", loginMember);
        request.setAttribute("center", "boardDetail.jsp");

        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

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

        String action = request.getParameter("action");
        String category = request.getParameter("category");
        String page = request.getParameter("page");
        String postIdStr = request.getParameter("postId");
        String commentIdStr = request.getParameter("commentId");
        int postId = 0;
        int commentId = 0;

        try {
            postId = Integer.parseInt(postIdStr);
            if (commentIdStr != null && !commentIdStr.isEmpty()) {
                commentId = Integer.parseInt(commentIdStr);
            }
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        switch (action) {
            case "insert": // 댓글/대댓글 작성
                String content = request.getParameter("content");
                if (content != null && !content.isBlank()) {

                    // ============================
                    // [추가] 댓글 50개 제한 체크
                    // - 일반 댓글 + 답글 포함
                    // - 50개 이상이면 작성 차단
                    // ============================
                    int commentCount = commentDAO.getCommentCountByPostId(postId);
                    if (commentCount >= MAX_COMMENT_COUNT) {
                        String category1 = (category != null) ? category : "0";
                        String page1 = (page != null) ? page : "1";

                        String limitRedirectUrl = String.format(
                                "%s/board/detail?postId=%d&category=%s&page=%s&commentLimit=1",
                                request.getContextPath(), postId, category1, page1
                        );

                        response.sendRedirect(limitRedirectUrl);
                        return;
                    }

                    CommentDTO comment = new CommentDTO();
                    comment.setPostId(postId);
                    comment.setMemberId(loginMember.getMemberId());
                    comment.setContent(content);

                    String parentIdStr = request.getParameter("parentCommentId");
                    if (parentIdStr != null && !parentIdStr.isEmpty()) {
                        comment.setParentCommentId(Integer.parseInt(parentIdStr));
                    }

                    commentDAO.insertComment(comment);
                }
                break;

            case "update": // 댓글 수정
                if(commentId > 0){
                    CommentDTO target = commentDAO.getCommentById(commentId);
                    if(target != null){
                        boolean isAuthor = target.getMemberId().equals(loginMember.getMemberId());
                        boolean isAdmin = "ADMIN".equalsIgnoreCase(loginMember.getRole()); // role 체크
                        if(isAuthor || isAdmin){
                            String newContent = request.getParameter("content");
                            if(newContent != null && !newContent.isBlank()){
                                commentDAO.updateComment(commentId, newContent);
                            }
                        }
                    }
                }
                break;

            case "delete":
                if(commentId > 0){
                    CommentDTO target = commentDAO.getCommentById(commentId);
                    if(target != null){
                        boolean isAuthor = target.getMemberId().equals(loginMember.getMemberId());
                        boolean isAdmin = "ADMIN".equalsIgnoreCase(loginMember.getRole());
                        if(isAuthor || isAdmin){
                            commentDAO.deleteCommentRecursive(commentId); // <- 변경
                        }
                    }
                }
                break;

            case "like":
                if (commentId > 0) {
                    commentDAO.likeComment(commentId, loginMember.getMemberId());
                }
                break;

            case "dislike":
                if (commentId > 0) {
                    commentDAO.dislikeComment(commentId, loginMember.getMemberId());
                }
                break;

            case "report":
                if (commentId > 0) {
                    String reason = request.getParameter("reason");
                    if (reason != null && !reason.isBlank()) {
                        commentDAO.reportComment(commentId, loginMember.getMemberId(), reason);
                    }
                }
                break;

            default:
                break;
        }

        // category/page 안전 처리
        String category1 = request.getParameter("category");
        String page1 = request.getParameter("page");
        category1 = (category1 != null) ? category1 : "0";
        page1 = (page1 != null) ? page1 : "1";

        String redirectUrl = String.format(
                "%s/board/detail?postId=%d&category=%s&page=%s",
                request.getContextPath(), postId, category1, page1
        );

        response.sendRedirect(redirectUrl);
    }
}