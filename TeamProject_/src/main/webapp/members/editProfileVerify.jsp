<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>비밀번호 확인</title>
<link rel="stylesheet" href="${contextPath}/css/auth.css" />
<style>
.verify-card-note{
	margin-top:10px;
	padding:14px 16px;
	border-radius:14px;
	background:#f8fafc;
	border:1px solid #dbeafe;
	color:#475569;
	line-height:1.6;
}
.verify-lock{
	width:58px;
	height:58px;
	border-radius:18px;
	background:linear-gradient(135deg,#dbeafe,#bfdbfe);
	color:#1d4ed8;
	display:flex;
	align-items:center;
	justify-content:center;
	font-size:28px;
	box-shadow:0 12px 24px rgba(37,99,235,.14);
	margin-bottom:14px;
}
</style>
</head>
<body>
<div class="container">
	<div class="join-card">
		<div class="join-hero">
			<div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; flex-wrap:wrap;">
				<div>
					<span class="step">SECURITY CHECK</span>
					<h1>비밀번호 확인</h1>
					<p>회원정보를 수정하기 전에 현재 비밀번호를 한 번 더 확인합니다.</p>
				</div>
				<div class="hero-right">
					<a href="${contextPath}/member/mypage.me">마이페이지로 돌아가기</a>
				</div>
			</div>
		</div>

		<div class="join-body">
			<div class="verify-lock">🔒</div>
			<c:if test="${not empty requestScope.verifyError}">
				<div class="alert" role="alert">${requestScope.verifyError}</div>
			</c:if>

			<form action="${contextPath}/member/verifyEditProfilePassword.me" method="post">
				<div class="field">
					<label class="label" for="password">현재 비밀번호 <span class="req">*</span></label>
					<input type="password" id="password" name="password" placeholder="현재 비밀번호를 입력해 주세요" required autofocus>
					<div class="help-hint">보안을 위해 회원정보 수정 전에 본인 확인을 진행합니다.</div>
				</div>

				<div class="verify-card-note">
					현재 로그인된 계정의 비밀번호가 맞으면 회원정보 수정 화면으로 이동합니다.
				</div>

				<div class="actions">
					<a href="${contextPath}/member/mypage.me" class="btn btn-outline btn-block">취소</a>
					<button type="submit" class="btn btn-primary btn-block">확인하고 계속</button>
				</div>
			</form>
		</div>
	</div>
</div>
</body>
</html>
