<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<link rel="stylesheet" href="${contextPath}/css/member-pages.css" />

<div class="my-activity-wrap">
  <div class="my-activity-card">
    <div class="my-activity-hero my-activity-hero--comments">
      <h2>내가 쓴 댓글 전체보기</h2>
      <p>내가 남긴 댓글과 답글을 전체 목록으로 확인할 수 있어요.</p>
    </div>

    <div class="my-activity-body">
       <div class="my-activity-top">
        <div class="my-activity-count my-activity-count--comments">${empty myCommentCount ? 0 : myCommentCount}</div>
        <div class="my-activity-actions">
          <a class="my-btn" href="${contextPath}/member/mypage.me">마이페이지</a>
          <a class="my-btn primary my-btn--comments" href="${contextPath}/board/list?category=all">게시판 가기</a>
        </div>
      </div>

      <c:choose>
        <c:when test="${empty myCommentList}">
          <div class="my-empty">작성한 댓글이 아직 없습니다.</div>
        </c:when>
        <c:otherwise>
          <div class="my-comment-list">
            <c:forEach var="comment" items="${myCommentList}">
              <a class="my-comment-item" href="${contextPath}/board/detail?postId=${comment.postId}&page=1">
                <div class="my-comment-meta">
                  <span class="my-chip my-chip--comments">
                    <c:choose>
                      <c:when test="${comment.parentCommentId ne null}">답글</c:when>
                      <c:otherwise>댓글</c:otherwise>
                    </c:choose>
                  </span>
                  <span>댓글 #${comment.commentId}</span>
                  <span>게시글 #${comment.postId}</span>
                  <span><fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                </div>
                <div class="my-comment-title">게시글 상세에서 보기</div>
                <div class="my-comment-content"><c:out value="${comment.content}" /></div>
              </a>
            </c:forEach>
          </div>
          <c:if test="${totalPage > 1}">
            <div class="my-pagination">
              <c:if test="${startPage > 1}">
                <a class="login-history-page-link my-comment-page-link" href="${contextPath}/mypage/comments?page=${startPage - 1}">이전</a>
              </c:if>
              <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
                <a class="login-history-page-link my-comment-page-link ${pageNo == currentPage ? 'is-active' : ''}" href="${contextPath}/mypage/comments?page=${pageNo}">${pageNo}</a>
              </c:forEach>
              <c:if test="${endPage < totalPage}">
                <a class="login-history-page-link my-comment-page-link" href="${contextPath}/mypage/comments?page=${endPage + 1}">다음</a>
              </c:if>
            </div>
          </c:if>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>