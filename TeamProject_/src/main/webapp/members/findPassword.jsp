<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>비밀번호 찾기</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/auth.css" />
<style>
.alert{ white-space: pre-line; }
.success-message{
	margin-bottom: 10px;
	padding: 10px 12px;
	border-radius: 8px;
	background: rgba(60, 179, 113, 0.18);
	color: #d7ffe8;
	white-space: pre-line;
}
.find-password-page{
	max-width: 560px;
	margin: 0 auto;
}
.find-password-card{
	border-radius: 20px;
	overflow: hidden;
}
.find-password-body{
	padding: 24px;
}
.find-password-form{
	display: grid;
	gap: 14px;
}
.find-password-actions{
	display: grid;
	gap: 10px;
	margin-top: 8px;
}
.btn-secondary{
	border: 0;
	border-radius: 10px;
	padding: 12px 14px;
	cursor: pointer;
}
.find-password-help{
	font-size: 12px;
	opacity: 0.85;
	line-height: 1.5;
}
.sub-links{
	margin-top: 12px;
	text-align: center;
	font-size: 13px;
}
</style>
</head>
<body>
<%
	String contextPath = request.getContextPath();
	String passwordFindId = request.getAttribute("passwordFindId") == null ? "" : (String) request.getAttribute("passwordFindId");
	String passwordFindEmail = request.getAttribute("passwordFindEmail") == null ? "" : (String) request.getAttribute("passwordFindEmail");
	String passwordFindError = (String) request.getAttribute("passwordFindError");
	String passwordFindMessage = (String) request.getAttribute("passwordFindMessage");
	boolean passwordCodeSent = Boolean.TRUE.equals(request.getAttribute("passwordCodeSent"));
%>

<div class="find-password-page">
	<div class="login-card find-password-card" role="region" aria-label="비밀번호 찾기">
		<div class="login-hero">
			<h2>비밀번호 찾기</h2>
			<p>가입한 아이디와 이메일을 확인한 뒤 인증번호를 검증하면 임시 비밀번호를 메일로 보내드립니다.</p>
		</div>

		<div class="find-password-body">
			<% if (passwordFindError != null) { %>
				<div class="alert" role="alert"><%= passwordFindError %></div>
			<% } %>
			<% if (passwordFindMessage != null) { %>
				<div class="success-message" role="status"><%= passwordFindMessage %></div>
			<% } %>

			<form class="find-password-form" method="post" action="<%=contextPath%>/member/findPassword.me">
				<div class="login-form-row">
					<label class="label" for="findId">아이디</label>
					<input type="text" id="findId" name="findId" placeholder="가입한 아이디" value="<%= passwordFindId %>" required>
				</div>
				<div class="login-form-row">
					<label class="label" for="findEmail">이메일</label>
					<input type="email" id="findEmail" name="findEmail" placeholder="가입한 이메일" value="<%= passwordFindEmail %>" required>
				</div>
				<div class="find-password-actions">
					<button class="btn-secondary" type="submit">인증번호 메일 보내기</button>
					<div class="find-password-help">가입한 이메일로 인증번호를 보냅니다. 인증이 완료되면 임시 비밀번호를 메일로 다시 발송합니다.</div>
				</div>
			</form>

			<% if (passwordCodeSent) { %>
			<form class="find-password-form" method="post" action="<%=contextPath%>/member/verifyPasswordCode.me" style="margin-top:18px;">
				<input type="hidden" name="findId" value="<%= passwordFindId %>">
				<input type="hidden" name="findEmail" value="<%= passwordFindEmail %>">
				<div class="login-form-row">
					<label class="label" for="verificationCode">인증번호</label>
					<input type="text" id="verificationCode" name="verificationCode" placeholder="메일로 받은 인증번호" required>
				</div>
				<div class="find-password-actions">
					<button class="btn-secondary" type="submit">인증 확인 후 임시 비밀번호 발급</button>
				</div>
			</form>
			<% } %>

			<div class="sub-links">
				<a href="<%=contextPath%>/member/login.me">로그인으로 돌아가기</a>
				&nbsp;|&nbsp;
				<a href="<%=contextPath%>/member/join.me">회원가입</a>
			</div>
		</div>
	</div>
</div>

<script src="<%=request.getContextPath()%>/js/auth.js"></script>
</body>
</html>
