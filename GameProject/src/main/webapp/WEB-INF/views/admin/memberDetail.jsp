<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/admin/memberDetail.jsp

  [역할]
  - 관리자 전용 회원 상세 + 수정/제재/삭제

  [필요한 request attribute]
  - member(Member): AdminController에서 설정

  [폼 action]
  - 수정: POST /admin/memberUpdate.do (loginId, nickname, role)
  - 제재: POST /admin/memberSanction.do (loginId, days)
  - 삭제: POST /admin/memberDelete.do (loginId)

  [유지보수 포인트]
  - 관리자 권한 체크는 AdminController.ensureAdmin 이 최종 방어선입니다.
--%>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  Member m = (Member)request.getAttribute("member");
  String ctx = request.getContextPath();
%>

<h2>회원상세</h2>
<p>아이디: <b><%=m.getLoginId()%></b></p>
<p>닉네임: <b><%=m.getNickname()%></b></p>
<p>권한: <b><%=m.getRole()%></b></p>
<p>상태: <b><%=m.getStatus()%></b></p>
<p>제재기한: <b><%=m.getSanctionUntil()%></b></p>

<h3>수정</h3>
<form method="post" action="<%=ctx%>/admin/memberUpdate.do">
  <input type="hidden" name="loginId" value="<%=m.getLoginId()%>">
  <div>
    <label>닉네임</label>
    <input type="text" name="nickname" value="<%=m.getNickname()%>">
  </div>
  <div>
    <label>권한</label>
    <select name="role">
      <option value="USER" <%= "USER".equals(m.getRole()) ? "selected" : "" %>>USER</option>
      <option value="ADMIN" <%= "ADMIN".equals(m.getRole()) ? "selected" : "" %>>ADMIN</option>
    </select>
  </div>
  <button type="submit">저장</button>
</form>

<h3>제재</h3>
<form method="post" action="<%=ctx%>/admin/memberSanction.do">
  <input type="hidden" name="loginId" value="<%=m.getLoginId()%>">
  <label>일수</label>
  <input type="number" name="days" value="7" min="1">
  <button type="submit">제재 적용</button>
</form>

<h3>삭제</h3>
<form method="post" action="<%=ctx%>/admin/memberDelete.do" onsubmit="return confirm('정말 삭제할까요?');">
  <input type="hidden" name="loginId" value="<%=m.getLoginId()%>">
  <button type="submit">회원삭제</button>
</form>

<p>
  <a href="<%=ctx%>/admin/members.do">목록</a>
</p>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />