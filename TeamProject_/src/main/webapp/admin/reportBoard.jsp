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
		<div class="chip">전체 신고<strong>${totalReportCount}</strong></div>
		<div class="chip">처리 대기<strong>${totalReportCount}</strong></div>
		<div class="chip">처리 완료<strong>0</strong></div>
		<div class="chip">중복<strong>0</strong></div>
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

	<div class="card report-preview-card">
		<div class="table-head report-preview-card__head">
			<h2>신고당한 댓글 미리보기</h2>
			<div class="actions">
				<c:if test="${not empty selectedComment}">
					<a class="btn btn--ghost" href="${contextPath}/member/admin/reportList.me">닫기</a>
				</c:if>
			</div>
		</div>
		<c:choose>
			<c:when test="${not empty selectedComment}">
				<div class="report-preview">
					<div class="report-preview__meta">
						<div><strong>댓글 번호</strong> ${selectedComment.commentId}</div>
						<div><strong>게시글 번호</strong> ${selectedComment.postId}</div>
						<div><strong>작성자</strong> <c:out value="${empty selectedComment.memberNickname ? selectedComment.memberId : selectedComment.memberNickname}" /></div>
						<div><strong>작성일</strong> ${selectedCommentCreatedAtText}</div>
						<div><strong>삭제 여부</strong> ${selectedComment.isDeleted ? '삭제됨' : '정상'}</div>
					</div>
					<div class="report-preview__content"><c:out value="${selectedComment.content}" /></div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="empty-box report-empty-box">
					<p class="report-empty-box__title">신고 목록에서 댓글 원문 보기 버튼을 누르면 이 영역에 표시됩니다.</p>
					<p class="report-empty-box__desc">신고당한 댓글을 관리자 화면에서 바로 확인할 수 있습니다.</p>
					<c:if test="${not empty reportBoardMessage}"><p class="report-empty-box__desc"><c:out value="${reportBoardMessage}" /></p></c:if>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="table-card">
		<div class="table-head">
			<h2>신고 목록</h2>
			<div>총 <strong>${totalReportCount}</strong>건</div>
		</div>
		<c:choose>
			<c:when test="${empty reportList}">
				<div class="empty-box report-empty-box">
					<p class="report-empty-box__title">접수된 신고가 없습니다.</p>
					<p class="report-empty-box__desc">신고 데이터가 들어오면 이 영역에 자동으로 표시됩니다.</p>
				</div>
			</c:when>
			<c:otherwise>
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
						<c:forEach var="report" items="${reportList}">
							<tr>
								<td>${report.reportId}</td>
								<td>댓글 신고</td>
								<td><c:out value="${empty report.reportMemberNickname ? report.reportMemberId : report.reportMemberNickname}" /></td>
								<td><c:out value="${empty report.targetMemberNickname ? report.targetMemberId : report.targetMemberNickname}" /></td>
								<td><c:out value="${empty report.reason ? '-' : report.reason}" /></td>
								<td>${report.createdAt}</td>
								<td><span class="badge badge--BANNED">처리 대기</span></td>
								<td><a class="link" href="${contextPath}/member/admin/reportList.me?commentId=${report.commentId}">원문 보기</a></td>
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