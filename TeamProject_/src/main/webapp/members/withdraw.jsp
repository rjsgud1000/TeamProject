<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="member" value="${requestScope.memberDetail}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원탈퇴</title>
<link rel="stylesheet" href="${contextPath}/css/auth.css" />
<link rel="stylesheet" href="${contextPath}/css/member-pages.css" />
</head>
<body>
<div class="container">
	<div class="join-card">
		<div class="join-hero">
			<div class="page-flex-head">
				<div>
					<span class="step">MY PAGE</span>
					<h1>회원탈퇴</h1>
					<p>탈퇴 전 비밀번호를 한 번 더 확인합니다.</p>
				</div>
				<div class="hero-right">
					<a href="${contextPath}/member/mypage.me">마이페이지로 돌아가기</a>
				</div>
			</div>
		</div>

		<div class="join-body">
			<c:if test="${not empty requestScope.withdrawError}">
				<div class="alert" role="alert">${requestScope.withdrawError}</div>
			</c:if>

			<div class="withdraw-warn">
				<strong>${member.nickname}</strong>님, 탈퇴하면 현재 계정으로 다시 로그인할 수 없습니다.<br>
				계속 진행하려면 비밀번호를 입력하고 확인해 주세요.
			</div>

			<form action="${contextPath}/member/withdrawPro.me" method="post" id="withdrawForm">
				<div class="field">
					<label class="label">아이디</label>
					<input type="text" value="${member.memberId}" readonly>
				</div>

				<div class="field">
					<label class="label" for="password">비밀번호 확인 <span class="req">*</span></label>
					<div class="pw-wrap">
						<input type="password" id="password" name="password" placeholder="현재 비밀번호를 입력하세요" required>
						<button class="pw-toggle" type="button" id="pwToggle" aria-label="비밀번호 보기/숨기기" title="비밀번호 보기/숨기기">👁</button>
					</div>
					<div class="notice" id="capsNotice" role="status">CapsLock이 켜져 있어요</div>
				</div>

				<div class="check-row">
					<input type="checkbox" id="confirmWithdraw" name="confirmWithdraw" value="yes">
					<label for="confirmWithdraw" class="field-label-strong field-label-wrap">
						회원탈퇴 시 계정을 다시 사용할 수 없음을 확인했습니다.
					</label>
				</div>

				<div class="actions">
					<a href="${contextPath}/member/mypage.me" class="btn btn-outline btn-block">아니오</a>
					<button type="submit" class="btn btn-primary btn-block withdraw-danger-btn">예, 탈퇴합니다</button>
				</div>
			</form>
		</div>
	</div>
</div>

<script src="${contextPath}/js/auth.js"></script>
<script>
	(function() {
		var form = document.getElementById('withdrawForm');
		if (!form) return;
		form.addEventListener('submit', function(e) {
			var password = document.getElementById('password');
			var confirmBox = document.getElementById('confirmWithdraw');
			if (!password || password.value.trim() === '') {
				alert('비밀번호를 입력해 주세요.');
				password && password.focus();
				e.preventDefault();
				return;
			}
			if (!confirmBox || !confirmBox.checked) {
				alert('회원탈퇴 확인에 체크해 주세요.');
				confirmBox && confirmBox.focus();
				e.preventDefault();
				return;
			}
			if (!confirm('정말 탈퇴하시겠습니까?')) {
				e.preventDefault();
			}
		});
	})();
</script>
</body>
</html>