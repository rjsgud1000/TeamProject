<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Service.BoardService, Vo.BoardVO, Comment.CommentDAO, Comment.CommentDTO, java.util.List" %>

<%
    String boardIdStr = request.getParameter("boardId");
    if(boardIdStr == null) {
        out.println("<script>alert('게시글이 없습니다.'); history.back();</script>");
        return;
    }
    int boardId = Integer.parseInt(boardIdStr);

    // 게시글 조회
    BoardService boardService = new BoardService();
    BoardVO board = boardService.getBoard(boardId);

    // 댓글 조회
    CommentDAO commentDAO = new CommentDAO();
    List<CommentDTO> comments = commentDAO.getCommentsByPostId(boardId);

    // 로그인 정보
    String loginId = (String) session.getAttribute("loginId");
%>

<h2><%= board.getTitle() %></h2>
<p>작성자: <%= board.getWriterId() %> | 조회수: <%= board.getViewCount() %></p>
<hr>
<p><%= board.getContent() %></p>

<hr>
<h3>댓글</h3>

<% for(CommentDTO c : comments) { %>
    <div style="margin-left: <%= (c.getParentCommentId() != null ? 30 : 0) %>px; border-bottom:1px solid #eee; padding:5px;">
        <b><%= c.getMemberNickname() %></b> 
        <span style="font-size:0.8em; color:gray;"><%= c.getCreatedAt() %></span><br>
        <%= c.getContent() %>
        <br>
        <form action="CommentInsert.jsp" method="post" style="display:inline;">
            <input type="hidden" name="postId" value="<%= boardId %>">
            <input type="hidden" name="parentCommentId" value="<%= c.getCommentId() %>">
            <input type="hidden" name="memberId" value="<%= loginId %>">
            <input type="text" name="content" placeholder="답글 달기..." required>
            <button type="submit">답글</button>
        </form>

        <!-- 좋아요/싫어요 -->
        <form action="CommentLike.jsp" method="post" style="display:inline;">
            <input type="hidden" name="commentId" value="<%= c.getCommentId() %>">
            <input type="hidden" name="memberId" value="<%= loginId %>">
            <button type="submit">👍 <%= commentDAO.getCommentLikeCount(c.getCommentId()) %></button>
        </form>
        <form action="CommentDislike.jsp" method="post" style="display:inline;">
            <input type="hidden" name="commentId" value="<%= c.getCommentId() %>">
            <input type="hidden" name="memberId" value="<%= loginId %>">
            <button type="submit">👎 <%= commentDAO.getCommentDislikeCount(c.getCommentId()) %></button>
        </form>
    </div>
<% } %>

<hr>
<h4>새 댓글 작성</h4>
<% if(loginId != null) { %>
    <form action="CommentInsert.jsp" method="post">
        <input type="hidden" name="postId" value="<%= boardId %>">
        <input type="hidden" name="memberId" value="<%= loginId %>">
        <textarea name="content" rows="3" cols="50" required placeholder="댓글 작성..."></textarea><br>
        <button type="submit">댓글 작성</button>
    </form>
<% } else { %>
    <p>로그인 후 댓글 작성이 가능합니다.</p>
<% } %>