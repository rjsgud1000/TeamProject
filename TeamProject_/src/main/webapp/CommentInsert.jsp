<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Comment.CommentDAO, Comment.CommentDTO" %>

<%
    String postIdStr = request.getParameter("postId");
    String memberId = request.getParameter("memberId");
    String content = request.getParameter("content");
    String parentCommentIdStr = request.getParameter("parentCommentId");

    if(postIdStr == null || memberId == null || content == null) {
        out.println("<script>alert('잘못된 요청입니다.'); history.back();</script>");
        return;
    }

    int postId = Integer.parseInt(postIdStr);
    Integer parentCommentId = (parentCommentIdStr != null && !parentCommentIdStr.isEmpty()) ? Integer.parseInt(parentCommentIdStr) : null;

    CommentDTO comment = new CommentDTO();
    comment.setPostId(postId);
    comment.setMemberId(memberId);
    comment.setContent(content);
    comment.setParentCommentId(parentCommentId);

    CommentDAO dao = new CommentDAO();
    boolean success = dao.insertComment(comment);

    if(success) {
%>
<script>
    alert("댓글이 등록되었습니다.");
    location.href = "BoardView.jsp?boardId=<%= postId %>";
</script>
<%
    } else {
%>
<script>
    alert("댓글 등록 실패");
    history.back();
</script>
<%
    }
%>