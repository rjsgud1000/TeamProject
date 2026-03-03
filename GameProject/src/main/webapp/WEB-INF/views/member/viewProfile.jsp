<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  Member p = (Member)request.getAttribute("profile");
  String ctx = request.getContextPath();
  String img = (p != null ? p.getProfileImage() : null);
  String defaultSvg = "data:image/svg+xml;utf8," + java.net.URLEncoder.encode(
      "<svg xmlns='http://www.w3.org/2000/svg' width='96' height='96'>" +
      "<rect width='96' height='96' rx='48' fill='%23222'/>" +
      "<circle cx='48' cy='38' r='18' fill='%23555'/>" +
      "<path d='M16 88c6-18 22-28 32-28s26 10 32 28' fill='%23555'/>" +
      "</svg>",
      "UTF-8");
  String imgUrl = (img == null || img.trim().isEmpty()) ? defaultSvg : (ctx + "/uploads/profile/" + img);
%>

<div class="gc-card">
  <div style="display:flex; gap:16px; align-items:center; flex-wrap:wrap;">
    <img src="<%=imgUrl%>" alt="profile" style="width:72px; height:72px; border-radius:50%; object-fit:cover; border:1px solid rgba(255,255,255,0.12);">
    <div>
      <div class="gc-title" style="margin:0;">프로필</div>
      <div style="margin-top:6px; font-size:1.6rem;">
        <div>아이디: <b><%=p.getLoginId()%></b></div>
        <div>닉네임: <b><%=p.getNickname()%></b></div>
      </div>
    </div>
  </div>

  <div class="gc-divider"></div>
  <a class="gc-btn" href="<%=ctx%>/community/list.do?level=5">홈</a>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />