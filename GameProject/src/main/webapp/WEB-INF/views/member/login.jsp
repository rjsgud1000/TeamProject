<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/member/login.jsp

  [역할]
  - 로그인 폼 화면

  [입력/출력]
  - form action: /member/login.me (POST)
  - 파라미터: loginId, password

  [사용하는 request attribute]
  - error: 로그인 실패/차단 시 컨트롤러가 설정하는 메시지
--%>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<h2>로그인</h2>

<% String error = (String)request.getAttribute("error"); %>
<% if(error != null){ %>
  <p style="color:red;"><%=error%></p>
<% } %>

<form method="post" action="<%=request.getContextPath()%>/member/login.me">
  <div>
    <label>아이디</label>
    <input type="text" name="loginId" required>
  </div>
  <div>
    <label>비밀번호</label>
    <input type="password" name="password" required>
  </div>
  <button type="submit">로그인</button>
</form>

<p>
  <a href="<%=request.getContextPath()%>/member/joinForm.me">회원가입</a>
</p>
<jsp:include page="/WEB-INF/views/layout/footer.jsp" />