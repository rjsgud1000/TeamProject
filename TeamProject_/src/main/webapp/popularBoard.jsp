<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<style>

.board-top-nav {
    margin: 18px 0 24px;
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
.section-icon {
    font-size: 22px;
    vertical-align: middle;
    margin-right: 6px;
    color: #1f4fa3;
}

.popular-page {
    max-width: 1000px;
    margin: 40px auto;
    padding: 0 20px;
    color: #222;
}

.popular-title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #111;
}

.popular-desc {
    font-size: 14px;
    color: #666;
    margin-bottom: 18px;
}

.popular-section {
    background: #fff;
    border-radius: 14px;
    padding: 22px 24px;
    margin-bottom: 28px;
    box-shadow: 0 3px 12px rgba(0, 0, 0, 0.08);
}

.popular-section-title {
    font-size: 22px;
    font-weight: bold;
    color: #1f4fa3;
    margin-bottom: 18px;
}

.rank-item {
    display: flex;
    align-items: center;
    gap: 18px;
    padding: 14px 0;
    border-bottom: 1px solid #eee;
}

.rank-item:last-child {
    border-bottom: none;
}

.rank-number {
    width: 52px;
    min-width: 52px;
    height: 52px;
    line-height: 52px;
    text-align: center;
    border-radius: 50%;
    background: linear-gradient(135deg, #2b67d1, #4e8cff);
    color: #fff;
    font-size: 18px;
    font-weight: bold;
}

.rank-content {
    flex: 1;
}

.rank-title-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 6px;
}

.rank-title {
    font-size: 17px;
    font-weight: bold;
    color: #222;
    text-decoration: none;
}

.rank-title:hover {
    color: #1f4fa3;
    text-decoration: underline;
}

.meta-row {
    font-size: 13px;
    color: #777;
}

.badge-hot {
    display: inline-block;
    padding: 3px 8px;
    border-radius: 999px;
    background: #ff4d4f;
    color: #fff;
    font-size: 11px;
    font-weight: bold;
}

.badge-empty {
    color: #888;
    font-size: 14px;
    padding: 10px 0;
}

.top1 .rank-number {
    background: linear-gradient(135deg, #f5a623, #ffcc4d);
    color: #fff;
}

.top2 .rank-number {
    background: linear-gradient(135deg, #8f9fb3, #c0cad8);
    color: #fff;
}

.top3 .rank-number {
    background: linear-gradient(135deg, #b87333, #d79a5b);
    color: #fff;
}
</style>

<div class="popular-page">
    <div class="popular-title">인기 게시글</div>
    <div class="popular-desc">추천수, 조회수, 댓글수를 기준으로 인기 게시글을 확인할 수 있습니다.</div>

	<div class="board-top-nav">
        <div class="board-category-tabs">
            <a class="tab"
               href="${pageContext.request.contextPath}/board/list?category=0&sort=latest">
               공지사항
            </a>

            <a class="tab"
               href="${pageContext.request.contextPath}/board/list?category=1&sort=latest">
               자유게시판
            </a>

            <a class="tab"
               href="${pageContext.request.contextPath}/board/list?category=2&sort=latest">
               질문게시판
            </a>

            <a class="tab"
               href="${pageContext.request.contextPath}/board/list?category=3&sort=latest">
               파티게시판
            </a>

            <a class="tab active"
               href="${pageContext.request.contextPath}/popularBoard">
               인기글
            </a>
        </div>
    </div>
    <!-- 추천 TOP 5 -->
	<div class="popular-section">
		<div class="popular-section-title">
			<span class="material-icons section-icon">thumb_up</span>추천 TOP 5
		</div>

		<c:choose>
			<c:when test="${empty likeTopList}">
				<div class="badge-empty">표시할 게시글이 없습니다.</div>
			</c:when>
			<c:otherwise>
				<c:forEach var="post" items="${likeTopList}" varStatus="status">
					<div
						class="rank-item ${status.index == 0 ? 'top1' : status.index == 1 ? 'top2' : status.index == 2 ? 'top3' : ''}">
						<div class="rank-number">${status.index + 1}</div>

						<div class="rank-content">
							<div class="rank-title-row">
								<a class="rank-title"
									href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}">
									${post.title} </a>

								<c:if test="${post.likeCount >= 3 || post.viewcount >= 50}">
									<span class="badge-hot">HOT</span>
								</c:if>
							</div>

							<div class="meta-row">
								작성자 ${post.nickname} &nbsp;|&nbsp; 조회수 ${post.viewcount}
								&nbsp;|&nbsp; 추천 ${post.likeCount} &nbsp;|&nbsp; 댓글
								${post.commentCount} &nbsp;|&nbsp;
								<fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd" />
							</div>
						</div>
					</div>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>

	<!-- 조회수 TOP 5 -->
    <div class="popular-section">
    	<div class="popular-section-title">
        	<span class="material-icons section-icon">visibility</span> 조회수 TOP 5
        </div>

        <c:choose>
            <c:when test="${empty viewTopList}">
                <div class="badge-empty">표시할 게시글이 없습니다.</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="post" items="${viewTopList}" varStatus="status">
                    <div class="rank-item ${status.index == 0 ? 'top1' : status.index == 1 ? 'top2' : status.index == 2 ? 'top3' : ''}">
                        <div class="rank-number">${status.index + 1}</div>

                        <div class="rank-content">
                            <div class="rank-title-row">
                                <a class="rank-title"
                                   href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}">
                                    ${post.title}
                                </a>

                                <c:if test="${post.likeCount >= 3 || post.viewcount >= 50}">
                                    <span class="badge-hot">HOT</span>
                                </c:if>
                            </div>

                            <div class="meta-row">
                                작성자 ${post.nickname}
                                &nbsp;|&nbsp; 조회수 ${post.viewcount}
                                &nbsp;|&nbsp; 추천 ${post.likeCount}
                                &nbsp;|&nbsp; 댓글 ${post.commentCount}
                                &nbsp;|&nbsp;
                                <fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd" />
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- 댓글 TOP 5 -->
    <div class="popular-section">
    	<div class="popular-section-title">
        	<span class="material-icons section-icon">chat_bubble</span> 댓글 TOP 5
        </div>

        <c:choose>
            <c:when test="${empty commentTopList}">
                <div class="badge-empty">표시할 게시글이 없습니다.</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="post" items="${commentTopList}" varStatus="status">
                    <div class="rank-item ${status.index == 0 ? 'top1' : status.index == 1 ? 'top2' : status.index == 2 ? 'top3' : ''}">
                        <div class="rank-number">${status.index + 1}</div>

                        <div class="rank-content">
                            <div class="rank-title-row">
                                <a class="rank-title"
                                   href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}">
                                    ${post.title}
                                </a>

                                <c:if test="${post.likeCount >= 3 || post.viewcount >= 50}">
                                    <span class="badge-hot">HOT</span>
                                </c:if>
                            </div>

                            <div class="meta-row">
                                작성자 ${post.nickname}
                                &nbsp;|&nbsp; 조회수 ${post.viewcount}
                                &nbsp;|&nbsp; 추천 ${post.likeCount}
                                &nbsp;|&nbsp; 댓글 ${post.commentCount}
                                &nbsp;|&nbsp;
                                <fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd" />
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>