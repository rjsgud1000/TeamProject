<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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