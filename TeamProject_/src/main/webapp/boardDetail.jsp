<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<style>
.detail-container {
    max-width: 1100px;
    margin: 30px auto 60px;
    padding: 0 20px;
}

.detail-title {
    font-size: 42px;
    font-weight: 800;
    color: #111827;
    margin-bottom: 22px;
    letter-spacing: -0.5px;
}

.detail-card {
    background: #f8fafc;
    border-radius: 16px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    overflow: hidden;
    margin-bottom: 20px;
}

.detail-table {
    width: 100%;
    border-collapse: collapse;
}

.detail-table th {
    width: 140px;
    background: #eef4ff;
    color: #334155;
    font-weight: 700;
    text-align: center;
    padding: 14px 12px;
    border-bottom: 1px solid #dbe4f0;
}

.detail-table td {
    background: #ffffff;
    color: #222;
    padding: 14px 16px;
    border-bottom: 1px solid #edf1f5;
}

.detail-table .content-th {
    vertical-align: middle;
}

.detail-content {
    min-height: 220px;
    line-height: 1.7;
    white-space: pre-wrap;
    margin: 0;
    font-family: inherit;
    font-size: 15px;
}

.detail-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
    margin: 18px 0 24px;
}

.detail-left-actions,
.detail-right-actions {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
}

.text-link {
    text-decoration: none;
    color: #334155;
    font-weight: 600;
}

.text-link:hover {
    color: #2563eb;
}

.action-btn,
.small-btn {
    border: none;
    background: #f1f5f9;
    color: #334155;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.action-btn {
    height: 38px;
    padding: 0 14px;
    font-size: 14px;
}

.small-btn {
    height: 30px;
    padding: 0 10px;
    font-size: 12px;
}

.primary-btn {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: white;
}

.danger-btn {
    background: #ef4444;
    color: white;
}

.pill-box {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 18px;
    border: 1px solid #cbd5e1;
    border-radius: 999px;
    background: #ffffff;
    color: #334155;
    font-weight: 700;
    font-size: 14px;
}

.select-box {
    height: 38px;
    padding: 0 10px;
    border: 1px solid #cbd5e1;
    border-radius: 8px;
    background: white;
}

.comment-section {
    margin-top: 40px;
}

.comment-title {
    font-size: 28px;
    font-weight: 800;
    color: #111827;
    margin-bottom: 16px;
}

.comment-list {
    background: #f8fafc;
    border-radius: 16px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    overflow: hidden;
}

.comment-item {
    padding: 18px 20px;
    border-bottom: 1px solid #e5e7eb;
    background: #ffffff;
}

.comment-item:last-child {
    border-bottom: none;
}

.comment-reply {
    margin-left: 36px;
    background: #f9fbff;
    border-left: 3px solid #bfdbfe;
}

.comment-header {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
    margin-bottom: 8px;
    font-size: 14px;
}

.comment-writer {
    font-weight: 700;
    color: #111827;
}

.comment-date {
    color: #6b7280;
    font-size: 13px;
}

.comment-content {
    font-size: 15px;
    color: #222;
    line-height: 1.6;
    margin-bottom: 12px;
}

.comment-actions {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: wrap;
}

.comment-inline-form {
    display: inline-flex;
    gap: 6px;
    align-items: center;
}

.comment-form-box {
    margin-top: 10px;
    padding: 12px;
    background: #f8fafc;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
}

.comment-textarea {
    width: 100%;
    min-height: 80px;
    border: 1px solid #cbd5e1;
    border-radius: 10px;
    padding: 12px;
    resize: vertical;
    font-family: inherit;
    font-size: 14px;
    box-sizing: border-box;
}

.comment-submit-wrap {
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
}

.comment-pagination {
    margin-top: 20px;
    text-align: center;
}

.comment-pagination a,
.comment-pagination span {
    display: inline-block;
    margin: 0 4px;
    padding: 6px 10px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: 600;
    font-size: 14px;
}

.comment-pagination a {
    color: #334155;
}

.comment-pagination a:hover {
    background: #eef4ff;
    color: #2563eb;
}

.comment-pagination .current {
    background: #2563eb;
    color: white;
}

.comment-write-box {
    margin-top: 20px;
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    padding: 18px;
}

@media (max-width: 768px) {
    .detail-container {
        padding: 0 12px;
    }

    .detail-title {
        font-size: 32px;
    }

    .detail-table th,
    .detail-table td {
        display: block;
        width: 100%;
        box-sizing: border-box;
    }

    .detail-actions {
        flex-direction: column;
        align-items: stretch;
    }

    .comment-reply {
        margin-left: 18px;
    }
}
</style>

<div class="detail-container">
	
	<!-- 게시글 신고 접수판 -->
	<c:if test="${param.reportSuccess == '1'}">
		<script>
        alert('게시글 신고가 접수되었습니다.');
    </script>
	</c:if>

	<c:if test="${param.reportDuplicate == '1'}">
		<script>
        alert('이미 신고한 게시글입니다.');
    </script>
	</c:if>

	<!-- 댓글수 제한 -->
	<c:if test="${param.error == 'commentLength'}">
		<script>
        alert('댓글은 최대 50자까지만 입력할 수 있습니다.');
    </script>
	</c:if>

	<div class="detail-card">
		<table class="detail-table">
			<tr>
				<th>번호</th>
				<td>${post.postId}</td>
				<th>카테고리</th>
				<td><c:choose>
						<c:when test="${post.category == '0' || post.category == 0}">공지</c:when>
						<c:when test="${post.category == '1' || post.category == 1}">자유</c:when>
						<c:when test="${post.category == '2' || post.category == 2}">질문</c:when>
						<c:when test="${post.category == '3' || post.category == 3}">파티</c:when>
						<c:otherwise>기타</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>제목</th>
				<td colspan="3">${post.title}</td>
			</tr>
			<tr>
				<th>작성자</th>
				<td>${post.writer}</td>
				<th>조회수</th>
				<td>${post.viewCount}</td>
			</tr>
			<tr>
				<th>작성일</th>
				<td colspan="3"><fmt:formatDate value="${post.createdAt}"
						pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<tr>
				<th class="content-th">내용</th>
				<td colspan="3"><pre class="detail-content">${post.content}</pre>
				</td>
			</tr>
		</table>
	</div>

	<!-- 게시판 글쓰기 수정 삭제 목록(관리자권한추가) -->
	<div class="detail-actions">
		<div class="detail-left-actions">
			<a
				href="${pageContext.request.contextPath}/board/list?category=${category}&page=${page}"
				class="text-link"> 목록으로 </a>
		</div>

		<div class="detail-right-actions">
			<c:choose>
				<c:when test="${post.category == '0' || post.category == 0}">
					<c:if
						test="${sessionScope.loginMember != null && sessionScope.loginMember.role eq 'ADMIN'}">
						<a
							href="${pageContext.request.contextPath}/board/edit?postId=${post.postId}&category=${category}&page=${page}"
							class="action-btn"> 수정 </a>
						<a
							href="${pageContext.request.contextPath}/board/delete?postId=${post.postId}&category=${category}&page=${page}"
							class="action-btn danger-btn"
							onclick="return confirm('정말 삭제하시겠습니까?');"> 삭제 </a>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if
						test="${sessionScope.loginMember != null && sessionScope.loginMember.memberId == post.memberId}">
						<a
							href="${pageContext.request.contextPath}/board/edit?postId=${post.postId}&category=${category}&page=${page}"
							class="action-btn"> 수정 </a>
						<a
							href="${pageContext.request.contextPath}/board/delete?postId=${post.postId}&category=${category}&page=${page}"
							class="action-btn danger-btn"
							onclick="return confirm('정말 삭제하시겠습니까?');"> 삭제 </a>
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<%-- 질문글에 답변글 작성기능 --%>
			<c:if test="${post.category == '2' || post.category == 2}">
		    <c:if test="${post.acceptedCommentId == null}">
		        <c:if test="${sessionScope.loginMember != null}">
		            |
		            <a href="${pageContext.request.contextPath}/board/write?category=2&parentPostId=${post.postId}">
		                답변달기
		            </a>
		        </c:if>
		    	</c:if>
			</c:if>
			
	<%-- 답변글에 질문글로 가는 링크 --%>
	<c:if test="${post.category == '2' || post.category == 2}">
	    <c:if test="${post.acceptedCommentId != null}">
		        <div style="margin-top:10px;">
		            <a href="${pageContext.request.contextPath}/board/detail?postId=${post.acceptedCommentId}&category=2&page=1">
		                원문 질문 보기
		            </a>
		        </div>
		    </c:if>
		</c:if>

	<!-- 추천하기 및 취소 탭 : 공지사항(category=0)에서는 숨김 -->
	<c:if test="${post.category != '0' && post.category != 0}">
		<div style="margin-top: 20px; text-align: center;">

			<c:choose>
				<c:when test="${sessionScope.loginMember == null}">
					<div class="pill-box">
						<span style="font-size: 18px;">👍</span> <span>추천
							${likeCount}</span>
					</div>

					<div style="margin-top: 8px; color: gray; font-size: 14px;">
						로그인 후 추천 가능합니다.</div>
				</c:when>

				<c:otherwise>
					<form action="${pageContext.request.contextPath}/board/like"
						method="post" style="display: inline-block; margin: 0;">
						<input type="hidden" name="postId" value="${post.postId}">
						<input type="hidden" name="category" value="${category}">
						<input type="hidden" name="page" value="${page}"> <input
							type="hidden" name="commentPage" value="${commentPage}" />

						<button type="submit" class="pill-box"
							style="${liked ? 'background:#2563eb; color:white; border-color:#2563eb;' : ''}">
							<span style="font-size: 18px;">👍</span> <span> <c:choose>
									<c:when test="${liked}">추천됨 ${likeCount}</c:when>
									<c:otherwise>추천 ${likeCount}</c:otherwise>
								</c:choose>
							</span>
						</button>
					</form>
				</c:otherwise>
			</c:choose>

		</div>
	</c:if>

	<!-- 게시글 신고버튼 -->
	<c:if test="${post.category != '0' && post.category != 0}">
		<div style="margin-top: 12px; text-align: center;">

			<c:if test="${sessionScope.loginMember != null}">
				<form action="${pageContext.request.contextPath}/board/report"
					method="post"
					style="display: inline-flex; align-items: center; gap: 8px; flex-wrap: wrap;">
					<input type="hidden" name="postId" value="${post.postId}">
					<input type="hidden" name="category" value="${category}"> <input
						type="hidden" name="page" value="${page}"> <input
						type="hidden" name="commentPage" value="${commentPage}" /> <select
						name="reason" class="select-box">
						<option value="욕설/비방">욕설/비방</option>
						<option value="도배/스팸">도배/스팸</option>
						<option value="음란/부적절">음란/부적절</option>
						<option value="기타">기타</option>
					</select>

					<button type="submit" onclick="return confirm('이 게시글을 신고하시겠습니까?');"
						class="action-btn danger-btn">🚨 신고하기</button>
				</form>
			</c:if>

			<c:if test="${sessionScope.loginMember == null}">
				<div style="margin-top: 8px; color: gray; font-size: 14px;">
					신고는 로그인 후 가능합니다.</div>
			</c:if>

		</div>
	</c:if>

	<!-- 댓글 영역 : 공지사항(category=0)에서는 숨김 -->
	<c:if test="${post.category != '0' && post.category != 0}">
		<div class="comment-section">
			<h3 class="comment-title">댓글</h3>
			<div class="comment-list">

				<c:forEach var="c" items="${comments}">
					<div class="comment-item ${c.parentCommentId != null ? 'comment-reply' : ''}">

						<div class="comment-header">
							<span class="comment-writer">${c.memberNickname}</span> <span
								class="comment-date"> <fmt:formatDate
									value="${c.createdAt}" pattern="yyyy-MM-dd HH:mm" />
							</span>
						</div>

						<div class="comment-content">${c.content}</div>

						<div class="comment-actions">
							<!-- 좋아요 / 싫어요 -->
							<c:choose>
								<c:when test="${sessionScope.loginMember != null}">
									<form action="${pageContext.request.contextPath}/board/detail"
										method="post" class="comment-inline-form">
										<input type="hidden" name="action" value="like" /> <input
											type="hidden" name="commentId" value="${c.commentId}" /> <input
											type="hidden" name="postId" value="${post.postId}" /> <input
											type="hidden" name="category" value="${category}" /> <input
											type="hidden" name="page" value="${page}" /> <input
											type="hidden" name="commentPage" value="${commentPage}" />
										<button type="submit" class="small-btn">👍
											${c.likeCount}</button>
									</form>

									<form action="${pageContext.request.contextPath}/board/detail"
										method="post" class="comment-inline-form">
										<input type="hidden" name="action" value="dislike" /> <input
											type="hidden" name="commentId" value="${c.commentId}" /> <input
											type="hidden" name="postId" value="${post.postId}" /> <input
											type="hidden" name="category" value="${category}" /> <input
											type="hidden" name="page" value="${page}" /> <input
											type="hidden" name="commentPage" value="${commentPage}" />
										<button type="submit" class="small-btn">👎
											${c.dislikeCount}</button>
									</form>
								</c:when>

								<c:otherwise>
									<button type="button" class="small-btn"
										onclick="alert('로그인 후 이용 가능합니다.');">👍 ${c.likeCount}</button>
									<button type="button" class="small-btn"
										onclick="alert('로그인 후 이용 가능합니다.');">👎
										${c.dislikeCount}</button>
								</c:otherwise>
							</c:choose>

							<!-- 수정 / 삭제 : 본인 또는 관리자만 -->
							<c:if
								test="${sessionScope.loginMember != null 
                                  && (sessionScope.loginMember.role eq 'ADMIN' 
                                  || sessionScope.loginMember.memberId eq c.memberId)}">

								<button type="button" class="small-btn"
									onclick="toggleEdit(${c.commentId})">수정</button>

								<form action="${pageContext.request.contextPath}/board/detail"
									method="post" class="comment-inline-form">
									<input type="hidden" name="action" value="delete" /> <input
										type="hidden" name="commentId" value="${c.commentId}" /> <input
										type="hidden" name="postId" value="${post.postId}" /> <input
										type="hidden" name="category" value="${category}" /> <input
										type="hidden" name="page" value="${page}" /> <input
										type="hidden" name="commentPage" value="${commentPage}" />
									<button type="submit" class="small-btn danger-btn"
										onclick="return confirm('댓글을 삭제하시겠습니까?');">삭제</button>
								</form>
							</c:if>

							<!-- 답글 / 신고 : 회원만 -->
							<c:if test="${sessionScope.loginMember != null}">
								<button type="button" class="small-btn"
									onclick="toggleReply(${c.commentId})">답글</button>
								<button type="button" class="small-btn"
									onclick="toggleReport(${c.commentId})">신고</button>
							</c:if>
						</div>

						<!-- 수정 폼 : 본인 또는 관리자만 -->
						<c:if
							test="${sessionScope.loginMember != null 
                              && (sessionScope.loginMember.role eq 'ADMIN' 
                              || sessionScope.loginMember.memberId eq c.memberId)}">
							<div id="editForm-${c.commentId}" class="comment-form-box"
								style="display: none;">
								<form action="${pageContext.request.contextPath}/board/detail"
									method="post">
									<input type="hidden" name="action" value="update" /> <input
										type="hidden" name="commentId" value="${c.commentId}" /> <input
										type="hidden" name="postId" value="${post.postId}" /> <input
										type="hidden" name="category" value="${category}" /> <input
										type="hidden" name="page" value="${page}" /> <input
										type="hidden" name="commentPage" value="${commentPage}" />

									<textarea name="content" class="comment-textarea"
										maxlength="50"><c:out value="${c.content}" /></textarea>

									<div class="comment-submit-wrap">
										<button type="submit" class="small-btn primary-btn">수정
											완료</button>
									</div>
								</form>
							</div>
						</c:if>

						<!-- 답글 폼 : 회원만 -->
						<c:if test="${sessionScope.loginMember != null}">
							<div id="replyForm-${c.commentId}" class="comment-form-box"
								style="display: none;">
								<form action="${pageContext.request.contextPath}/board/detail"
									method="post">
									<input type="hidden" name="action" value="insert" /> <input
										type="hidden" name="postId" value="${post.postId}" /> <input
										type="hidden" name="category" value="${category}" /> <input
										type="hidden" name="page" value="${page}" /> <input
										type="hidden" name="commentPage" value="${commentPage}" /> <input
										type="hidden" name="parentCommentId" value="${c.commentId}" />

									<textarea name="content" class="comment-textarea"
										maxlength="50" placeholder="답글을 입력하세요 (최대 50자)"></textarea>

									<div class="comment-submit-wrap">
										<button type="submit" class="small-btn primary-btn">답글
											작성</button>
									</div>
								</form>
							</div>
						</c:if>

						<!-- 신고 폼 : 회원만 -->
						<c:if test="${sessionScope.loginMember != null}">
							<div id="reportForm-${c.commentId}" class="comment-form-box"
								style="display: none;">
								<form action="${pageContext.request.contextPath}/board/detail"
									method="post">
									<input type="hidden" name="action" value="report" /> <input
										type="hidden" name="commentId" value="${c.commentId}" /> <input
										type="hidden" name="postId" value="${post.postId}" /> <input
										type="hidden" name="category" value="${category}" /> <input
										type="hidden" name="page" value="${page}" /> <input
										type="hidden" name="commentPage" value="${commentPage}" />

									<textarea name="reason" class="comment-textarea"
										maxlength="100" placeholder="신고 사유를 입력하세요"></textarea>

									<div class="comment-submit-wrap">
										<button type="submit" class="small-btn danger-btn">신고
											접수</button>
									</div>
								</form>
							</div>
						</c:if>

					</div>
				</c:forEach>

				<!-- 댓글 페이지 번호 -->
				<c:if test="${commentTotalPages > 1}">
					<div class="comment-pagination">
						<c:forEach var="i" begin="1" end="${commentTotalPages}">
							<c:choose>
								<c:when test="${i == commentPage}">
									<span class="current">${i}</span>
								</c:when>
								<c:otherwise>
									<a
										href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${page}&commentPage=${i}">
										${i} </a>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</div>
				</c:if>

				<!-- 일반 댓글 작성 폼 -->
				<c:if test="${sessionScope.loginMember != null}">
					<div class="comment-write-box">
						<form action="${pageContext.request.contextPath}/board/detail"
							method="post">
							<input type="hidden" name="action" value="insert" /> <input
								type="hidden" name="postId" value="${post.postId}" /> <input
								type="hidden" name="category" value="${category}" /> <input
								type="hidden" name="page" value="${page}" /> <input
								type="hidden" name="commentPage" value="${commentPage}" />

							<textarea name="content" class="comment-textarea" maxlength="50"
								placeholder="댓글을 입력하세요 (최대 50자)"></textarea>

							<div class="comment-submit-wrap">
								<button type="submit" class="action-btn primary-btn">댓글
									달기</button>
							</div>
						</form>
					</div>
				</c:if>
			</div>
		</div>
	</c:if>

<script>
function toggleReply(id){
    let f = document.getElementById("replyForm-" + id);
    f.style.display = (f.style.display === "none") ? "block" : "none";
}
function toggleReport(id){
    let f = document.getElementById("reportForm-" + id);
    f.style.display = (f.style.display === "none") ? "block" : "none";
}
function toggleEdit(id){
    let f = document.getElementById("editForm-" + id);
    f.style.display = (f.style.display === "none") ? "block" : "none";
}
</script>