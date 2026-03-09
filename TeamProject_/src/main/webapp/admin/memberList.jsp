<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="flash" value="${sessionScope.adminMemberFlash}" />
<c:set var="memberCount" value="${empty memberList ? 0 : memberList.size()}" />
<c:if test="${not empty flash}">
	<c:remove var="adminMemberFlash" scope="session" />
	<script>alert("${flash}");</script>
</c:if>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원관리</title>
<link rel="stylesheet" href="${contextPath}/css/admin-member.css" />
</head>
<body>
<div class="member-admin">
	<div class="member-admin__hero">
		<div>
			<h1>회원관리</h1>
			<p>회원 목록 조회, 상태 확인, 상세 페이지 이동을 한 곳에서 처리합니다.</p>
		</div>
		<a class="btn btn--ghost" href="${contextPath}/main.jsp">메인으로</a>
	</div>

	<div class="member-admin__chips">
		<div class="chip">전체 회원<strong>${statusSummary.ALL}</strong></div>
		<div class="chip">활성<strong>${statusSummary.ACTIVE}</strong></div>
		<div class="chip">휴면/비활성<strong>${statusSummary.INACTIVE}</strong></div>
		<div class="chip">제재<strong>${statusSummary.BANNED}</strong></div>
		<div class="chip">탈퇴<strong>${statusSummary.WITHDRAWN}</strong></div>
	</div>

	<div class="filter-card">
		<form class="filter-form" method="get" action="${contextPath}/member/admin/list.me">
			<div>
				<label for="keyword">검색어</label>
				<input type="text" id="keyword" name="keyword" value="${keyword}" placeholder="아이디, 이름, 닉네임으로 검색">
			</div>
			<div>
				<label for="status">상태</label>
				<select id="status" name="status">
					<option value="ALL" <c:if test="${selectedStatus eq 'ALL'}">selected</c:if>>${statusLabelMap.ALL}</option>
					<option value="ACTIVE" <c:if test="${selectedStatus eq 'ACTIVE'}">selected</c:if>>${statusLabelMap.ACTIVE}</option>
					<option value="INACTIVE" <c:if test="${selectedStatus eq 'INACTIVE'}">selected</c:if>>${statusLabelMap.INACTIVE}</option>
					<option value="BANNED" <c:if test="${selectedStatus eq 'BANNED'}">selected</c:if>>${statusLabelMap.BANNED}</option>
					<option value="WITHDRAWN" <c:if test="${selectedStatus eq 'WITHDRAWN'}">selected</c:if>>${statusLabelMap.WITHDRAWN}</option>
				</select>
			</div>
			<button class="btn" type="submit">검색</button>
			<a class="btn btn--ghost" href="${contextPath}/member/admin/list.me">초기화</a>
		</form>
	</div>

	<div class="table-card">
		<div class="table-head">
			<h2>회원 목록</h2>
			<div>총 <strong>${memberCount}</strong>명</div>
		</div>
		<c:choose>
			<c:when test="${empty memberList}">
				<div class="empty-box">조건에 맞는 회원이 없습니다.</div>
			</c:when>
			<c:otherwise>
				<table class="member-table">
					<thead>
						<tr>
							<th>아이디</th>
							<th>이름</th>
							<th>닉네임</th>
							<th>권한</th>
							<th>상태</th>
							<th>이메일</th>
							<th>상세</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="member" items="${memberList}">
							<tr>
								<td>${member.memberId}</td>
								<td>${member.username}</td>
								<td>${member.nickname}</td>
								<td><span class="badge badge--${member.role}">${empty roleLabelMap[member.role] ? member.role : roleLabelMap[member.role]}</span></td>
								<td><span class="badge badge--${member.status}">${empty statusLabelMap[member.status] ? member.status : statusLabelMap[member.status]}</span></td>
								<td><c:out value="${empty member.email ? '-' : member.email}" /></td>
								<td><a class="link" href="${contextPath}/member/admin/detail.me?memberId=${member.memberId}">보기</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:otherwise>
		</c:choose>
	</div>
</div>
</body>
</html>