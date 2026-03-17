<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<style>
.board-top-nav {
    margin: 18px 0 20px;
}

.board-category-tabs,
.board-sort-tabs {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    margin-bottom: 12px;
}

.board-category-tabs .tab,
.board-sort-tabs .sort-tab {
    display: inline-block;
    padding: 10px 16px;
    border-radius: 999px;
    text-decoration: none;
    font-size: 14px;
    font-weight: 600;
    transition: all 0.2s ease;
}

.board-category-tabs .tab {
    background: #f3f4f6;
    color: #333;
}

.board-category-tabs .tab:hover {
    background: #e5e7eb;
}

.board-category-tabs .tab.active {
    background: #1f4fa3;
    color: #fff;
}

.board-sort-tabs .sort-tab {
    background: #fff;
    border: 1px solid #dcdfe6;
    color: #555;
}

.board-sort-tabs .sort-tab:hover {
    border-color: #1f4fa3;
    color: #1f4fa3;
}

.board-sort-tabs .sort-tab.active {
    border-color: #1f4fa3;
    background: #eef4ff;
    color: #1f4fa3;
}
</style>

<div style="max-width:1000px; margin:0 auto; padding:20px;">

    <h2>${boardTitle}</h2>

	<div class="board-top-nav">
	    <div class="board-category-tabs">
	        <c:choose>
	            <c:when test="${category eq 'all'}">
	                <a class="tab active"
	                   href="${pageContext.request.contextPath}/board/list?category=all&sort=${empty sort ? 'latest' : sort}">
	                   전체
	                </a>
	            </c:when>
	            <c:otherwise>
	                <a class="tab"
	                   href="${pageContext.request.contextPath}/board/list?category=all&sort=${empty sort ? 'latest' : sort}">
	                   전체
	                </a>
	            </c:otherwise>
	        </c:choose>
	
	        <c:choose>
	            <c:when test="${category eq '0'}">
	                <a class="tab active"
	                   href="${pageContext.request.contextPath}/board/list?category=0&sort=${empty sort ? 'latest' : sort}">
	                   공지사항
	                </a>
	            </c:when>
	            <c:otherwise>
	                <a class="tab"
	                   href="${pageContext.request.contextPath}/board/list?category=0&sort=${empty sort ? 'latest' : sort}">
	                   공지사항
	                </a>
	            </c:otherwise>
	        </c:choose>
	
	        <c:choose>
	            <c:when test="${category eq '1'}">
	                <a class="tab active"
	                   href="${pageContext.request.contextPath}/board/list?category=1&sort=${empty sort ? 'latest' : sort}">
	                   자유게시판
	                </a>
	            </c:when>
	            <c:otherwise>
	                <a class="tab"
	                   href="${pageContext.request.contextPath}/board/list?category=1&sort=${empty sort ? 'latest' : sort}">
	                   자유게시판
	                </a>
	            </c:otherwise>
	        </c:choose>
	
	        <c:choose>
	            <c:when test="${category eq '2'}">
	                <a class="tab active"
	                   href="${pageContext.request.contextPath}/board/list?category=2&sort=${empty sort ? 'latest' : sort}">
	                   질문게시판
	                </a>
	            </c:when>
	            <c:otherwise>
	                <a class="tab"
	                   href="${pageContext.request.contextPath}/board/list?category=2&sort=${empty sort ? 'latest' : sort}">
	                   질문게시판
	                </a>
	            </c:otherwise>
	        </c:choose>
	    </div>
	
	    <div class="board-sort-tabs">
	        <c:choose>
	            <c:when test="${empty sort or sort eq 'latest'}">
	                <a class="sort-tab active"
	                   href="${pageContext.request.contextPath}/board/list?category=${category}&sort=latest&searchType=${searchType}&keyword=${keyword}">
	                   최신순
	                </a>
	            </c:when>
	            <c:otherwise>
	                <a class="sort-tab"
	                   href="${pageContext.request.contextPath}/board/list?category=${category}&sort=latest&searchType=${searchType}&keyword=${keyword}">
	                   최신순
	                </a>
	            </c:otherwise>
	        </c:choose>
	
	        <c:choose>
	            <c:when test="${sort eq 'view'}">
	                <a class="sort-tab active"
	                   href="${pageContext.request.contextPath}/board/list?category=${category}&sort=view&searchType=${searchType}&keyword=${keyword}">
	                   조회순
	                </a>
	            </c:when>
	            <c:otherwise>
	                <a class="sort-tab"
	                   href="${pageContext.request.contextPath}/board/list?category=${category}&sort=view&searchType=${searchType}&keyword=${keyword}">
	                   조회순
	                </a>
	            </c:otherwise>
	        </c:choose>
	
	        <c:if test="${category ne '0'}">
	            <c:choose>
	                <c:when test="${sort eq 'like'}">
	                    <a class="sort-tab active"
	                       href="${pageContext.request.contextPath}/board/list?category=${category}&sort=like&searchType=${searchType}&keyword=${keyword}">
	                       추천순
	                    </a>
	                </c:when>
	                <c:otherwise>
	                    <a class="sort-tab"
	                       href="${pageContext.request.contextPath}/board/list?category=${category}&sort=like&searchType=${searchType}&keyword=${keyword}">
	                       추천순
	                    </a>
	                </c:otherwise>
	            </c:choose>
	        </c:if>
	    </div>
	</div>

	<!-- 게시판 글쓰기 탭(공지사항 관리자권한넣기) -->
	<div style="text-align: right; margin: 10px 0 15px 0;">
		<c:choose>
			<c:when test="${category eq '0'}">
				<c:if
					test="${sessionScope.loginMember != null && sessionScope.loginMember.role eq 'ADMIN'}">
					<a
						href="${pageContext.request.contextPath}/board/write?category=${category}"
						style="display: inline-block; padding: 10px 16px; background: #2d6cdf; color: white; text-decoration: none; border-radius: 8px; font-weight: 600;">
						글쓰기
					</a>
				</c:if>
			</c:when>

			<c:otherwise>
				<a
					href="${pageContext.request.contextPath}/board/write?category=${category}"
					style="display: inline-block; padding: 10px 16px; background: #2d6cdf; color: white; text-decoration: none; border-radius: 8px; font-weight: 600;">
					글쓰기
				</a>
			</c:otherwise>
		</c:choose>
	</div>
	
	<!-- 게시판 검색버튼 -->
	<div style="margin-bottom: 15px;">
		<form action="${pageContext.request.contextPath}/board/list"
			method="get"
			style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap;">

			<input type="hidden" name="category" value="${category}">
			<input type="hidden" name="sort" value="${sort}">
			
			<select name="searchType" style="padding: 5px 8px; border-radius: 4px;">
				<option value="writer" ${searchType eq 'writer' ? 'selected' : ''}>작성자</option>
				<option value="title" ${searchType eq 'title' ? 'selected' : ''}>제목</option>
				<option value="content" ${searchType eq 'content' ? 'selected' : ''}>내용</option>
			</select>
			
			<input type="text" name="keyword" value="${keyword}"
				placeholder="검색어 입력"
				style="padding: 6px 10px; width: 220px; border: 1px solid #ccc; border-radius: 4px;">

			<button type="submit" style="padding: 6px 12px;">검색</button>

			<c:if test="${not empty keyword}">
				<a
					href="${pageContext.request.contextPath}/board/list?category=${category}&sort=${sort}"
					style="text-decoration: none; color: #444;">초기화</a>
			</c:if>
		</form>
	</div>

	<table style="width:100%; border-collapse:collapse; margin-top:15px;">
        <thead>
            <tr style="border-bottom:1px solid #ccc;">
                <th style="padding:10px;">번호</th>
                <th style="padding:10px;">카테고리</th>
                <th style="padding:10px; text-align:left;">제목</th>
                <th style="padding:10px;">작성자</th>
                <th style="padding:10px;">조회수</th>
                <th style="padding:10px;">추천수</th>
                <th style="padding:10px;">작성일</th>
            </tr>
        </thead>
        <tbody>
			<c:forEach var="post" items="${boardList}">
				<tr style="border-bottom: 1px solid #eee;">
					<td style="padding: 10px; text-align: center;">${post.postId}</td>

					<td style="padding: 10px; text-align: center;">
						<c:choose>
							<c:when test="${post.category == 0}">공지</c:when>
							<c:when test="${post.category == 1}">자유</c:when>
							<c:when test="${post.category == 2}">질문</c:when>
							<c:when test="${post.category == 3}">파티</c:when>
							<c:otherwise>기타</c:otherwise>
						</c:choose>
					</td>

					<!-- 새게시글 NEW 추천5이상 이거나 조회수100이상일 경우 HOT -->
					<td style="padding: 10px; text-align: left;">
						<c:set var="isHot" value="${post.likeCount >= 5 || post.viewcount >= 100}" />
						<c:set var="isNew" value="${(now.time - post.createAt.time) < 86400000}" />

						<a href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${currentPage}"
						   style="text-decoration: none; color: inherit;">
						   ${post.title}
						   
							<c:if test="${post.category != 0 && post.commentCount > 0}">
								<span style="color: #2563eb; font-weight: bold; margin-left: 4px;">[${post.commentCount}]</span>
							</c:if>
							
							<c:if test="${isHot}">
								<span style="display: inline-block; margin-left: 6px; padding: 2px 6px; font-size: 11px; font-weight: bold; color: white; background: #ef4444; border-radius: 10px;">
									HOT
								</span>
							</c:if>
							
							<c:if test="${isNew}">
								<span style="display: inline-block; margin-left: 4px; padding: 2px 6px; font-size: 11px; font-weight: bold; color: white; background: #22c55e; border-radius: 10px;">
									NEW
								</span>
							</c:if>
						</a>
					 </td>

					<td style="padding: 10px; text-align: center;">${post.nickname}</td>
					<td style="padding: 10px; text-align: center;">${post.viewcount}</td>
					<td style="padding: 10px; text-align: center;">${post.likeCount}</td>
					<td style="padding: 10px; text-align: center;">
						<fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd" />
					</td>
				</tr>
			</c:forEach>

			<c:if test="${empty boardList}">
                <tr>
                    <td colspan="7" style="padding:20px; text-align:center;">
                        게시글이 없습니다.
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
    
	<div style="text-align: center; margin-top: 20px;">

		<c:if test="${startPage > 1}">
			<a
				href="${pageContext.request.contextPath}/board/list?category=${category}&page=${startPage - 1}&sort=${sort}&searchType=${searchType}&keyword=${keyword}"
				style="margin: 0 5px;">[이전]</a>
		</c:if>

		<c:forEach var="i" begin="${startPage}" end="${endPage}">
			<c:choose>
				<c:when test="${i == currentPage}">
					<strong style="margin: 0 8px; color: blue;">${i}</strong>
				</c:when>
				<c:otherwise>
					<a
						href="${pageContext.request.contextPath}/board/list?category=${category}&page=${i}&sort=${sort}&searchType=${searchType}&keyword=${keyword}"
						style="margin: 0 8px;">${i}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>

		<c:if test="${endPage < totalPage}">
			<a
				href="${pageContext.request.contextPath}/board/list?category=${category}&page=${endPage + 1}&sort=${sort}&searchType=${searchType}&keyword=${keyword}"
				style="margin: 0 5px;">[다음]</a>
		</c:if>

	</div>
</div>