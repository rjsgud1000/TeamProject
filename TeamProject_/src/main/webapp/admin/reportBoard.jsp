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
<style>
	.report-board.member-admin{
		max-width: 1180px;
	}
	.report-board .table-card{
		overflow: visible;
	}
	.report-board .report-table{
		width: 100%;
		table-layout: fixed;
		border-collapse: collapse;
	}
	.report-board .report-table th,
	.report-board .report-table td{
		padding: 14px 16px;
		border-bottom: 1px solid #eef2f7;
		font-size: 14px;
		text-align: left;
		vertical-align: top;
		overflow: visible;
	}
	.report-board .report-table__comment-col,
	.report-board .report-table__reason-col{
		width: 18%;
	}
	.report-board .report-table__content{
		white-space: normal;
		word-break: break-word;
		overflow-wrap: anywhere;
		line-height: 1.55;
	}
	.report-board .report-table__preview{
		position: relative;
		display: block;
		max-width: 100%;
		padding-bottom: 2px;
	}
	.report-board .report-table__preview::after{
		display: none !important;
		content: none !important;
	}
	.report-board .report-table__preview-text{
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
		overflow: hidden;
		line-height: 1.55;
		max-height: 4.65em;
		white-space: normal;
		word-break: break-word;
		overflow-wrap: anywhere;
	}
	.report-board .report-table__preview-text::after{
		content: none;
		display: none;
	}
	.report-board .report-table__hover-card{
		display: none;
		position: absolute;
		left: 0;
		top: calc(100% + 8px);
		z-index: 50;
		width: max-content;
		min-width: 220px;
		max-width: 420px;
		max-height: 260px;
		overflow-y: auto;
		pointer-events: auto;
		padding: 12px 14px;
		border: 1px solid #cbd5e1;
		border-radius: 14px;
		background: #fff;
		box-shadow: 0 18px 40px rgba(15, 23, 42, .18);
		white-space: normal;
		word-break: break-word;
		overflow-wrap: anywhere;
		line-height: 1.6;
	}
	.report-board .report-table__preview:hover .report-table__hover-card,
	.report-board .report-table__preview:focus .report-table__hover-card,
	.report-board .report-table__preview:focus-within .report-table__hover-card{
		display: block !important;
	}
	.report-board .report-table__status-cell,
	.report-board .report-table__action-cell{
		text-align: center !important;
		vertical-align: middle !important;
		white-space: nowrap !important;
	}
	.report-board .report-table__status-cell .badge,
	.report-board .report-table__action-cell .btn,
	.report-board .report-table__action-cell .report-table__dash,
	.report-board .report-table__action-cell form{
		margin: 0 auto;
	}
	.report-board .report-table__action-form{
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0;
		width: 100%;
		gap: 8px;
		flex-wrap: wrap;
	}
	.report-board .report-table__action-btn.btn{
		display: inline-flex !important;
		align-items: center !important;
		justify-content: center !important;
		height: 38px !important;
		min-width: 96px !important;
		padding: 0 14px !important;
		border-radius: 10px !important;
		font-size: 13px !important;
		line-height: 1 !important;
		white-space: nowrap !important;
	}
	.report-board .report-table__action-btn--danger.btn{
		background: #fff1f2 !important;
		border: 1px solid #fecdd3 !important;
		color: #be123c !important;
	}
	.report-board .report-table__flash{
		margin-bottom: 18px;
		padding: 14px 16px;
		border: 1px solid #bfdbfe;
		border-radius: 14px;
		background: #eff6ff;
		color: #1d4ed8;
		font-size: 14px;
		font-weight: 700;
	}
	.report-board .report-filter{
		display: flex;
		gap: 10px;
		flex-wrap: wrap;
		align-items: center;
	}
	.report-board .report-filter__label{
		font-size: 13px;
		color: #64748b;
		font-weight: 700;
	}
	.report-board .report-filter__link{
		display: inline-flex;
		align-items: center;
		justify-content: center;
		min-height: 36px;
		padding: 0 14px;
		border-radius: 999px;
		border: 1px solid #dbe3ef;
		background: #fff;
		color: #334155;
		text-decoration: none;
		font-size: 13px;
		font-weight: 700;
	}
	.report-board .report-filter__link.is-active{
		background: #2563eb;
		border-color: #2563eb;
		color: #fff;
	}
	.report-board .report-filter__summary{
		font-size: 13px;
		color: #475569;
	}
	@media (max-width: 900px){
		.report-board .report-table{
			table-layout: auto;
		}
		.report-board .report-table__preview-text{
			display: block;
			-webkit-line-clamp: initial;
			max-height: none;
		}
		.report-board .report-table__hover-card{
			display: none !important;
		}
	}
	@media (max-width: 760px){
		.report-board.member-admin{
			padding-left: 12px;
			padding-right: 12px;
		}
		.report-board .member-admin__hero,
		.report-board .table-head{
			align-items: stretch;
		}
		.report-board .actions,
		.report-board .report-filter{
			width: 100%;
		}
		.report-board .actions .btn,
		.report-board .report-filter__link{
			flex: 1 1 calc(50% - 6px);
			min-width: 0;
		}
		.report-board .table-card{
			overflow: hidden;
		}
		.report-board .report-table,
		.report-board .report-table thead,
		.report-board .report-table tbody,
		.report-board .report-table tr,
		.report-board .report-table th,
		.report-board .report-table td{
			display: block;
			width: 100%;
		}
		.report-board .report-table thead{
			display: none;
		}
		.report-board .report-table tbody{
			padding: 12px;
		}
		.report-board .report-table tr{
			border: 1px solid #e2e8f0;
			border-radius: 18px;
			background: #fff;
			box-shadow: 0 10px 30px rgba(15, 23, 42, .06);
			overflow: hidden;
		}
		.report-board .report-table tr + tr{
			margin-top: 12px;
		}
		.report-board .report-table td{
			padding: 12px 14px;
			border-bottom: 1px solid #eef2f7;
		}
		.report-board .report-table td:last-child{
			border-bottom: 0;
		}
		.report-board .report-table td::before{
			display: block;
			margin-bottom: 6px;
			font-size: 12px;
			font-weight: 900;
			color: #64748b;
		}
		.report-board .report-table td:nth-child(1)::before{ content:'번호'; }
		.report-board .report-table td:nth-child(2)::before{ content:'분류'; }
		.report-board .report-table td:nth-child(3)::before{ content:'신고자'; }
		.report-board .report-table td:nth-child(4)::before{ content:'대상자'; }
		.report-board .report-table td:nth-child(5)::before{ content:'게시글'; }
		.report-board .report-table td:nth-child(6)::before{ content:'댓글 내용'; }
		.report-board .report-table td:nth-child(7)::before{ content:'신고 사유'; }
		.report-board .report-table td:nth-child(8)::before{ content:'접수일'; }
		.report-board .report-table td:nth-child(9)::before{ content:'처리상태'; }
		.report-board .report-table td:nth-child(10)::before{ content:'처리'; }
		.report-board .report-table__status-cell,
		.report-board .report-table__action-cell{
			text-align: left !important;
			white-space: normal !important;
		}
		.report-board .report-table__status-cell .badge,
		.report-board .report-table__action-cell .btn,
		.report-board .report-table__action-cell .report-table__dash,
		.report-board .report-table__action-cell form{
			margin: 0;
		}
		.report-board .report-table__action-form{
			justify-content: flex-start;
		}
		.report-board .report-table__action-btn.btn{
			width: 100% !important;
			max-width: 220px;
		}
	}
	@media (max-width: 480px){
		.report-board .actions .btn,
		.report-board .report-filter__link{
			flex-basis: 100%;
		}
		.report-board .member-admin__hero h1{
			font-size: 24px;
		}
		.report-board .table-head h2{
			font-size: 18px;
		}
	}
</style>
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
										<c:otherwise><span class="badge badge--pending">처리안됨</span></c:otherwise>
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