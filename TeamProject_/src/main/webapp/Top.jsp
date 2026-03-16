<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="loginId" value="${sessionScope.loginId}" />
<c:set var="loginName" value="${sessionScope.loginName}" />
<style>
  .topbar__inner{
    flex-wrap:wrap;
    height:auto;
    min-height:76px;
    padding:12px 0;
  }

  .nav{
    margin-left:auto;
    display:flex;
    flex-wrap:wrap;
    justify-content:flex-end;
    row-gap:8px;
  }

  .nav-dropdown{
    position:relative;
    display:inline-flex;
    align-items:center;
  }

  .nav-dropdown__toggle{
    display:inline-flex;
    align-items:center;
    gap:6px;
  }

  .nav-dropdown__toggle::after{
    content:'▾';
    font-size:12px;
    line-height:1;
  }

  .nav-dropdown__menu{
    position:absolute;
    top:100%;
    left:0;
    min-width:170px;
    display:none;
    flex-direction:column;
    padding:8px 0;
    background:#fff;
    border:1px solid rgba(0,0,0,0.08);
    border-radius:10px;
    box-shadow:0 10px 24px rgba(0,0,0,0.12);
    z-index:1000;
  }

  .nav-dropdown:hover .nav-dropdown__menu,
  .nav-dropdown:focus-within .nav-dropdown__menu{
    display:flex;
  }

  .nav-dropdown__menu a{
    white-space:nowrap;
    padding:10px 14px;
    color:#222;
  }

  .nav-dropdown__menu a:hover{
    background:#f5f7fb;
    color:#111;
  }

  @media (max-width: 900px){
    .topbar__inner{
      gap:12px;
    }

    .nav{
      width:100%;
      margin-left:0;
      justify-content:flex-start;
    }
  }

  @media (max-width: 640px){
    .brand{
      width:100%;
    }

    .nav a{
      padding:8px 8px;
      font-size:13px;
    }

    .subnav__inner{
      height:auto;
      padding:8px 0;
      flex-wrap:wrap;
      gap:8px;
    }

    .nav-dropdown__menu{
      left:auto;
      right:0;
    }
  }
</style>
<header class="topbar">
	<div class="container topbar__inner">
		<a class="brand" href="${contextPath}/main.jsp"> <img
			src="${contextPath}/img/logo.png" alt="G-UNIVERSE 로고"
			style="width: 70px; height: 34px; border-radius: 10px; object-fit: cover; display: block;" />
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
					</c:if>
					
					<c:if test="${sessionScope.loginMember != null && sessionScope.loginMember.role eq 'ADMIN'}">
						<div class="nav-dropdown">
							<a href="#" class="nav-dropdown__toggle" onclick="return false;">신고처리</a>
							<div class="nav-dropdown__menu">
								<a href="${contextPath}/admin/postReportList">게시글 신고처리</a>
								<a href="${contextPath}/member/admin/reportList.me">댓글 신고처리</a>
							</div>
						</div>
					</c:if>

					<a href="#"
						style="pointer-events: none; opacity: 0.9; font-weight: 900;">
						${not empty loginName ? loginName : loginId}님 </a>
					<a href="${contextPath}/member/logout.me">로그아웃</a>
					<a href="${contextPath}/member/mypage.me">마이페이지</a>
				</c:otherwise>
			</c:choose>
		</nav>
	</div>

	<!-- 인기게시글 contextPath-> pageContext로변경 -->
	<div class="subnav">
		<div class="container subnav__inner" aria-label="보조 메뉴">
			<a href="${contextPath}/board/list?category=all">전체보기</a> <a
				href="${contextPath}/board/list?category=0">공지사항</a> <a
				href="${pageContext.request.contextPath}/board/popular">인기 게시글</a> <a
				href="${contextPath}/board/list?category=1">자유 게시판</a> <a
				href="${contextPath}/board/list?category=2">질문과 답변</a> <a
				href="${contextPath}/board/list?category=3">파티원 모집</a>
		</div>
	</div>
</header>