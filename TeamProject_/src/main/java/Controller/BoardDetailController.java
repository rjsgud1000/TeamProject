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
        String category = request.getParameter("category");   
        String page = request.getParameter("page");           
        int postId;

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

            // ⭐ 좋아요/싫어요 수 세팅
            for (CommentDTO c : comments) {
                c.setLikeCount(commentDAO.getCommentLikeCount(c.getCommentId()));
                c.setDislikeCount(commentDAO.getCommentDislikeCount(c.getCommentId()));
            }

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
        request.setAttribute("category", category);   
        request.setAttribute("page", page);          
        request.setAttribute("loginMember", loginMember);
        request.setAttribute("center", "boardDetail.jsp");

        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    // POST 요청 처리 (댓글/답글/좋아요/싫어요/삭제/신고)
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
            if(commentIdStr != null && !commentIdStr.isEmpty()) commentId = Integer.parseInt(commentIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/board/list");
            return;
        }

        switch (action) {
            case "insert": // 댓글 / 대댓글 작성
                String content = request.getParameter("content");
                if (content != null && !content.isBlank()) {
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
                    String newContent = request.getParameter("content");
                    if(newContent != null && !newContent.isBlank()){
                        commentDAO.updateComment(commentId, newContent);
                    }
                }
                break;

            case "delete": // 댓글 삭제
                if(commentId > 0){
                    commentDAO.deleteComment(commentId);
                }
                break;

            case "like": // 댓글 좋아요
                if(commentId > 0){
                    commentDAO.likeComment(commentId, loginMember.getMemberId());
                }
                break;

            case "dislike": // 댓글 싫어요
                if(commentId > 0){
                    commentDAO.dislikeComment(commentId, loginMember.getMemberId());
                }
                break;

            case "report": // 댓글 신고
                if(commentId > 0){
                    String reason = request.getParameter("reason");
                    if(reason != null && !reason.isBlank()){
                        commentDAO.reportComment(commentId, loginMember.getMemberId(), reason);
                    }
                }
                break;
                

            default:
                break;
        }

        response.sendRedirect(
        	    request.getContextPath() +
        	    "/board/detail?postId=" + postId +
        	    "&category=" + category +
        	    "&page=" + page
        	);
        

    }
}