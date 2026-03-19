<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="now" class="java.util.Date" />

<style>
.board-container {
    max-width: 1100px;
    margin: 0 auto;
    padding: 28px 20px 50px;
}


.board-panel {
    background: transparent;
    border: none;
    border-radius: 0;
    padding: 28px 0 24px;
    box-shadow: none;
}

.board-page-title {
    margin-bottom: 18px;
    font-size: 34px;
    font-weight: 800;
    color: #ffffff;
    letter-spacing: -0.4px;
}

.board-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 18px 0 18px 0;
    flex-wrap: wrap;
    gap: 12px;
}

.board-sort-wrap {
    display: flex;
    align-items: center;
    gap: 10px;
}

.board-sort-label {
    font-weight: 700;
    color: #f3f7ff;
    font-size: 15px;
}

.board-select {
    height: 38px;
    padding: 0 12px;
    border-radius: 10px;
}

.board-write-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 38px;
    padding: 0 16px;
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: white;
    text-decoration: none;
    border-radius: 10px;
    font-weight: 600;
    font-size: 14px;
    box-shadow: 0 4px 10px rgba(37, 99, 235, 0.25);
    transition: all 0.15s ease;
}

.board-write-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 14px rgba(37, 99, 235, 0.35);
}

.board-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
    background: #f8fafc;
    border-top: 2px solid #8fb3ff;
    border-radius: 14px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.10);
}

.board-table thead tr {
    border-bottom: 1px solid #d9e0ea;
    background: #eef4ff;
}

.board-table th {
    padding: 14px 10px;
    font-size: 15px;
    font-weight: 700;
    color: #223;
    text-align: center;
}

.board-table th.title-col {
    text-align: left;
}

.board-table tbody tr {
    border-bottom: 1px solid #edf1f5;
    transition: background 0.15s ease;
}

.board-table tbody tr:hover {
    background: #f3f7ff;
}

.board-table td {
    padding: 14px 10px;
    text-align: center;
    font-size: 14px;
    color: #333;
    vertical-align: middle;
}

.board-table td.title-cell {
    text-align: left;
}

.board-title-link {
    text-decoration: none;
    color: #222;
    font-weight: 500;
    transition: color 0.15s ease;
}

.board-title-link:hover {
    color: #2d6cdf;
    text-decoration: underline;
}

.comment-count {
    color: #2563eb;
    font-weight: 700;
    margin-left: 4px;
}

.badge-hot,
.badge-new {
    display: inline-block;
    margin-left: 6px;
    padding: 2px 6px;
    font-size: 11px;
    font-weight: 700;
    color: white;
    border-radius: 10px;
    vertical-align: middle;
}

.badge-hot {
    background: #ef4444;
}

.badge-new {
    background: #22c55e;
}

.empty-row {
    padding: 24px !important;
    text-align: center !important;
    color: #666;
}

.board-search-wrap {
    width: 100%;
    display: flex;
    justify-content: center;
    margin: 14px 0 8px 0;
}

.board-search-form {
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
}

.board-search-select,
.board-search-input {
    border: 1px solid #c9d7f0;
    border-radius: 8px;
    box-sizing: border-box;
    font-size: 14px;
    background: rgba(255,255,255,0.95);
}

.board-search-select {
    padding: 8px 10px;
    height: 40px;
}

.board-search-input {
    padding: 8px 12px;
    width: 240px;
    height: 40px;
}

.board-search-btn,
.board-reset-btn {
    height: 40px;
    padding: 0 14px;
    border-radius: 8px;
    font-weight: 600;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.board-search-btn {
    border: none;
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: white;
    cursor: pointer;
}

.board-search-btn:hover {
    background: linear-gradient(135deg, #2563eb, #1d4ed8);
}

.board-reset-btn {
    color: #444;
    border: 1px solid #dcdfe6;
    background: #fff;
}

.board-pagination {
    text-align: center;
    margin-top: 10px;
}

.board-pagination a,
.board-pagination strong {
    display: inline-block;
    min-width: 34px;
    padding: 7px 9px;
    border-radius: 8px;
    font-size: 14px;
}

.board-pagination {
    text-align: center;
    margin-top: 12px;
}

.board-pagination a,
.board-pagination strong {
    display: inline-block;
    min-width: 34px;
    padding: 7px 9px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    text-decoration: none;
    margin: 0 4px;
}

.board-pagination a {
    color: #334155;          /* 글자색 진하게 */
    background: transparent;
}

.board-pagination a:hover {
    background: #eef4ff;
    color: #2563eb;
}

.board-pagination strong {
    color: #fff;
    background: #2563eb;
}

.board-pagination a:hover {
    background: rgba(255,255,255,0.18);
    color: #fff;
}

.board-pagination strong {
    color: #fff;
    background: linear-gradient(135deg, #3b82f6, #2563eb);
}

@media (max-width: 768px) {
    .board-container {
        padding: 20px 12px 40px;
    }

    .board-panel {
        padding: 20px 16px;
    }

    .board-page-title {
        font-size: 28px;
    }

    .board-toolbar {
        align-items: stretch;
    }

    .board-search-input {
        width: 100%;
        max-width: 280px;
    }
}

		.qna-answer-row {
		    background: #f7faff;
		}
		
		.qna-answer-row:hover {
		    background: #eef5ff !important;
		}
		
		.qna-answer-title {
		    padding-left: 0;
		    position: static;
		}
		
		.qna-answer-badge {
		    display: inline-block;
		    margin-right: 8px;
		    padding: 2px 8px;
		    font-size: 11px;
		    font-weight: 700;
		    color: #fff;
		    border-radius: 999px;
		    background: #2563eb;
		    vertical-align: middle;
		    line-height: 1.4;
		}
		
		.qna-answer-title {
   		 padding-left: 10px;
		}
</style>

<div class="board-container">
    <div class="board-panel">

        <div class="board-page-title">${boardTitle}</div>

        <!-- 정렬 + 글쓰기 버튼 -->
        <div class="board-toolbar">
            <div class="board-sort-wrap">
                <label class="board-sort-label">정렬</label>
                <select onchange="changeSort(this.value)" class="board-select">
                    <option value="latest" ${empty sort or sort eq 'latest' ? 'selected' : ''}>최신순</option>
                    <option value="view" ${sort eq 'view' ? 'selected' : ''}>조회순</option>

                    <c:if test="${category ne '0'}">
                        <option value="like" ${sort eq 'like' ? 'selected' : ''}>추천순</option>
                    </c:if>
                </select>
            </div>

            <div>
                <c:choose>
                    <c:when test="${category eq '0'}">
                        <c:if test="${sessionScope.loginMember != null && sessionScope.loginMember.role eq 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/board/write?category=${category}" class="board-write-btn">글쓰기</a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/board/write?category=${category}" class="board-write-btn">글쓰기</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <table class="board-table">
            <thead>
                <tr>
                    <th style="width: 8%;">번호</th>
                    <th style="width: 10%;">카테고리</th>
                    <th class="title-col" style="width: 36%;">제목</th>
                    <th style="width: 12%;">작성자</th>
                    <th style="width: 10%;">조회수</th>
                    <th style="width: 10%;">추천수</th>
                    <th style="width: 14%;">작성일</th>
                </tr>
            </thead>
            <tbody>
			<c:forEach var="post" items="${boardList}">
			    <tr class="${post.answerPost ? 'qna-answer-row' : ''}">
			        <td>${post.postId}</td>
			
			        <td>
			            <c:choose>
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
			            </c:choose>
			        </td>
			
					<td class="title-cell ${post.answerPost ? 'qna-answer-title' : ''}">
					    <c:set var="isHot" value="${post.likeCount >= 5 || post.viewcount >= 100}" />
					    <c:set var="isNew" value="${(now.time - post.createAt.time) < 86400000}" />
					
					    <c:if test="${post.answerPost}">
					        <span class="qna-answer-badge">답변</span>
					    </c:if>
					
					    <a href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${currentPage}" class="board-title-link">
					        ${post.title}
					    </a>
					
					    <c:if test="${post.category != 0 && post.commentCount > 0}">
					        <span class="comment-count">[${post.commentCount}]</span>
					    </c:if>
					
					    <c:if test="${isHot}">
					        <span class="badge-hot">HOT</span>
					    </c:if>
					
					    <c:if test="${isNew}">
					        <span class="badge-new">NEW</span>
					    </c:if>
					</td>
			
			        <td>${post.nickname}</td>
			        <td>${post.viewcount}</td>
			        <td>${post.likeCount}</td>
			        <td><fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd" /></td>
			    </tr>
			</c:forEach>

                <c:if test="${empty boardList}">
                    <tr>
                        <td colspan="7" class="empty-row">게시글이 없습니다.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <!-- 검색 -->
        <div class="board-search-wrap">
            <form action="${pageContext.request.contextPath}/board/list" method="get" class="board-search-form">
                <input type="hidden" name="category" value="${category}">
                <input type="hidden" name="sort" value="${sort}">

                <select name="searchType" class="board-search-select">
                    <option value="writer" ${searchType eq 'writer' ? 'selected' : ''}>작성자</option>
                    <option value="title" ${searchType eq 'title' ? 'selected' : ''}>제목</option>
                    <option value="content" ${searchType eq 'content' ? 'selected' : ''}>내용</option>
                </select>

                <input type="text" name="keyword" value="${keyword}" placeholder="검색어 입력" class="board-search-input">

                <button type="submit" class="board-search-btn">검색</button>

                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/board/list?category=${category}&sort=${sort}" class="board-reset-btn">초기화</a>
                </c:if>
            </form>
        </div>

        <!-- 페이징 -->
        <div class="board-pagination">
            <c:if test="${startPage > 1}">
                <a href="${pageContext.request.contextPath}/board/list?category=${category}&page=${startPage - 1}&sort=${sort}&searchType=${searchType}&keyword=${keyword}">이전</a>
            </c:if>

            <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <strong>${i}</strong>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/board/list?category=${category}&page=${i}&sort=${sort}&searchType=${searchType}&keyword=${keyword}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${endPage < totalPage}">
                <a href="${pageContext.request.contextPath}/board/list?category=${category}&page=${endPage + 1}&sort=${sort}&searchType=${searchType}&keyword=${keyword}">다음</a>
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