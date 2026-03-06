<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    /* Java 코드: 한글 인코딩 설정 */
    request.setCharacterEncoding("utf-8");
    /* Java 코드: 현재 웹 애플리케이션의 경로 얻기 (예: /CarProject2) */
    String contextPath = request.getContextPath();

    // 로그인 상태 (MemberController에서 session에 loginId/loginName 저장)
    String loginId = (String) session.getAttribute("loginId");
    String loginName = (String) session.getAttribute("loginName");
%>
<header class="topbar">
    <div class="container topbar__inner">
      <a class="brand" href="<%=contextPath %>/main.jsp">
        <img src="<%=contextPath %>/img/logo.png" alt="G-UNIVERSE 로고" style="width:70px; height:34px; border-radius:10px; object-fit:cover; display:block;" />
        <span class="brand__name">G-UNIVERSE</span>
      </a>

      <nav class="nav" aria-label="상단 메뉴">
        <% if (loginId == null) { %>
          <a href="<%=contextPath%>/member/login.me">로그인</a>
          <a href="<%=contextPath%>/member/join.me">회원가입</a>
        <% } else { %>
          <a href="#" style="pointer-events:none; opacity:0.9; font-weight:900;">
            <%= (loginName != null ? loginName : loginId) %>님
          </a>
          <a href="<%=contextPath%>/member/logout.me">로그아웃</a>
          <a href="<%=contextPath%>/member/mypage.me">마이페이지</a>
        <% } %>
      </nav>
    </div>

    <div class="subnav">
      <div class="container subnav__inner" aria-label="보조 메뉴">
        <a href="#">전체보기</a>
        <a href="#">인기 게시글</a>
        <a href="#">자유 게시판</a>
        <a href="#">질문과 답변</a>
        <a href="#">파티원 모집</a>
        <a href="#">공지사항</a>
      </div>
    </div>
  </header>