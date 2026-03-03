<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="vo.Comment" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  List<Comment> comments = (List<Comment>)request.getAttribute("comments");
  int pageNum = (Integer)request.getAttribute("page");
  String ctx = request.getContextPath();
%>

<h2>내가 쓴 댓글</h2>

<table border="1" width="900">
  <tr>
    <th>ID</th><th>게시글</th><th>내용</th><th>작성일</th>
  </tr>
  <% if(comments != null){ for(Comment c : comments){ %>
    <tr>
      <td><%=c.getId()%></td>
      <td><a href="<%=ctx%>/community/detail.do?id=<%=c.getPostId()%>">게시글 보기</a></td>
      <td><pre style="white-space:pre-wrap;"><%=c.getContent()%></pre></td>
      <td><%=c.getCreatedAt()%></td>
    </tr>
  <% }} %>
</table>

<p>
  <a href="<%=ctx%>/member/myComments.me?page=<%=(pageNum-1)%>">이전</a> |
  <a href="<%=ctx%>/member/myComments.me?page=<%=(pageNum+1)%>">다음</a>
</p>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />