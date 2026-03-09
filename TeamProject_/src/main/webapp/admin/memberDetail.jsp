<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="member" value="${requestScope.memberDetail}" />
<c:set var="flash" value="${sessionScope.adminMemberFlash}" />
<c:if test="${not empty flash}">
	<c:remove var="adminMemberFlash" scope="session" />
	<script>alert("${flash}");</script>
</c:if>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 상세관리</title>
<link rel="stylesheet" href="${contextPath}/css/admin-member.css" />
</head>
<body>
<div class="detail-wrap">
	<div class="detail-head">
		<div>
			<h1>회원 상세관리</h1>
			<p><strong>${member.memberId}</strong> 회원의 상세 정보와 상태를 관리합니다.</p>
		</div>
		<div class="actions">
			<a class="btn btn--ghost" href="${contextPath}/member/admin/list.me">목록으로</a>
			<a class="btn btn--ghost" href="${contextPath}/main.jsp">메인으로</a>
		</div>
	</div>

	<div class="card summary">
		<div>
			<dl class="meta">
				<dt>아이디</dt><dd>${member.memberId}</dd>
				<dt>이름</dt><dd>${member.username}</dd>
				<dt>닉네임</dt><dd>${member.nickname}</dd>
				<dt>권한</dt><dd><span class="badge badge--${member.role}">${empty roleLabelMap[member.role] ? member.role : roleLabelMap[member.role]}</span></dd>
				<dt>상태</dt><dd><span class="badge badge--${member.status}">${empty statusLabelMap[member.status] ? member.status : statusLabelMap[member.status]}</span></dd>
				<dt>이메일</dt><dd><c:out value="${empty member.email ? '-' : member.email}" /></dd>
				<dt>휴대폰</dt><dd><c:out value="${empty member.phone ? '-' : member.phone}" /></dd>
				<dt>가입일</dt><dd>${memberCreatedAtText}</dd>
				<dt>수정일</dt><dd>${memberUpdatedAtText}</dd>
			</dl>
		</div>
		<div>
			<c:if test="${not empty member.sanctionReason or not empty member.sanctionEndAt}">
				<div class="notice">
					<div>최근 제재 사유: <strong><c:out value="${empty member.sanctionReason ? '사유 없음' : member.sanctionReason}" /></strong></div>
					<div>최근 제재 종료일: <strong>${memberSanctionEndAtText}</strong></div>
				</div>
			</c:if>
		</div>
	</div>

	<div class="card">
		<h2 style="margin-top:0;">주소 정보</h2>
		<div class="address">우편번호: <c:out value="${empty member.zipcode ? '-' : member.zipcode}" />
도로명주소: <c:out value="${empty member.addr1 ? '-' : member.addr1}" />
지번주소: <c:out value="${empty member.addr2 ? '-' : member.addr2}" />
상세주소: <c:out value="${empty member.addr3 ? '-' : member.addr3}" />
참고항목: <c:out value="${empty member.addr4 ? '-' : member.addr4}" /></div>
	</div>
</div>
</body>
</html>