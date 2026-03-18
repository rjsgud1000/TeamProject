<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<style>
.login-history-wrap{max-width:1100px;margin:0 auto;padding:8px 0 16px;}
.login-history-card{background:#fff;border:1px solid #e5e7eb;border-radius:20px;box-shadow:0 16px 40px rgba(15,23,42,.08);overflow:hidden;}
.login-history-hero{padding:24px 28px;background:linear-gradient(135deg,#312e81,#4338ca 55%,#6366f1);color:#fff;}
.login-history-hero h2{margin:0;font-size:26px;font-weight:900;}
.login-history-hero p{margin:8px 0 0;font-size:14px;opacity:.92;}
.login-history-body{padding:22px 24px 26px;}
.login-history-top{display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap;margin-bottom:18px;}
.login-history-count{display:inline-flex;align-items:center;justify-content:center;min-width:42px;height:42px;padding:0 14px;border-radius:999px;background:#eef2ff;color:#3730a3;font-weight:900;}
.login-history-actions{display:flex;gap:10px;flex-wrap:wrap;}
.login-history-btn{display:inline-flex;align-items:center;justify-content:center;height:40px;padding:0 14px;border-radius:12px;border:1px solid #dbe3f0;background:#fff;color:#0f172a;font-size:13px;font-weight:900;text-decoration:none;}
.login-history-btn.primary{border:none;background:linear-gradient(135deg,#4338ca,#312e81);color:#fff;}
.login-history-list{display:flex;flex-direction:column;gap:14px;}
.login-history-item{padding:18px;border:1px solid #e2e8f0;border-radius:16px;background:#f8fafc;}
.login-history-meta{display:flex;flex-wrap:wrap;gap:8px 12px;align-items:center;margin-bottom:10px;font-size:12px;color:#64748b;}
.login-history-badge{display:inline-flex;align-items:center;padding:5px 10px;border-radius:999px;font-weight:900;}
.login-history-badge--success{background:#dcfce7;color:#166534;}
.login-history-badge--fail{background:#fee2e2;color:#b91c1c;}
.login-history-badge--blocked{background:#ede9fe;color:#6d28d9;}
.login-history-label{font-size:15px;font-weight:800;color:#0f172a;line-height:1.4;}
.login-history-text{margin-top:6px;font-size:14px;line-height:1.65;color:#475569;word-break:break-word;}
.login-history-empty{padding:40px 20px;border:1px dashed #cbd5e1;border-radius:16px;background:#f8fafc;color:#64748b;text-align:center;}
.login-history-pagination{display:flex;justify-content:center;align-items:center;gap:8px;flex-wrap:wrap;margin-top:22px;}
.login-history-page-link{display:inline-flex;align-items:center;justify-content:center;min-width:40px;height:40px;padding:0 12px;border-radius:12px;border:1px solid #dbe3f0;background:#fff;color:#334155;text-decoration:none;font-size:13px;font-weight:800;}
.login-history-page-link.is-active{background:#4338ca;border-color:#4338ca;color:#fff;}
@media (max-width: 768px){.login-history-hero h2{font-size:22px;}.login-history-item{padding:16px;}}
</style>

<div class="login-history-wrap">
  <div class="login-history-card">
    <div class="login-history-hero">
      <h2>로그인 기록 보기</h2>
      <p>내 계정의 로그인 성공, 실패, 차단 이력을 확인할 수 있어요.</p>
    </div>

    <div class="login-history-body">
      <div class="login-history-top">
        <div class="login-history-count">${empty loginHistoryCount ? 0 : loginHistoryCount}</div>
        <div class="login-history-actions">
          <a class="login-history-btn" href="${contextPath}/member/mypage.me">마이페이지</a>
          <a class="login-history-btn primary" href="${contextPath}/main.jsp">메인으로</a>
        </div>
      </div>

      <c:choose>
        <c:when test="${empty loginHistoryList}">
          <div class="login-history-empty">표시할 로그인 기록이 없습니다.</div>
        </c:when>
        <c:otherwise>
          <div class="login-history-list">
            <c:forEach var="history" items="${loginHistoryList}">
              <div class="login-history-item">
                <div class="login-history-meta">
                  <c:choose>
                    <c:when test="${history.loginResult eq 'SUCCESS'}">
                      <span class="login-history-badge login-history-badge--success"><c:out value="${history.loginResultLabel}" /></span>
                    </c:when>
                    <c:when test="${history.loginResult eq 'BLOCKED'}">
                      <span class="login-history-badge login-history-badge--blocked"><c:out value="${history.loginResultLabel}" /></span>
                    </c:when>
                    <c:otherwise>
                      <span class="login-history-badge login-history-badge--fail"><c:out value="${history.loginResultLabel}" /></span>
                    </c:otherwise>
                  </c:choose>
                  <span>기록 #${history.loginHistoryId}</span>
                  <span>입력 아이디 <c:out value="${history.inputMemberId}" /></span>
                  <span><c:out value="${history.loginAtText}" /></span>
                </div>
                <div class="login-history-label">접속 IP: <c:out value="${empty history.loginIp ? '-' : history.loginIp}" /></div>
                <div class="login-history-text">실패 사유: <c:out value="${empty history.failReasonLabel ? '-' : history.failReasonLabel}" /></div>
                <div class="login-history-text" title="${history.userAgent}">브라우저 정보: <c:out value="${empty history.userAgentSummary ? '-' : history.userAgentSummary}" /></div>
              </div>
            </c:forEach>
          </div>
          <c:if test="${totalPage > 1}">
            <div class="login-history-pagination">
              <c:if test="${startPage > 1}">
                <a class="login-history-page-link" href="${contextPath}/mypage/login-history?page=${startPage - 1}">이전</a>
              </c:if>
              <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
                <a class="login-history-page-link ${pageNo == currentPage ? 'is-active' : ''}" href="${contextPath}/mypage/login-history?page=${pageNo}">${pageNo}</a>
              </c:forEach>
              <c:if test="${endPage < totalPage}">
                <a class="login-history-page-link" href="${contextPath}/mypage/login-history?page=${endPage + 1}">다음</a>
              </c:if>
            </div>
          </c:if>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
