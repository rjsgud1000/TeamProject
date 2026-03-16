<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-member.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-post-report.css" />

<div class="member-admin report-board post-report-page">
	<div class="member-admin__hero post-report-page__header">
		<div>
			<h1>게시글 신고 관리</h1>
			<p>접수된 게시글 신고 내역을 확인하고 처리 상태를 관리합니다.</p>
		</div>
	</div>

	<div class="table-card post-report-card">
		<div class="table-head post-report-table-head">
			<h2>신고 접수 목록</h2>
			<span class="post-report-table-head__count">총 ${empty reportList ? 0 : reportList.size()}건</span>
		</div>
		<div class="post-report-table-wrap">
			<table class="member-table post-report-table">
				<thead>
					<tr>
						<th>신고번호</th>
						<th>게시글</th>
						<th>작성자</th>
						<th>신고자</th>
						<th>신고일</th>
						<th>상태</th>
						<th>관리</th>
						<th>신고사유</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${empty reportList}">
							<tr>
								<td colspan="8" class="post-report-table__empty">신고 내역이 없습니다.</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:forEach var="report" items="${reportList}">
								<tr>
									<td data-label="신고번호">${report.reportId}</td>
									<td data-label="게시글">
										<a class="post-report-link link" href="${pageContext.request.contextPath}/board/detail?postId=${report.postId}">
											${report.postTitle}
										</a>
									</td>
									<td data-label="작성자">${report.postWriterNickname}</td>
									<td data-label="신고자">${report.reporterNickname}</td>
									<td data-label="신고일"><fmt:formatDate value="${report.reportedAt}" pattern="yyyy-MM-dd HH:mm" /></td>
									<td data-label="상태">
										<c:choose>
											<c:when test="${report.status == 'PENDING'}">
												<span class="badge report-badge report-badge--pending">대기</span>
											</c:when>
											<c:when test="${report.status == 'RESOLVED'}">
												<span class="badge report-badge report-badge--resolved">처리완료</span>
											</c:when>
											<c:when test="${report.status == 'REJECTED'}">
												<span class="badge report-badge report-badge--rejected">반려</span>
											</c:when>
											<c:otherwise>
												<span class="badge report-badge">${report.status}</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td data-label="관리">
										<c:choose>
											<c:when test="${report.status == 'PENDING'}">
												<div class="post-report-actions">
													<form action="${pageContext.request.contextPath}/admin/postReportProcess" method="post">
														<input type="hidden" name="reportId" value="${report.reportId}">
														<input type="hidden" name="postId" value="${report.postId}">
														<input type="hidden" name="status" value="RESOLVED">
														<button type="submit" class="btn report-btn report-btn--success" onclick="return confirm('이 신고를 처리완료하고, 게시글을 블라인드 처리하시겠습니까?');">처리완료</button>
													</form>

													<form action="${pageContext.request.contextPath}/admin/postReportProcess" method="post">
														<input type="hidden" name="reportId" value="${report.reportId}">
														<input type="hidden" name="postId" value="${report.postId}">
														<input type="hidden" name="status" value="REJECTED">
														<button type="submit" class="btn report-btn report-btn--danger" onclick="return confirm('이 신고를 반려하시겠습니까?');">반려</button>
													</form>
												</div>
											</c:when>
											<c:when test="${report.status == 'RESOLVED' && report.blinded == 1}">
												<div class="post-report-actions post-report-actions--stack">
													<span class="post-report-done">처리됨</span>
													<form action="${pageContext.request.contextPath}/admin/unblindPost" method="post">
														<input type="hidden" name="postId" value="${report.postId}">
														<button type="submit" class="btn report-btn report-btn--warning" onclick="return confirm('이 게시글의 블라인드를 해제하시겠습니까?');">블라인드 해제</button>
													</form>
												</div>
											</c:when>
											<c:otherwise>
												<span class="post-report-done">처리됨</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td data-label="신고사유" class="post-report-reason">${report.reason}</td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</div>
</div>