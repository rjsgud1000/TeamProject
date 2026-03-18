<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<style>
.my-activity-wrap{max-width:1100px;margin:0 auto;padding:8px 0 16px;}
.my-activity-card{background:#fff;border:1px solid #e5e7eb;border-radius:20px;box-shadow:0 16px 40px rgba(15,23,42,.08);overflow:hidden;}
.my-activity-hero{padding:24px 28px;background:linear-gradient(135deg,#0f766e,#0ea5a4 55%,#22c55e);color:#fff;}
.my-activity-hero h2{margin:0;font-size:26px;font-weight:900;}
.my-activity-hero p{margin:8px 0 0;font-size:14px;opacity:.92;}
.my-activity-body{padding:22px 24px 26px;}
.my-activity-top{display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap;margin-bottom:18px;}
.my-activity-count{display:inline-flex;align-items:center;justify-content:center;min-width:42px;height:42px;padding:0 14px;border-radius:999px;background:#ecfeff;color:#0f766e;font-weight:900;}
.my-activity-actions{display:flex;gap:10px;flex-wrap:wrap;}
.my-btn{display:inline-flex;align-items:center;justify-content:center;height:40px;padding:0 14px;border-radius:12px;border:1px solid #dbe3f0;background:#fff;color:#0f172a;font-size:13px;font-weight:900;text-decoration:none;}
.my-btn.primary{border:none;background:linear-gradient(135deg,#0f766e,#0ea5a4);color:#fff;}
.my-comment-list{display:flex;flex-direction:column;gap:14px;}
.my-comment-item{display:block;padding:18px;border:1px solid #e2e8f0;border-radius:16px;background:#f8fafc;text-decoration:none;color:inherit;transition:.2s ease;}
.my-comment-item:hover{transform:translateY(-1px);box-shadow:0 10px 20px rgba(15,23,42,.06);border-color:#cbd5e1;}
.my-comment-meta{display:flex;flex-wrap:wrap;gap:8px 12px;align-items:center;margin-bottom:10px;font-size:12px;color:#64748b;}
.my-chip{display:inline-flex;align-items:center;padding:5px 10px;border-radius:999px;background:#dcfce7;color:#166534;font-weight:900;}
.my-comment-title{font-size:18px;font-weight:900;color:#0f172a;line-height:1.4;}
.my-comment-content{margin-top:8px;font-size:14px;line-height:1.7;color:#475569;white-space:pre-wrap;word-break:break-word;}
.my-empty{padding:40px 20px;border:1px dashed #cbd5e1;border-radius:16px;background:#f8fafc;color:#64748b;text-align:center;}
.my-pagination{display:flex;justify-content:center;align-items:center;gap:8px;flex-wrap:wrap;margin-top:22px;}
.my-page-link{display:inline-flex;align-items:center;justify-content:center;min-width:40px;height:40px;padding:0 12px;border-radius:12px;border:1px solid #dbe3f0;background:#fff;color:#334155;text-decoration:none;font-size:13px;font-weight:800;}
.my-page-link.is-active{background:#0f766e;border-color:#0f766e;color:#fff;}
@media (max-width: 768px){.my-activity-hero h2{font-size:22px;}.my-comment-item{padding:16px;}}
</style>

<div class="my-activity-wrap">
  <div class="my-activity-card">
    <div class="my-activity-hero">
      <h2>내가 쓴 댓글 전체보기</h2>
      <p>내가 남긴 댓글과 답글을 전체 목록으로 확인할 수 있어요.</p>
    </div>

    <div class="my-activity-body">
       <div class="my-activity-top">
        <div class="my-activity-count">${empty myCommentCount ? 0 : myCommentCount}</div>
        <div class="my-activity-actions">
          <a class="my-btn" href="${contextPath}/member/mypage.me">마이페이지</a>
          <a class="my-btn primary" href="${contextPath}/board/list?category=all">게시판 가기</a>
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
                  <span class="my-chip">
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
                <a class="my-page-link" href="${contextPath}/mypage/comments?page=${startPage - 1}">이전</a>
              </c:if>
              <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
                <a class="my-page-link ${pageNo == currentPage ? 'is-active' : ''}" href="${contextPath}/mypage/comments?page=${pageNo}">${pageNo}</a>
              </c:forEach>
              <c:if test="${endPage < totalPage}">
                <a class="my-page-link" href="${contextPath}/mypage/comments?page=${endPage + 1}">다음</a>
              </c:if>
            </div>
          </c:if>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>