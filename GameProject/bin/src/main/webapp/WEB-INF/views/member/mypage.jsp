<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/member/mypage.jsp

  [역할]
  - 로그인한 사용자의 요약 정보 + 내 글/내 댓글 접근 링크 제공

  [필요한 request attribute]
  - me(Member): MemberController.myPage에서 설정

  [사용하는 session attribute]
  - id: 로그인 아이디

  [유지보수 포인트]
  - 링크 대상 변경 시 MemberController 라우팅과 같이 수정
--%>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  Member me = (Member)request.getAttribute("me");
  String loginId = (String)session.getAttribute("id");
  String ctx = request.getContextPath();
%>

<div class="gc-card">
  <div class="gc-title">마이페이지</div>
  <div class="gc-subtitle">내 정보와 활동 내역을 확인해요</div>

  <div style="display:flex; gap:18px; flex-wrap:wrap;">
    <div style="min-width:260px;">
      <div style="color:var(--gc-muted);">아이디</div>
      <div style="font-weight:900; font-size:2.0rem;"><%=loginId%></div>
    </div>

    <% if(me != null){ %>
    <div style="min-width:260px;">
      <div style="color:var(--gc-muted);">닉네임</div>
      <div style="font-weight:900; font-size:2.0rem;"><%=me.getNickname()%></div>
    </div>
    <div style="min-width:260px;">
      <div style="color:var(--gc-muted);">권한</div>
      <div style="font-weight:900; font-size:2.0rem;"><%=me.getRole()%></div>
    </div>
    <% } %>
  </div>

  <div class="gc-divider"></div>

  <div style="display:flex; flex-wrap:wrap; gap:10px;">
    <a class="gc-btn gc-btn-accent" href="<%=ctx%>/member/profileForm.me">정보수정</a>
    <a class="gc-btn" href="<%=ctx%>/member/myPosts.me?page=1">내가 쓴 글</a>
    <a class="gc-btn" href="<%=ctx%>/member/myComments.me?page=1">내가 쓴 댓글</a>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />