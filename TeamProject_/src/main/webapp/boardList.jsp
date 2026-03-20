<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="now" class="java.util.Date" />

<div class="board-container">
	<div class="board-panel">

		<div class="board-page-title">${boardTitle}</div>

		<div class="board-toolbar">
			<div class="board-sort-wrap">
				<label class="board-sort-label">정렬</label> <select
					onchange="changeSort(this.value)" class="board-select">
					<option value="latest"
						${empty sort or sort eq 'latest' ? 'selected' : ''}>최신순</option>
					<option value="view" ${sort eq 'view' ? 'selected' : ''}>조회순</option>

					<c:if test="${category ne '0'}">
						<option value="like" ${sort eq 'like' ? 'selected' : ''}>추천순</option>
					</c:if>
				</select>
			</div>

			<div>
				<c:choose>
					<c:when test="${category eq '0'}">
						<c:if
							test="${sessionScope.loginMember != null && sessionScope.loginMember.role eq 'ADMIN'}">
							<a
								href="${pageContext.request.contextPath}/board/write?category=${category}"
								class="board-write-btn">글쓰기</a>
						</c:if>
					</c:when>
					<c:otherwise>
						<a
							href="${pageContext.request.contextPath}/board/write?category=${category}"
							class="board-write-btn">글쓰기</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<table class="board-table">
			<thead>
				<tr>
					<th class="col-id">번호</th>
					<th class="col-category">카테고리</th>

					<c:if test="${category eq '3'}">
						<th class="col-status">모집상태</th>
						<th class="col-members">인원</th>
						<th class="title-col col-title-party">제목</th>
					</c:if>

					<c:if test="${category ne '3'}">
						<th class="title-col col-title">제목</th>
					</c:if>

					<th class="col-writer">작성자</th>
					<th class="col-view">조회수</th>
					<th class="col-like">추천수</th>
					<th class="col-date">작성일</th>
				</tr>
			</thead>

			<tbody>
				<c:forEach var="post" items="${boardList}">
					<tr class="${post.answerPost ? 'qna-answer-row' : ''}">
						<td>${post.postId}</td>

						<td><c:choose>
								<c:when test="${post.category == 0}">공지</c:when>
								<c:when test="${post.category == 1}">자유</c:when>
								<c:when test="${post.category == 2}">
									<c:choose>
										<c:when test="${post.answerPost}">답변</c:when>
										<c:otherwise>질문</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${post.category == 3}">파티</c:when>
								<c:otherwise>기타</c:otherwise>
							</c:choose></td>

						<c:if test="${category eq '3'}">
							<td><c:choose>
									<c:when test="${post.recruitStatus == 1}">
										<span class="recruit-open">모집중</span>
									</c:when>
									<c:when test="${post.recruitStatus == 0}">
										<span class="recruit-closed">모집완료</span>
									</c:when>
									<c:otherwise>-</c:otherwise>
								</c:choose></td>
							<td><c:choose>
									<c:when
										test="${post.currentMembers != null && post.maxMembers != null}">
                                        ${post.currentMembers} / ${post.maxMembers}
                                    </c:when>
									<c:otherwise>-</c:otherwise>
								</c:choose></td>
						</c:if>

						<td
							class="title-cell ${post.answerPost ? 'qna-answer-title' : ''}">
							<c:set var="isHot"
								value="${post.likeCount >= 5 || post.viewcount >= 100}" /> <c:set
								var="isNew"
								value="${(now.time - post.createAt.time) < 86400000}" /> <c:if
								test="${post.answerPost}">
								<span class="qna-answer-badge">답변</span>
							</c:if> <a
							href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${currentPage}"
							class="board-title-link"> ${post.title} </a> <c:if
								test="${post.category != 0 && post.commentCount > 0}">
								<span class="comment-count">[${post.commentCount}]</span>
							</c:if> <c:if test="${isHot}">
								<span class="badge-hot">HOT</span>
							</c:if> <c:if test="${isNew}">
								<span class="badge-new">NEW</span>
							</c:if>
						</td>

						<td>${post.nickname}</td>
						<td>${post.viewcount}</td>
						<td>${post.likeCount}</td>
						<td><fmt:formatDate value="${post.createAt}"
								pattern="yyyy-MM-dd" /></td>
					</tr>
				</c:forEach>

				<c:if test="${empty boardList}">
					<tr>
						<td colspan="${category eq '3' ? 9 : 7}" class="empty-row">게시글이
							없습니다.</td>
					</tr>
				</c:if>
			</tbody>
		</table>

		<div class="board-search-wrap">
			<form action="${pageContext.request.contextPath}/board/list"
				method="get" class="board-search-form">
				<input type="hidden" name="category" value="${category}"> <input
					type="hidden" name="sort" value="${sort}"> <select
					name="searchType" class="board-search-select">
					<option value="writer" ${searchType eq 'writer' ? 'selected' : ''}>작성자</option>
					<option value="title" ${searchType eq 'title' ? 'selected' : ''}>제목</option>
					<option value="content"
						${searchType eq 'content' ? 'selected' : ''}>내용</option>
				</select> <input type="text" name="keyword" value="${keyword}"
					placeholder="검색어 입력" class="board-search-input">

				<button type="submit" class="board-search-btn">검색</button>

				<c:if test="${not empty keyword}">
					<a
						href="${pageContext.request.contextPath}/board/list?category=${category}&sort=${sort}"
						class="board-reset-btn">초기화</a>
				</c:if>
			</form>
		</div>

		<div class="board-pagination">
			<c:if test="${startPage > 1}">
				<a
					href="${pageContext.request.contextPath}/board/list?category=${category}&page=${startPage - 1}&sort=${sort}&searchType=${searchType}&keyword=${keyword}">이전</a>
			</c:if>

			<c:forEach var="i" begin="${startPage}" end="${endPage}">
				<c:choose>
					<c:when test="${i == currentPage}">
						<strong>${i}</strong>
					</c:when>
					<c:otherwise>
						<a
							href="${pageContext.request.contextPath}/board/list?category=${category}&page=${i}&sort=${sort}&searchType=${searchType}&keyword=${keyword}">${i}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>

			<c:if test="${endPage < totalPage}">
				<a
					href="${pageContext.request.contextPath}/board/list?category=${category}&page=${endPage + 1}&sort=${sort}&searchType=${searchType}&keyword=${keyword}">다음</a>
			</c:if>
		</div>

	</div>
</div>

<script>
function changeSort(sort) {
    const category = "${category}";
    const searchType = "${searchType}";
    const keyword = "${keyword}";

    location.href = "${pageContext.request.contextPath}/board/list?category="
        + encodeURIComponent(category)
        + "&sort=" + encodeURIComponent(sort)
        + "&searchType=" + encodeURIComponent(searchType)
        + "&keyword=" + encodeURIComponent(keyword);
}
</script>