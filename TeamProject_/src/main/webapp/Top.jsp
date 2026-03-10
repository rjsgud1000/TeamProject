<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="loginId" value="${sessionScope.loginId}" />
<c:set var="loginName" value="${sessionScope.loginName}" />
<header class="topbar">
    <div class="container topbar__inner">
      <a class="brand" href="${contextPath}/main.jsp">
        <img src="${contextPath}/img/logo.png" alt="G-UNIVERSE 로고" style="width:70px; height:34px; border-radius:10px; object-fit:cover; display:block;" />
        <span class="brand__name">G-UNIVERSE</span>
      </a>

      <nav class="nav" aria-label="상단 메뉴">
        <c:choose>
          <c:when test="${empty loginId}">
            <a href="${contextPath}/member/login.me">로그인</a>
            <a href="${contextPath}/member/join.me">회원가입</a>
          </c:when>
          <c:otherwise>
            <c:if test="${requestScope.isAdmin}">
              <a href="${contextPath}/member/admin/list.me">회원조회</a>
              <a href="${contextPath}/member/admin/reportList.me">신고처리</a>
            </c:if>
            <a href="#" style="pointer-events:none; opacity:0.9; font-weight:900;">
              ${not empty loginName ? loginName : loginId}님
            </a>
            <a href="${contextPath}/member/logout.me">로그아웃</a>
            <a href="${contextPath}/member/mypage.me">마이페이지</a>
          </c:otherwise>
        </c:choose>
      </nav>
    </div>

<div class="subnav">
  <div class="container subnav__inner" aria-label="보조 메뉴">
    <a href="${contextPath}/board/list?category=all">전체보기</a>
	<a href="${contextPath}/board/list?category=0">공지사항</a>
	<a href="${contextPath}/board/list?sort=popular">인기 게시글</a>
	<a href="${contextPath}/board/list?category=1">자유 게시판</a>
	<a href="${contextPath}/board/list?category=2">질문과 답변</a>
	<a href="${contextPath}/board/list?category=3">파티원 모집</a>
  </div>
</div>
  </header>