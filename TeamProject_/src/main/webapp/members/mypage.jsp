<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="member" value="${requestScope.memberDetail}" />
<c:set var="profileFlash" value="${sessionScope.profileFlash}" />
<c:if test="${not empty profileFlash}">
	<c:remove var="profileFlash" scope="session" />
	<script type="text/javascript">
		alert("${profileFlash}");
	</script>
</c:if>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>마이페이지</title>

<link rel="stylesheet" href="${contextPath}/css/mypage.css" />

</head>
<body>

<div class="page">
	<div class="container">
		<div class="card">
			<div class="hero">
				<h1>마이페이지</h1>
				<p>내 정보와 계정 상태를 확인할 수 있어요.</p>
			</div>

			<div class="body">
				<c:choose>
					<c:when test="${empty member}">
						<div class="empty">
							<h2>로그인이 필요해요</h2>
							<p>마이페이지는 로그인 후 이용할 수 있습니다.</p>
							<div class="actions">
								<a class="btn primary" href="${contextPath}/member/login.me">로그인</a>
								<a class="btn" href="${contextPath}/main.jsp">메인으로</a>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<c:set var="displayName" value="${not empty member.nickname ? member.nickname : member.memberId}" />
						<c:set var="avatarText" value="${not empty displayName ? displayName.substring(0,1) : '?'}" />
						<c:set var="genderLabel" value="${member.gender eq 'man' ? '남성' : member.gender eq 'woman' ? '여성' : '(미등록)'}" />
						<c:set var="fullAddress" value="${empty member.zipcode and empty member.addr1 and empty member.addr2 and empty member.addr3 and empty member.addr4 ? '(미등록)' : '['}${member.zipcode}${empty member.zipcode ? '' : '] '}${member.addr1} ${member.addr2} ${member.addr3} ${member.addr4}" />

						<div class="grid">
							<div class="profile">
								<div class="section-title">프로필</div>
								<div style="display:flex; gap:12px; align-items:center;">
									<div class="avatar">${avatarText}</div>
									<div class="kv">
										<div class="name">${displayName}</div>
										<div class="sub">@${member.memberId}</div>
									</div>
								</div>

								<div class="actions">
									<a class="btn" href="${contextPath}/member/logout.me">로그아웃</a>
									<a class="btn primary" href="${contextPath}/member/editProfile.me">회원정보 수정</a>
									<a class="btn" href="${contextPath}/member/withdraw.me" style="background:#fff5f5; color:#b91c1c; border:1px solid #fecaca;">회원탈퇴</a>
								</div>

								<div class="note">
									비밀번호 변경은 회원정보 수정 화면에서 함께 처리할 수 있습니다.
								</div>
							</div>

							<div>
								<div class="section-title">내 정보</div>
								<div class="list" aria-label="내 정보">
									<div class="row">
										<div class="key">아이디</div>
										<div class="val">${member.memberId}</div>
									</div>
									<div class="row">
										<div class="key">닉네임</div>
										<div class="val">${displayName}</div>
									</div>
									<div class="row">
										<div class="key">이름</div>
										<div class="val">${member.username}</div>
									</div>
									<div class="row">
										<div class="key">성별</div>
										<div class="val">${genderLabel}</div>
									</div>
									<div class="row">
										<div class="key">이메일</div>
										<div class="val">${empty member.email ? '(미등록)' : member.email}</div>
									</div>
									<div class="row">
										<div class="key">휴대폰</div>
										<div class="val">${empty member.phone ? '(미등록)' : member.phone}</div>
									</div>
									<div class="row">
										<div class="key">집주소</div>
										<div class="val">${fullAddress}</div>
									</div>
								</div>
								<div class="note">
									<span class="small">TIP</span><br>
									이메일, 휴대폰, 주소는 DB에서 다시 조회한 최신 정보입니다.
								</div>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>

</body>
</html>