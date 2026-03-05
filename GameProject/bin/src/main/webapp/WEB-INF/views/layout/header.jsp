<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/layout/header.jsp

  [역할]
  - 모든 페이지에서 공통으로 include 하는 상단 레이아웃(브랜드/네비/로그인 상태 메뉴)을 렌더링합니다.

  [사용하는 session attribute]
  - id: 로그인 아이디
  - role: 권한(ADMIN/USER) -> 관리자 메뉴 노출 여부 결정

  [유지보수 포인트]
  - CSS 경로: /assets/css/site.css
  - Bootstrap CDN 버전 교체 시: 아래 <link>/<script> 변경
  - 메뉴/권한 정책 변경 시: AdminController.ensureAdmin, MemberController.login 세션 세팅과 함께 확인
--%>
<%
	request.setCharacterEncoding("utf-8");
	String contextPath = request.getContextPath();
	String loginId = (String) session.getAttribute("id");
	String role = (String) session.getAttribute("role");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Game Community</title>
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">
<link rel="stylesheet" href="<%=contextPath%>/assets/css/site.css" />

<style>
	/* 커뮤니티 드롭다운: hover로 열리게 */
	.navbar-nav .dropdown:hover > .dropdown-menu { display: block; }
	.navbar-nav .dropdown-menu { margin-top: 0; }
</style>
</head>
<body>
	<div class="gc-container">
		<div class="gc-topbar">
			<div class="d-flex justify-content-between align-items-center flex-wrap" style="gap:12px;">
				<div class="gc-brand">
					<div class="gc-logo"></div>
					<div>
						<a href="<%=contextPath%>/community/list.do?level=5" style="text-decoration:none;">
							<div class="gc-brand-title">Game Community</div>
						</a>
						<div class="gc-brand-sub">자유 · Q&A · 동영상 · 같이할사람</div>
					</div>
				</div>

				<div>
					<% if(loginId == null){ %>
						<a class="gc-btn" href="<%=contextPath%>/member/loginForm.me">로그인</a>
						<a class="gc-btn gc-btn-accent" href="<%=contextPath%>/member/joinForm.me" style="margin-left:8px;">회원가입</a>
					<% } else { %>
						<span style="font-weight:800; margin-right:10px;"><%=loginId%></span>
						<a class="gc-btn" href="<%=contextPath%>/member/myPage.me">마이페이지</a>
						<a class="gc-btn" href="<%=contextPath%>/member/logout.me" style="margin-left:8px;">로그아웃</a>
						<% if("ADMIN".equals(role)) { %>
							<a class="gc-btn" href="<%=contextPath%>/admin/members.do" style="margin-left:8px; border-color: rgba(249,115,22,0.6);">관리자</a>
						<% } %>
					<% } %>
				</div>
			</div>

			<div class="gc-nav">
				<nav class="navbar navbar-expand">
					<ul class="navbar-nav" style="width:100%; justify-content:space-between;">
						<li class="nav-item">
							<a class="nav-link" href="<%=contextPath %>/community/list.do?level=5">홈</a>
						</li>

						<li class="nav-item dropdown">
							<a class="nav-link dropdown-toggle" href="<%=contextPath %>/community/list.do?level=5" id="communityDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								커뮤니티
							</a>
							<div class="dropdown-menu" aria-labelledby="communityDropdown">
								<a class="dropdown-item" href="<%=contextPath %>/community/list.do?level=0">공지</a>
								<a class="dropdown-item" href="<%=contextPath %>/community/list.do?level=1">자유</a>
								<a class="dropdown-item" href="<%=contextPath %>/community/list.do?level=2">Q&A</a>
								<a class="dropdown-item" href="<%=contextPath %>/community/list.do?level=3">동영상</a>
								<a class="dropdown-item" href="<%=contextPath %>/community/list.do?level=4">같이할사람</a>
								<div class="dropdown-divider"></div>
								<a class="dropdown-item" href="<%=contextPath %>/community/list.do?level=5">인기</a>
							</div>
						</li>

						<li class="nav-item">
							<a class="nav-link" href="<%=contextPath %>/member/myPage.me">마이페이지</a>
						</li>
					</ul>
				</nav>
			</div>
		</div>
	</div>

	<div class="gc-container">