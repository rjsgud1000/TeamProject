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
<style>
	@media (max-width: 760px){
		body .member-admin{
			padding-left: 12px;
			padding-right: 12px;
		}

		body .member-admin > .table-card{
			overflow: hidden;
		}

		body .member-admin .table-head{
			align-items: stretch;
			flex-direction: column;
		}

		body .member-admin .member-table,
		body .member-admin .member-table thead,
		body .member-admin .member-table tbody,
		body .member-admin .member-table tr,
		body .member-admin .member-table th,
		body .member-admin .member-table td{
			display: block !important;
			width: 100% !important;
		}

		body .member-admin .member-table thead{
			display: none !important;
		}

		body .member-admin .member-table tbody{
			padding: 12px;
		}

		body .member-admin .member-table tr{
			margin: 0 0 12px;
			border: 1px solid #e2e8f0;
			border-radius: 18px;
			background: #fff;
			box-shadow: 0 10px 30px rgba(15, 23, 42, .06);
			overflow: hidden;
		}

		body .member-admin .member-table tr:last-child{
			margin-bottom: 0;
		}

		body .member-admin .member-table td{
			padding: 12px 14px !important;
			border-bottom: 1px solid #eef2f7;
			text-align: left !important;
			white-space: normal !important;
			word-break: break-word;
			overflow-wrap: anywhere;
		}

		body .member-admin .member-table td:last-child{
			border-bottom: 0;
		}

		body .member-admin .member-table td::before{
			content: attr(data-label);
			display: block !important;
			margin-bottom: 6px;
			font-size: 12px;
			font-weight: 900;
			color: #64748b;
		}
	}

	@media (max-width: 480px){
		body .member-admin__chips{
			grid-template-columns: repeat(2, minmax(0, 1fr));
		}

		body .member-admin .member-admin__hero h1{
			font-size: 24px;
		}
	}
</style>
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
		<div class="chip">경고<strong>${statusSummary.WARNING}</strong></div>
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
					<option value="WARNING" <c:if test="${selectedStatus eq 'WARNING'}">selected</c:if>>${statusLabelMap.WARNING}</option>
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
								<td data-label="아이디">${member.memberId}</td>
								<td data-label="이름">${member.username}</td>
								<td data-label="닉네임">${member.nickname}</td>
								<td data-label="권한"><span class="badge badge--${member.role}">${empty roleLabelMap[member.role] ? member.role : roleLabelMap[member.role]}</span></td>
								<td data-label="상태"><span class="badge badge--${member.status}">${empty statusLabelMap[member.status] ? member.status : statusLabelMap[member.status]}</span></td>
								<td data-label="이메일"><c:out value="${empty member.email ? '-' : member.email}" /></td>
								<td data-label="상세"><a class="link" href="${contextPath}/member/admin/detail.me?memberId=${member.memberId}">보기</a></td>
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