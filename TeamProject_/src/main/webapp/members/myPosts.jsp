<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<style>
.my-activity-wrap{max-width:1100px;margin:0 auto;padding:8px 0 16px;}
.my-activity-card{background:#fff;border:1px solid #e5e7eb;border-radius:20px;box-shadow:0 16px 40px rgba(15,23,42,.08);overflow:hidden;}
.my-activity-hero{padding:24px 28px;background:linear-gradient(135deg,#1d4ed8,#2563eb 55%,#0ea5e9);color:#fff;}
.my-activity-hero h2{margin:0;font-size:26px;font-weight:900;}
.my-activity-hero p{margin:8px 0 0;font-size:14px;opacity:.92;}
.my-activity-body{padding:22px 24px 26px;}
.my-activity-top{display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap;margin-bottom:18px;}
.my-activity-count{display:inline-flex;align-items:center;justify-content:center;min-width:42px;height:42px;padding:0 14px;border-radius:999px;background:#eff6ff;color:#1d4ed8;font-weight:900;}
.my-activity-actions{display:flex;gap:10px;flex-wrap:wrap;}
.my-btn{display:inline-flex;align-items:center;justify-content:center;height:40px;padding:0 14px;border-radius:12px;border:1px solid #dbe3f0;background:#fff;color:#0f172a;font-size:13px;font-weight:900;text-decoration:none;}
.my-btn.primary{border:none;background:linear-gradient(135deg,#2563eb,#1d4ed8);color:#fff;}
.my-post-list{display:flex;flex-direction:column;gap:14px;}
.my-post-item{display:block;padding:18px;border:1px solid #e2e8f0;border-radius:16px;background:#f8fafc;text-decoration:none;color:inherit;transition:.2s ease;}
.my-post-item:hover{transform:translateY(-1px);box-shadow:0 10px 20px rgba(15,23,42,.06);border-color:#cbd5e1;}
.my-post-meta{display:flex;flex-wrap:wrap;gap:8px 12px;align-items:center;margin-bottom:10px;font-size:12px;color:#64748b;}
.my-chip{display:inline-flex;align-items:center;padding:5px 10px;border-radius:999px;background:#dbeafe;color:#1e40af;font-weight:900;}
.my-post-title{font-size:18px;font-weight:900;color:#0f172a;line-height:1.4;}
.my-post-content{margin-top:8px;font-size:14px;line-height:1.65;color:#475569;display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden;}
.my-empty{padding:40px 20px;border:1px dashed #cbd5e1;border-radius:16px;background:#f8fafc;color:#64748b;text-align:center;}
@media (max-width: 768px){.my-activity-hero h2{font-size:22px;}.my-post-item{padding:16px;}}
</style>

<div class="my-activity-wrap">
  <div class="my-activity-card">
    <div class="my-activity-hero">
      <h2>내가 쓴 글 전체보기</h2>
      <p>내가 작성한 게시글을 한 번에 확인하고 상세 페이지로 이동할 수 있어요.</p>
    </div>

    <div class="my-activity-body">
       <div class="my-activity-top">
        <div class="my-activity-count">${empty myPostCount ? 0 : myPostCount}</div>
        <div class="my-activity-actions">
          <a class="my-btn" href="${contextPath}/member/mypage.me">마이페이지</a>
          <a class="my-btn primary" href="${contextPath}/board/write">글쓰기</a>
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
                  <span class="my-chip">
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
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
