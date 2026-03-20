<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="popular-page">
    <div class="popular-title">인기 게시글</div>
    <div class="popular-desc">추천수, 조회수, 댓글수를 기준으로 인기 게시글을 확인할 수 있습니다.</div>
    
    <div class="period-tabs">
    <a class="period-tab ${period == 'day' ? 'active' : ''}"
       href="${pageContext.request.contextPath}/board/popular?period=day">일간</a>

    <a class="period-tab ${period == 'week' ? 'active' : ''}"
       href="${pageContext.request.contextPath}/board/popular?period=week">주간</a>

    <a class="period-tab ${period == 'month' ? 'active' : ''}"
       href="${pageContext.request.contextPath}/board/popular?period=month">월간</a>
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