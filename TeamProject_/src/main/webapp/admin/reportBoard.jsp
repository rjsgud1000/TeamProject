<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="flash" value="${sessionScope.adminMemberFlash}" />
<c:if test="${not empty flash}">
	<c:remove var="adminMemberFlash" scope="session" />
</c:if>
<c:set var="reportCount" value="${empty requestScope.reportCount ? 0 : requestScope.reportCount}" />
<c:set var="pendingReportCount" value="${empty requestScope.pendingReportCount ? 0 : requestScope.pendingReportCount}" />
<c:set var="completedReportCount" value="${empty requestScope.completedReportCount ? 0 : requestScope.completedReportCount}" />
<c:set var="filteredReportCount" value="${empty requestScope.filteredReportCount ? 0 : requestScope.filteredReportCount}" />
<c:set var="selectedReportFilter" value="${empty requestScope.selectedReportFilter ? 'ALL' : requestScope.selectedReportFilter}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>신고처리 게시판</title>
<link rel="stylesheet" href="${contextPath}/css/admin-member.css" />
<link rel="stylesheet" href="${contextPath}/css/admin-pages.css" />
</head>
<body>
<div class="member-admin report-board">
	<c:if test="${not empty flash}">
		<div class="report-table__flash"><c:out value="${flash}" /></div>
	</c:if>
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
			<div>
				<h2>신고 목록</h2>
				<div class="report-filter__summary">현재 <strong>${filteredReportCount}</strong>건 표시 중</div>
			</div>
			<div class="report-filter">
				<span class="report-filter__label">상태 필터</span>
				<a class="report-filter__link ${selectedReportFilter eq 'ALL' ? 'is-active' : ''}" href="${contextPath}/member/admin/reportList.me?filter=ALL">전체</a>
				<a class="report-filter__link ${selectedReportFilter eq 'PENDING' ? 'is-active' : ''}" href="${contextPath}/member/admin/reportList.me?filter=PENDING">처리대기만</a>
				<a class="report-filter__link ${selectedReportFilter eq 'COMPLETED' ? 'is-active' : ''}" href="${contextPath}/member/admin/reportList.me?filter=COMPLETED">처리완료만</a>
			</div>
		</div>
		<c:choose>
			<c:when test="${empty commentReportList}">
				<div class="empty-box">선택한 조건의 댓글 신고가 없습니다.</div>
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
							<th class="report-table__comment-col">댓글 내용</th>
							<th class="report-table__reason-col">신고 사유</th>
							<th>접수일</th>
							<th>처리상태</th>
							<th>처리</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="report" items="${commentReportList}">
							<tr>
								<td data-label="번호">${report.reportId}</td>
								<td data-label="분류">댓글</td>
								<td data-label="신고자"><c:out value="${report.reportMemberId}" /></td>
								<td data-label="대상자"><a class="link" href="${contextPath}/member/admin/detail.me?memberId=${report.targetMemberId}"><c:out value="${report.targetMemberId}" /></a></td>
								<td data-label="게시글"><c:out value="${report.postTitle}" /></td>
								<td class="report-table__content" data-label="댓글 내용">
									<div class="report-table__preview" tabindex="0" title="${report.commentContent}">
										<div class="report-table__preview-text"><c:out value="${report.commentContent}" /></div>
										<div class="report-table__hover-card"><c:out value="${report.commentContent}" /></div>
									</div>
								</td>
								<td class="report-table__content" data-label="신고 사유">
									<div class="report-table__preview" tabindex="0" title="${report.reason}">
										<div class="report-table__preview-text"><c:out value="${report.reason}" /></div>
										<div class="report-table__hover-card"><c:out value="${report.reason}" /></div>
									</div>
								</td>
								<td data-label="접수일"><c:out value="${report.createdAt}" /></td>
								<td class="report-table__status-cell" data-label="처리상태">
									<c:choose>
										<c:when test="${report.status eq 'REJECTED'}"><span class="badge badge--danger">반려</span></c:when>
										<c:when test="${report.status eq 'RESOLVED'}"><span class="badge badge--done">처리완료</span></c:when>
										<c:otherwise><span class="badge badge--pending">처리대기</span></c:otherwise>
									</c:choose>
								</td>
								<td class="report-table__action-cell" data-label="처리">
									<c:if test="${report.status eq 'PENDING'}">
										<form method="post" action="${contextPath}/member/admin/report/process.me" class="report-table__action-form">
											<input type="hidden" name="reportId" value="${report.reportId}">
											<input type="hidden" name="filter" value="${selectedReportFilter}">
											<button class="btn report-table__action-btn" type="submit" name="action" value="BLIND" onclick="return confirm('이 댓글 신고를 처리완료하고 댓글을 블라인드 처리하시겠습니까?');">처리완료</button>
											<button class="btn report-table__action-btn report-table__action-btn--danger" type="submit" name="action" value="REJECT" onclick="return confirm('이 댓글 신고를 반려하시겠습니까?');">반려</button>
										</form>
									</c:if>
									<c:if test="${report.status ne 'PENDING'}"><span class="report-table__dash">-</span></c:if>
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