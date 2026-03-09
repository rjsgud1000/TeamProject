<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- ==========================================================
📌 login.jsp - 로그인 입력 폼
========================================================== --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>로그인</title>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/auth.css" />

<style>
.alert{
	white-space: pre-line;
}
</style>

</head>
<body>

<%
	String contextPath = request.getContextPath();

	// 회원가입 성공 메시지(1회성)
	String joinFlash = null;
	javax.servlet.http.HttpSession flashSession = request.getSession(false);
	if (flashSession != null) {
		joinFlash = (String) flashSession.getAttribute("joinFlash");
		if (joinFlash != null) {
			flashSession.removeAttribute("joinFlash");
		}
	}
%>
<% if (joinFlash != null) { %>
	<script>
		alert('<%= joinFlash.replace("\\", "\\\\").replace("'", "\\'") %>');
	</script>
<% } %>

<div class="login-page">
	<div class="login-card" role="region" aria-label="로그인">

		<div class="login-hero">
			<h2>로그인</h2>
			<p>G-UNIVERSE에 오신 걸 환영합니다. 계정으로 로그인해 주세요.</p>
		</div>

		<div class="login-body">
			<%-- 로그인 실패 메시지 출력 --%>
			<%
			String loginError = (String) request.getAttribute("loginError");
			if (loginError != null) {
			%>
				<div class="alert" role="alert"><%= loginError %></div>
			<% } %>

			<%-- MemberController 서블릿에 로그인 처리 요청: id/pass 전달 --%>
			<form class="form-signin" method="post"
				action="<%=contextPath%>/member/loginPro.me" id="join">

				<div class="login-form-row">
					<label class="label" for="id">아이디</label>
					<input type="text" id="id" name="id" placeholder="아이디" required autofocus>
				</div>

				<div class="login-form-row">
					<label class="label" for="pass">비밀번호</label>
					<div class="pw-wrap">
						<input type="password" id="pass" name="pass" placeholder="비밀번호" required>
						<button class="pw-toggle" type="button" id="pwToggle" aria-label="비밀번호 보기/숨기기" title="비밀번호 보기/숨기기">👁</button>
					</div>
					<div class="notice" id="capsNotice" role="status">CapsLock이 켜져 있어요</div>
				</div>

				<button class="btn-login" type="submit">로그인</button>

				<div class="muted">
					아직 계정이 없나요? <a href="<%=contextPath%>/member/join.me">회원가입</a>
					&nbsp;|&nbsp;
					<a href="<%=contextPath%>/main.jsp">메인으로</a>
				</div>
			</form>
		</div>
	</div>
</div>

<script src="<%=request.getContextPath()%>/js/auth.js"></script>

</body>
</html>