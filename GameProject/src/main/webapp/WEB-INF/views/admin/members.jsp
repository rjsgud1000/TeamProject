<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/admin/members.jsp

  [역할]
  - 관리자 전용 회원 목록 화면

  [필요한 request attribute]
  - members(List<Member>): AdminController에서 설정

  [유지보수 포인트]
  - 회원 상세/수정/제재 기능은 memberDetail.jsp와 AdminController에 연결됩니다.
--%>
<%@ page import="java.util.List" %>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<h2>회원목록(관리자)</h2>
<%
  List<Member> members = (List<Member>)request.getAttribute("members");
  String ctx = request.getContextPath();
%>

<table border="1" width="800">
  <tr>
    <th>아이디</th><th>닉네임</th><th>권한</th><th>상태</th><th>제재</th><th>상세</th>
  </tr>
  <% if(members != null){ for(Member m : members){ %>
    <tr>
      <td><%=m.getLoginId()%></td>
      <td><%=m.getNickname()%></td>
      <td><%=m.getRole()%></td>
      <td><%=m.getStatus()%></td>
      <td><%=m.getSanctionUntil()%></td>
      <td><a href="<%=ctx%>/admin/memberDetail.do?loginId=<%=m.getLoginId()%>">보기</a></td>
    </tr>
  <% }} %>
</table>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />