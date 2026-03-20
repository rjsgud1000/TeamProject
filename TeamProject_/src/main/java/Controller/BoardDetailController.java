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

     // [추가] 댓글 페이지 번호 처리
        int commentPage = 1;
        String commentPageStr = request.getParameter("commentPage");
        try {
            if (commentPageStr != null && !commentPageStr.isBlank()) {
                commentPage = Integer.parseInt(commentPageStr);
            }
        } catch (NumberFormatException e) {
            commentPage = 1;
        }

        // [추가] 댓글 페이지당 부모댓글 5개
        int commentPageSize = 5;

        // [추가] 부모댓글 총 개수 / 총 페이지 수 계산
        int parentCommentCount = commentDAO.getParentCommentCountByPostId(postId);
        int commentTotalPages = Math.max(1, (int) Math.ceil((double) parentCommentCount / commentPageSize));
        
        if (commentPage < 1) {
            commentPage = 1;
        }
        if (commentPage > commentTotalPages) {
            commentPage = commentTotalPages;
        }
        
        // 시작 row 계산
        int commentStart = (commentPage - 1) * commentPageSize;
        
        List<CommentDTO> comments;
        try {
            // [수정] 전체 조회 -> 페이징 조회로 변경
            comments = commentDAO.getPagedCommentsWithReplies(postId, commentStart, commentPageSize);
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

       

        request.setAttribute("likeCount", likeCount);
        request.setAttribute("liked", liked);
        
        // [추가] 댓글 페이징 정보 전달
        request.setAttribute("commentPage", commentPage);
        request.setAttribute("commentTotalPages", commentTotalPages);
        request.setAttribute("commentPageSize", commentPageSize);
        request.setAttribute("parentCommentCount", parentCommentCount);

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
        String commentPageParam = request.getParameter("commentPage");
    	String commentPageValue = (commentPageParam != null && !commentPageParam.isBlank()) ? commentPageParam : "1";
    	
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
	
	            if (content != null) {
	                content = content.trim(); // [추가] 앞뒤 공백 제거
	            }
	
	            if (content != null && !content.isBlank()) {
	
	                // [추가] 댓글 50자 제한
	                if (content.length() > 50) {
	                    String category1 = (category != null) ? category : "0";
	                    String page1 = (page != null) ? page : "1";
	
	                    String redirectUrl = String.format(
	                        "%s/board/detail?postId=%d&category=%s&page=%s&commentPage=%s&error=commentLength",
	                        request.getContextPath(), postId, category1, page1, commentPageValue
	                    );
	
	                    response.sendRedirect(redirectUrl);
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
	                    boolean isAdmin = "ADMIN".equalsIgnoreCase(loginMember.getRole());
	                    if(isAuthor || isAdmin){
	                        String newContent = request.getParameter("content");

	                        if (newContent != null) {
	                            newContent = newContent.trim(); // [추가]
	                        }

	                        if(newContent != null && !newContent.isBlank()){

	                            // [추가] 수정도 50자 제한
	                            if (newContent.length() > 50) {
	                                String category1 = (category != null) ? category : "0";
	                                String page1 = (page != null) ? page : "1";

	                                String redirectUrl = String.format(
	                                    "%s/board/detail?postId=%d&category=%s&page=%s&commentPage=%s&error=commentLength",
	                                    request.getContextPath(), postId, category1, page1, commentPageValue
	                                );

	                                response.sendRedirect(redirectUrl);
	                                return;
	                            }

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
                    CommentDTO target = commentDAO.getCommentById(commentId);
                    if (target != null) {
                        boolean isAuthor = target.getMemberId().equals(loginMember.getMemberId());
                        if (!isAuthor) {
                            String reason = request.getParameter("reason");
                            if (reason != null && !reason.isBlank()) {
                                commentDAO.reportComment(commentId, loginMember.getMemberId(), reason.trim());
                            }
                        }
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
                "%s/board/detail?postId=%d&category=%s&page=%s&commentPage=%s",
                request.getContextPath(), postId, category1, page1, commentPageValue
        );

        response.sendRedirect(redirectUrl);
    }
}