<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="vo.Post" %>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  int level = (Integer)request.getAttribute("level");
  int pageNum = (Integer)request.getAttribute("page");
  List<Post> posts = (List<Post>)request.getAttribute("posts");
  String q = (String)request.getAttribute("q");
  String ctx = request.getContextPath();
  String role = (String)session.getAttribute("role");
  String qParam = (q != null && !q.trim().isEmpty()) ? "&q=" + java.net.URLEncoder.encode(q, "UTF-8") : "";
  Map<String, Member> authorMap = (Map<String, Member>) request.getAttribute("authorMap");
  String defaultSvg = "data:image/svg+xml;utf8," + java.net.URLEncoder.encode(
      "<svg xmlns='http://www.w3.org/2000/svg' width='96' height='96'>" +
      "<rect width='96' height='96' rx='48' fill='%23222'/>" +
      "<circle cx='48' cy='38' r='18' fill='%23555'/>" +
      "<path d='M16 88c6-18 22-28 32-28s26 10 32 28' fill='%23555'/>" +
      "</svg>",
      "UTF-8");
%>

<div class="gc-card">
  <div style="display:flex; justify-content:space-between; align-items:flex-end; gap:12px; flex-wrap:wrap;">
    <div>
      <div class="gc-title">커뮤니티</div>
      <div class="gc-subtitle">카테고리(level=<%=level%>) · 최신 글을 확인해요</div>
      <% if(level == 0 && !"ADMIN".equals(role)) { %>
        <div class="gc-subtitle" style="margin-top:6px;">공지는 관리자만 작성할 수 있어요.</div>
      <% } %>
    </div>
    <div style="display:flex; gap:8px; flex-wrap:wrap; justify-content:flex-end;">
      <a class="gc-btn" href="<%=ctx%>/community/list.do?level=5">인기</a>
      <% if(level != 0 || "ADMIN".equals(role)) { %>
        <a class="gc-btn gc-btn-accent" href="<%=ctx%>/community/writeForm.do?level=<%=level%>">글쓰기</a>
      <% } %>
    </div>
  </div>

  <%-- 검색 --%>
  <% if(level != 5){ %>
  <div style="margin-top:12px; display:flex; gap:8px; flex-wrap:wrap; align-items:center;">
    <form method="get" action="<%=ctx%>/community/list.do" style="display:flex; gap:8px; flex-wrap:wrap; align-items:center;">
      <input type="hidden" name="level" value="<%=level%>">
      <input class="gc-form-control" type="text" name="q" value="<%= (q == null ? "" : q) %>" placeholder="제목/내용 검색" style="max-width:360px;">
      <button class="gc-btn" type="submit">검색</button>
      <% if(q != null && !q.trim().isEmpty()){ %>
        <a class="gc-btn" href="<%=ctx%>/community/list.do?level=<%=level%>">초기화</a>
      <% } %>
    </form>
  </div>
  <% } else { %>
    <div style="margin-top:10px; color:var(--gc-muted); font-size:1.3rem;">인기게시글은 <b>좋아요 10개 이상</b>인 글만 노출됩니다.</div>
  <% } %>

  <div class="gc-divider"></div>

  <div style="display:flex; flex-wrap:wrap; gap:8px; margin-bottom:12px;">
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=0">공지</a>
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=1">자유</a>
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=2">Q&A</a>
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=3">동영상</a>
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=4">같이할사람</a>
  </div>

  <table class="gc-table">
    <tr>
      <th style="width:90px;">ID</th>
      <th>제목</th>
      <th style="width:160px;">작성자</th>
      <th style="width:90px;">조회</th>
      <th style="width:90px;">좋아요</th>
      <th style="width:200px;">작성일</th>
    </tr>
    <% if(posts == null || posts.isEmpty()){ %>
      <tr>
        <td colspan="6" style="text-align:center; color:var(--gc-muted); padding:18px;">표시할 게시글이 없습니다.</td>
      </tr>
    <% } else { for(Post p : posts){
         Member am = (authorMap == null) ? null : authorMap.get(p.getAuthorLoginId());
         String nick = (am != null && am.getNickname() != null) ? am.getNickname() : p.getAuthorLoginId();
         String img = (am != null) ? am.getProfileImage() : null;
         String imgUrl = (img == null || img.trim().isEmpty()) ? defaultSvg : (ctx + "/uploads/profile/" + img);
         String platform = (p.getLevel() == 4 && p.getPlatform() != null) ? p.getPlatform().trim() : null;
         String badgeClass = "";
         if (platform != null) {
           if ("PS".equalsIgnoreCase(platform)) badgeClass = "gc-badge--ps";
           else if ("SWITCH".equalsIgnoreCase(platform)) badgeClass = "gc-badge--switch";
           else if ("XBOX".equalsIgnoreCase(platform)) badgeClass = "gc-badge--xbox";
           else if ("PC".equalsIgnoreCase(platform)) badgeClass = "gc-badge--pc";
           else if ("MOBILE".equalsIgnoreCase(platform)) badgeClass = "gc-badge--mobile";
         }
    %>
      <tr>
        <td><%=p.getId()%></td>
        <td>
          <a href="<%=ctx%>/community/detail.do?id=<%=p.getId()%>">
            <% if(platform != null && !platform.isEmpty()) { %>
              <span class="gc-badge <%=badgeClass%>" style="margin-right:8px;"><%=platform%></span>
            <% } %>
            <%=p.getTitle()%>
          </a>
        </td>
        <td>
          <a href="<%=ctx%>/member/viewProfile.me?id=<%=java.net.URLEncoder.encode(p.getAuthorLoginId(), "UTF-8")%>" style="text-decoration:none; display:inline-flex; align-items:center; gap:8px;">
            <img src="<%=imgUrl%>" style="width:24px; height:24px; border-radius:50%; object-fit:cover; border:1px solid rgba(255,255,255,0.12);" alt="p" />
            <span><%=nick%></span>
          </a>
        </td>
        <td><%=p.getViews()%></td>
        <td><%=p.getLikes()%></td>
        <td><%=p.getCreatedAt()%></td>
      </tr>
    <% } } %>
  </table>

  <div style="display:flex; justify-content:center; gap:10px; margin-top:14px;">
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=<%=level%>&page=<%=(pageNum-1)%><%=qParam%>">이전</a>
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=<%=level%>&page=<%=(pageNum+1)%><%=qParam%>">다음</a>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />