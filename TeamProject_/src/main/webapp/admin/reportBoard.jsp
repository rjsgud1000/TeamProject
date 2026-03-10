<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="reportCount" value="${empty requestScope.reportCount ? 0 : requestScope.reportCount}" />
<c:set var="pendingReportCount" value="${empty requestScope.pendingReportCount ? 0 : requestScope.pendingReportCount}" />
<c:set var="completedReportCount" value="${empty requestScope.completedReportCount ? 0 : requestScope.completedReportCount}" />
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
			<p>댓글 신고 접수 내역을 관리하는 관리자 페이지입니다.</p>
		</div>
		<div class="actions">
			<a class="btn btn--ghost" href="${contextPath}/member/admin/list.me">회원관리</a>
			<a class="btn btn--ghost" href="${contextPath}/main.jsp">메인으로</a>
		</div>
	</div>

	<div class="member-admin__chips report-board__chips">
		<div class="chip">전체 신고<strong>${reportCount}</strong></div>
		<div class="chip">처리 대기<strong>${pendingReportCount}</strong></div>
		<div class="chip">처리 완료<strong>${completedReportCount}</strong></div>
	</div>

	<div class="table-card">
		<div class="table-head">
			<h2>신고 목록</h2>
			<div>총 <strong>${reportCount}</strong>건</div>
		</div>
		<c:choose>
			<c:when test="${empty commentReportList}">
				<div class="empty-box">접수된 댓글 신고가 없습니다.</div>
			</c:when>
			<c:otherwise>
				<table class="member-table report-table">
					<thead>
						<tr>
							<th>번호</th>
							<th>분류</th>
							<th>신고자</th>
							<th>대상자</th>
							<th>게시글</th>
							<th>댓글 내용</th>
							<th>신고 사유</th>
							<th>접수일</th>
							<th>처리상태</th>
							<th>처리</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="report" items="${commentReportList}">
							<tr>
								<td>${report.reportId}</td>
								<td>댓글</td>
								<td><c:out value="${report.reportMemberId}" /></td>
								<td><a class="link" href="${contextPath}/member/admin/detail.me?memberId=${report.targetMemberId}"><c:out value="${report.targetMemberId}" /></a></td>
								<td><c:out value="${report.postTitle}" /></td>
								<td><c:out value="${report.commentContent}" /></td>
								<td><c:out value="${report.reason}" /></td>
								<td><c:out value="${report.createdAt}" /></td>
								<td>
									<c:choose>
										<c:when test="${report.processed}">처리됨</c:when>
										<c:otherwise>처리안됨</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:if test="${not report.processed}">
										<form method="post" action="${contextPath}/member/admin/report/process.me" style="margin:0;">
											<input type="hidden" name="reportId" value="${report.reportId}">
											<button class="btn" type="submit">처리완료</button>
										</form>
									</c:if>
									<c:if test="${report.processed}">-</c:if>
								</td>
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