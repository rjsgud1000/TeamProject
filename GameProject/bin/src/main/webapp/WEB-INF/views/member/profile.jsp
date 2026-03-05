<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/member/profile.jsp

  [역할]
  - 프로필(닉네임/비밀번호) 수정 + 프로필 이미지 업로드/삭제 화면

  [필요한 request attribute]
  - me(Member): MemberController.profileForm에서 설정
  - error(String): 수정 실패 시 메시지(선택)

  [폼 action]
  - 이미지 업로드: POST /member/profileImageUpload.me (multipart, profileImage)
  - 이미지 삭제: POST /member/profileImageDelete.me
  - 정보 수정: POST /member/profileUpdate.me (nickname, newPassword)

  [유지보수 포인트]
  - 업로드 제한(확장자/용량)은 MemberController + ProfileImageUtil과 함께 변경해야 합니다.
--%>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<h2>정보수정</h2>

<%
  Member me = (Member)request.getAttribute("me");
  String error = (String)request.getAttribute("error");
  String ctx = request.getContextPath();
  String img = (me != null ? me.getProfileImage() : null);
  String defaultSvg = "data:image/svg+xml;utf8," + java.net.URLEncoder.encode(
      "<svg xmlns='http://www.w3.org/2000/svg' width='96' height='96'>" +
      "<rect width='96' height='96' rx='48' fill='%23222'/>" +
      "<circle cx='48' cy='38' r='18' fill='%23555'/>" +
      "<path d='M16 88c6-18 22-28 32-28s26 10 32 28' fill='%23555'/>" +
      "</svg>",
      "UTF-8");
  String imgUrl = (img == null || img.trim().isEmpty()) ? defaultSvg : (ctx + "/uploads/profile/" + img);
%>

<% if(error != null){ %>
  <p style="color:red;"><%=error%></p>
<% } %>

<div class="gc-card" style="margin-bottom:14px;">
  <div style="display:flex; gap:14px; align-items:center; flex-wrap:wrap;">
    <img src="<%=imgUrl%>" alt="profile" style="width:72px; height:72px; border-radius:50%; object-fit:cover; border:1px solid rgba(255,255,255,0.12);">
    <div>
      <div style="font-weight:900;">프로필 사진</div>
      <div style="color:var(--gc-muted); font-size:1.2rem;">png/jpg/gif/webp, 최대 5MB</div>
    </div>
  </div>

  <div class="gc-divider"></div>

  <form method="post" action="<%=ctx%>/member/profileImageUpload.me" enctype="multipart/form-data" style="display:flex; gap:10px; flex-wrap:wrap; align-items:center;">
    <input type="file" name="profileImage" accept="image/*" required>
    <button class="gc-btn gc-btn-accent" type="submit">업로드</button>
  </form>

  <form method="post" action="<%=ctx%>/member/profileImageDelete.me" style="margin-top:10px;">
    <button class="gc-btn" type="submit">프로필 사진 삭제</button>
  </form>
</div>

<form method="post" action="<%=ctx%>/member/profileUpdate.me">
  <div>
    <label>아이디</label>
    <input type="text" value="<%= (me != null ? me.getLoginId() : "") %>" readonly>
  </div>
  <div>
    <label>닉네임</label>
    <input type="text" name="nickname" value="<%= (me != null ? me.getNickname() : "") %>" required>
  </div>
  <div>
    <label>새 비밀번호(선택)</label>
    <input type="password" name="newPassword" placeholder="변경 시에만 입력">
  </div>
  <button type="submit">저장</button>
</form>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />