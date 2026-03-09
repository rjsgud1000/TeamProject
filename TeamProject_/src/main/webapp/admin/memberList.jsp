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
<style>
.member-admin{max-width:1180px;margin:0 auto;padding:8px 0 24px;}
.member-admin__hero{display:flex;justify-content:space-between;gap:16px;align-items:end;flex-wrap:wrap;margin-bottom:20px;}
.member-admin__hero h1{margin:0;font-size:30px;}
.member-admin__hero p{margin:8px 0 0;color:#64748b;font-weight:700;}
.member-admin__chips{display:grid;grid-template-columns:repeat(auto-fit,minmax(140px,1fr));gap:12px;margin-bottom:18px;}
.chip{background:#fff;border:1px solid #e2e8f0;border-radius:18px;padding:16px;box-shadow:0 10px 30px rgba(15,23,42,.06);}
.chip strong{display:block;font-size:24px;margin-top:6px;color:#0f172a;}
.filter-card,.table-card{background:#fff;border:1px solid #e2e8f0;border-radius:22px;box-shadow:0 14px 34px rgba(15,23,42,.08);}
.filter-card{padding:18px;margin-bottom:18px;}
.filter-form{display:grid;grid-template-columns:1.7fr 1fr auto auto;gap:12px;align-items:end;}
.filter-form label{display:block;font-size:13px;font-weight:900;color:#334155;margin-bottom:6px;}
.filter-form input,.filter-form select{width:100%;height:44px;border:1px solid #cbd5e1;border-radius:12px;padding:0 14px;font-size:14px;}
.btn{height:44px;padding:0 18px;border-radius:12px;border:1px solid #2563eb;background:#2563eb;color:#fff;font-weight:900;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;text-decoration:none;}
.btn--ghost{background:#fff;color:#334155;border-color:#cbd5e1;}
.table-card{overflow:hidden;}
.table-head{padding:18px 20px;border-bottom:1px solid #e2e8f0;display:flex;justify-content:space-between;align-items:center;gap:10px;}
.table-head h2{margin:0;font-size:20px;}
.member-table{width:100%;border-collapse:collapse;}
.member-table th,.member-table td{padding:14px 16px;border-bottom:1px solid #eef2f7;text-align:left;font-size:14px;}
.member-table th{background:#f8fafc;font-size:13px;color:#475569;}
.member-table tr:hover{background:#f8fbff;}
.badge{display:inline-flex;align-items:center;justify-content:center;padding:6px 10px;border-radius:999px;font-size:12px;font-weight:900;}
.badge--ACTIVE{background:#dcfce7;color:#166534;}.badge--INACTIVE{background:#fef3c7;color:#92400e;}.badge--BANNED{background:#fee2e2;color:#b91c1c;}.badge--WITHDRAWN{background:#e2e8f0;color:#475569;}.badge--ADMIN{background:#dbeafe;color:#1d4ed8;}.badge--USER{background:#f1f5f9;color:#334155;}
.empty-box{padding:48px 20px;text-align:center;color:#64748b;font-weight:700;}
.link{color:#1d4ed8;font-weight:900;text-decoration:none;}
@media (max-width:900px){.filter-form{grid-template-columns:1fr;}.member-table{font-size:13px;}}
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
					<option value="ALL" <c:if test="${selectedStatus eq 'ALL'}">selected</c:if>>전체</option>
					<option value="ACTIVE" <c:if test="${selectedStatus eq 'ACTIVE'}">selected</c:if>>ACTIVE</option>
					<option value="INACTIVE" <c:if test="${selectedStatus eq 'INACTIVE'}">selected</c:if>>INACTIVE</option>
					<option value="BANNED" <c:if test="${selectedStatus eq 'BANNED'}">selected</c:if>>BANNED</option>
					<option value="WITHDRAWN" <c:if test="${selectedStatus eq 'WITHDRAWN'}">selected</c:if>>WITHDRAWN</option>
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
								<td><span class="badge badge--${member.role}">${member.role}</span></td>
								<td><span class="badge badge--${member.status}">${member.status}</span></td>
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