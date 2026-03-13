<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="container mt-4">
	<h2>게시글 신고 관리</h2>

	<table class="table table-bordered table-hover mt-3">
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
						<td colspan="7" class="text-center">신고 내역이 없습니다.</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="report" items="${reportList}">
						<tr>
							<td>${report.reportId}</td>
							<td><a
								href="${pageContext.request.contextPath}/board/detail?postId=${report.postId}">
									${report.postTitle} </a></td>
							<td>${report.postWriterNickname}</td>
							<td>${report.reporterNickname}</td>
							<td><fmt:formatDate value="${report.reportedAt}"
									pattern="yyyy-MM-dd HH:mm" /></td>
							<td><c:choose>
									<c:when test="${report.status == 'PENDING'}">
										<span style="color: orange; font-weight: bold;">대기</span>
									</c:when>
									<c:when test="${report.status == 'RESOLVED'}">
										<span style="color: green; font-weight: bold;">처리완료</span>
									</c:when>
									<c:when test="${report.status == 'REJECTED'}">
										<span style="color: red; font-weight: bold;">반려</span>
									</c:when>
									<c:otherwise>
                                        ${report.status}
                                    </c:otherwise>
								</c:choose></td>
							<td><c:choose>

									<%-- 1. 아직 처리 전이면: 처리완료 / 반려 버튼 노출 --%>
									<c:when test="${report.status == 'PENDING'}">
										<form
											action="${pageContext.request.contextPath}/admin/postReportProcess"
											method="post" style="display: inline;">
											<input type="hidden" name="reportId"
												value="${report.reportId}"> <input type="hidden"
												name="postId" value="${report.postId}"> <input
												type="hidden" name="status" value="RESOLVED">
											<button type="submit" class="btn btn-sm btn-success"
												onclick="return confirm('이 신고를 처리완료하고, 게시글을 블라인드 처리하시겠습니까?');">
												처리완료</button>
										</form>

										<form
											action="${pageContext.request.contextPath}/admin/postReportProcess"
											method="post" style="display: inline;">
											<input type="hidden" name="reportId"
												value="${report.reportId}"> <input type="hidden"
												name="postId" value="${report.postId}"> <input
												type="hidden" name="status" value="REJECTED">
											<button type="submit" class="btn btn-sm btn-danger"
												onclick="return confirm('이 신고를 반려하시겠습니까?');">반려</button>
										</form>
									</c:when>

									<%-- 2. 처리완료 + 현재 블라인드 상태면: 블라인드 해제 버튼 노출 --%>
									<c:when
										test="${report.status == 'RESOLVED' && report.blinded == 1}">
										<span>처리됨</span>
										<form
											action="${pageContext.request.contextPath}/admin/unblindPost"
											method="post" style="display: inline; margin-left: 5px;">
											<input type="hidden" name="postId" value="${report.postId}">
											<button type="submit" class="btn btn-sm btn-warning"
												onclick="return confirm('이 게시글의 블라인드를 해제하시겠습니까?');">
												블라인드 해제</button>
										</form>
									</c:when>

									<%-- 3. 그 외(반려, 또는 이미 해제된 경우)는 처리됨만 표시 --%>
									<c:otherwise>
										<span>처리됨</span>
									</c:otherwise>

								</c:choose></td>
							<td>${report.reason}</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>