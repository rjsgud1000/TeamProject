<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<link rel="stylesheet" href="${contextPath}/css/member-pages.css" />

<div class="my-activity-wrap">
  <div class="my-activity-card">
    <div class="my-activity-hero my-activity-hero--posts">
      <h2>내가 쓴 글 전체보기</h2>
      <p>내가 작성한 게시글을 한 번에 확인하고 상세 페이지로 이동할 수 있어요.</p>
    </div>

    <div class="my-activity-body">
       <div class="my-activity-top">
        <div class="my-activity-count my-activity-count--posts">${empty myPostCount ? 0 : myPostCount}</div>
        <div class="my-activity-actions">
          <a class="my-btn" href="${contextPath}/member/mypage.me">마이페이지</a>
          <a class="my-btn primary my-btn--posts" href="${contextPath}/board/write">글쓰기</a>
        </div>
      </div>

      <c:choose>
        <c:when test="${empty myPostList}">
          <div class="my-empty">작성한 게시글이 아직 없습니다.</div>
        </c:when>
        <c:otherwise>
          <div class="my-post-list">
            <c:forEach var="post" items="${myPostList}">
              <a class="my-post-item" href="${contextPath}/board/detail?postId=${post.postId}&category=${post.category}&page=1">
                <div class="my-post-meta">
                  <span class="my-chip my-chip--posts">
                    <c:choose>
                      <c:when test="${post.category == 0}">공지사항</c:when>
                      <c:when test="${post.category == 1}">자유 게시판</c:when>
                      <c:when test="${post.category == 2}">질문과 답변</c:when>
                      <c:when test="${post.category == 3}">파티원 모집</c:when>
                      <c:otherwise>게시판</c:otherwise>
                    </c:choose>
                  </span>
                  <span>게시글 #${post.postId}</span>
                  <span>조회수 ${post.viewcount}</span>
                  <span>좋아요 ${post.likeCount}</span>
                  <span>댓글 ${post.commentCount}</span>
                  <span><fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                </div>
                <div class="my-post-title"><c:out value="${post.title}" /></div>
                <div class="my-post-content"><c:out value="${post.content}" /></div>
              </a>
            </c:forEach>
          </div>
          <c:if test="${totalPage > 1}">
            <div class="my-pagination">
              <c:if test="${startPage > 1}">
                <a class="my-page-link" href="${contextPath}/mypage/posts?page=${startPage - 1}">이전</a>
              </c:if>
              <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
                <a class="my-page-link ${pageNo == currentPage ? 'is-active' : ''}" href="${contextPath}/mypage/posts?page=${pageNo}">${pageNo}</a>
              </c:forEach>
              <c:if test="${endPage < totalPage}">
                <a class="my-page-link" href="${contextPath}/mypage/posts?page=${endPage + 1}">다음</a>
              </c:if>
            </div>
          </c:if>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>