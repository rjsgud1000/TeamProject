<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Comment.CommentDAO" %>
<%
    int commentId = Integer.parseInt(request.getParameter("commentId"));
    String memberId = request.getParameter("memberId");

    CommentDAO dao = new CommentDAO();
    dao.dislikeComment(commentId, memberId);

    response.sendRedirect("javascript:history.back()");
%>