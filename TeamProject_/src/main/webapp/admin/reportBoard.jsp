<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>신고처리 게시판</title>
<link rel="stylesheet" href="${contextPath}/css/admin-member.css" />
</head>
<body>
<div class="member-admin report-board">
	<div class="member-admin__hero">
		<div>
			<h1>신고처리 게시판</h1>
			<p>신고 접수 내역과 처리 현황을 관리하는 관리자 페이지입니다.</p>
		</div>
		<div class="actions">
			<a class="btn btn--ghost" href="${contextPath}/member/admin/list.me">회원관리</a>
			<a class="btn btn--ghost" href="${contextPath}/main.jsp">메인으로</a>
		</div>
	</div>

	<div class="member-admin__chips report-board__chips">
		<div class="chip">전체 신고<strong>0</strong></div>
		<div class="chip">처리 대기<strong>0</strong></div>
		<div class="chip">처리 완료<strong>0</strong></div>
		<div class="chip">중복/반려<strong>0</strong></div>
	</div>

	<div class="filter-card">
		<form class="filter-form report-filter-form" action="#" method="get">
			<div>
				<label for="reportKeyword">검색어</label>
				<input type="text" id="reportKeyword" name="keyword" placeholder="신고자, 대상자, 게시글 제목 검색" disabled>
			</div>
			<div>
				<label for="reportStatus">처리 상태</label>
				<select id="reportStatus" name="status" disabled>
					<option>전체</option>
					<option>처리 대기</option>
					<option>처리 완료</option>
					<option>반려</option>
				</select>
			</div>
			<button class="btn" type="button" disabled>검색</button>
			<button class="btn btn--ghost" type="button" disabled>초기화</button>
		</form>
	</div>

	<div class="table-card">
		<div class="table-head">
			<h2>신고 목록</h2>
			<div>총 <strong>0</strong>건</div>
		</div>
		<table class="member-table report-table">
			<thead>
				<tr>
					<th>번호</th>
					<th>신고 분류</th>
					<th>신고자</th>
					<th>대상자</th>
					<th>신고 사유</th>
					<th>접수일</th>
					<th>처리 상태</th>
					<th>상세</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="8">
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
</body>
</html>