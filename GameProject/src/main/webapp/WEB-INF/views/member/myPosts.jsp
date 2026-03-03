<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="vo.Post" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  List<Post> posts = (List<Post>)request.getAttribute("posts");
  int pageNum = (Integer)request.getAttribute("page");
  String ctx = request.getContextPath();
%>

<h2>내가 쓴 글</h2>

<table border="1" width="900">
  <tr>
    <th>ID</th><th>레벨</th><th>제목</th><th>조회</th><th>좋아요</th><th>작성일</th>
  </tr>
  <% if(posts != null){ for(Post p : posts){ %>
    <tr>
      <td><%=p.getId()%></td>
      <td><%=p.getLevel()%></td>
      <td><a href="<%=ctx%>/community/detail.do?id=<%=p.getId()%>"><%=p.getTitle()%></a></td>
      <td><%=p.getViews()%></td>
      <td><%=p.getLikes()%></td>
      <td><%=p.getCreatedAt()%></td>
    </tr>
  <% }} %>
</table>

<p>
  <a href="<%=ctx%>/member/myPosts.me?page=<%=(pageNum-1)%>">이전</a> |
  <a href="<%=ctx%>/member/myPosts.me?page=<%=(pageNum+1)%>">다음</a>
</p>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />