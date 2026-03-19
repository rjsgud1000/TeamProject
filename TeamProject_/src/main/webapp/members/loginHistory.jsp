<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<link rel="stylesheet" href="${contextPath}/css/member-pages.css" />

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
