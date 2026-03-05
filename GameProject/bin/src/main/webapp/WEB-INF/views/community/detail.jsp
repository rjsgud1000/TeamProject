<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/community/detail.jsp

  [역할]
  - 게시글 상세 화면
  - 댓글/좋아요/Q&A 답변 작성 UI를 포함합니다.

  [필요한 request attribute] (CommunityBoardController에서 세팅)
  - post(Post)
  - author(Member)
  - comments(List<Comment>)
  - commentAuthorMap(Map<String,Member>)
  - answers(List<Post>) : Q&A 질문글일 때만
  - answerAuthorMap(Map<Long,Member>) : Q&A 답변 작성자 프로필
  - youtubeEmbedUrl(String) : 동영상(level=3)일 때만
  - likedByMe(Boolean)

  [폼 action]
  - 좋아요: POST /community/like.do (postId)
  - 댓글 작성: POST /comment/write.do (postId, content)
  - 댓글 수정: POST /comment/update.do (postId, commentId, content)
  - 답변 작성(Q&A): POST /community/answer.do (parentId, content)

  [유지보수 포인트]
  - 권한(본인 댓글만 수정) UI는 loginId == comment.author 조건으로만 막고 있으므로,
    서버에서도(CommentService/DAO) 동일한 조건으로 방어가 되는지 유지해야 합니다.
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="vo.Post" %>
<%@ page import="vo.Comment" %>
<%@ page import="vo.Member" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  Post post = (Post)request.getAttribute("post");
  Member author = (Member)request.getAttribute("author");
  Map<String, Member> commentAuthorMap = (Map<String, Member>)request.getAttribute("commentAuthorMap");
  Map<Long, Member> answerAuthorMap = (Map<Long, Member>)request.getAttribute("answerAuthorMap");
  List<Comment> comments = (List<Comment>)request.getAttribute("comments");
  List<Post> answers = (List<Post>)request.getAttribute("answers");
  String youtubeEmbedUrl = (String)request.getAttribute("youtubeEmbedUrl");
  boolean likedByMe = Boolean.TRUE.equals(request.getAttribute("likedByMe"));
  String ctx = request.getContextPath();
  String loginId = (session == null) ? null : (String)session.getAttribute("id");
  String defaultSvg = "data:image/svg+xml;utf8," + java.net.URLEncoder.encode(
      "<svg xmlns='http://www.w3.org/2000/svg' width='96' height='96'>" +
      "<rect width='96' height='96' rx='48' fill='%23222'/>" +
      "<circle cx='48' cy='38' r='18' fill='%23555'/>" +
      "<path d='M16 88c6-18 22-28 32-28s26 10 32 28' fill='%23555'/>" +
      "</svg>",
      "UTF-8");
  String authorNick = (author != null && author.getNickname() != null) ? author.getNickname() : post.getAuthorLoginId();
  String authorImg = (author != null) ? author.getProfileImage() : null;
  String authorImgUrl = (authorImg == null || authorImg.trim().isEmpty()) ? defaultSvg : (ctx + "/uploads/profile/" + authorImg);
%>

<div class="gc-card">
  <div style="display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap; align-items:flex-end;">
    <div>
      <div class="gc-title">
        <%
          String platform = (post.getLevel() == 4 && post.getPlatform() != null) ? post.getPlatform().trim() : null;
          String badgeClass = "";
          if (platform != null) {
            if ("PS".equalsIgnoreCase(platform)) badgeClass = "gc-badge--ps";
            else if ("SWITCH".equalsIgnoreCase(platform)) badgeClass = "gc-badge--switch";
            else if ("XBOX".equalsIgnoreCase(platform)) badgeClass = "gc-badge--xbox";
            else if ("PC".equalsIgnoreCase(platform)) badgeClass = "gc-badge--pc";
            else if ("MOBILE".equalsIgnoreCase(platform)) badgeClass = "gc-badge--mobile";
          }
        %>
        <% if(platform != null && !platform.isEmpty()) { %>
          <span class="gc-badge <%=badgeClass%>" style="margin-right:10px; transform: translateY(-2px);"><%=platform%></span>
        <% } %>
        <%=post.getTitle()%>
      </div>
      <div class="gc-subtitle">
        <a href="<%=ctx%>/member/viewProfile.me?id=<%=java.net.URLEncoder.encode(post.getAuthorLoginId(), "UTF-8")%>" style="text-decoration:none; display:inline-flex; align-items:center; gap:8px;">
          <img src="<%=authorImgUrl%>" style="width:24px; height:24px; border-radius:50%; object-fit:cover; border:1px solid rgba(255,255,255,0.12);" alt="p" />
          <b><%=authorNick%></b>
        </a>
        &nbsp;· level <b><%=post.getLevel()%></b>
        &nbsp;· 조회 <b><%=post.getViews()%></b>
        &nbsp;· 좋아요 <b><%=post.getLikes()%></b>
      </div>
    </div>
    <div>
      <% if(loginId != null) { %>
        <form method="post" action="<%=ctx%>/community/like.do" style="display:inline;">
          <input type="hidden" name="postId" value="<%=post.getId()%>">
          <button class="gc-btn gc-btn-accent" type="submit"><%= likedByMe ? "좋아요 해제" : "좋아요" %></button>
        </form>
        <div style="margin-top:6px; color:var(--gc-muted); font-size:1.2rem;">
          <%= likedByMe ? "내가 좋아요한 글입니다." : "" %>
        </div>
      <% } %>
    </div>
  </div>

  <div class="gc-divider"></div>

  <div style="white-space:pre-wrap; line-height:1.7; font-size:1.6rem;"><%=post.getContent()%></div>

  <% if(post.getLevel() == 3 && post.getYoutubeUrl() != null && !post.getYoutubeUrl().isEmpty()) { %>
    <div class="gc-divider"></div>
    <div style="font-weight:800; margin-bottom:8px;">동영상</div>

    <% if(youtubeEmbedUrl != null) { %>
      <div style="position:relative; padding-top:56.25%; border-radius:14px; overflow:hidden; border:1px solid rgba(255,255,255,0.08);">
        <iframe
          src="<%=youtubeEmbedUrl%>"
          title="YouTube video player"
          style="position:absolute; top:0; left:0; width:100%; height:100%; border:0;"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
          allowfullscreen>
        </iframe>
      </div>
      <div style="margin-top:8px; color:var(--gc-muted); font-size:1.3rem;">원본 링크: <a href="<%=post.getYoutubeUrl()%>" target="_blank"><%=post.getYoutubeUrl()%></a></div>
    <% } else { %>
      <div>유튜브: <a href="<%=post.getYoutubeUrl()%>" target="_blank"><%=post.getYoutubeUrl()%></a></div>
    <% } %>
  <% } %>

  <% if(post.getLevel() == 4 && post.getPlatform() != null) { %>
    <div class="gc-divider"></div>
    <div>플랫폼: <b><%=post.getPlatform()%></b></div>
  <% } %>

  <% if(post.getLevel() == 2 && post.getParentId() == null) { %>
    <div class="gc-divider"></div>
    <div class="gc-title" style="font-size:1.9rem;">답변</div>

    <table class="gc-table">
      <tr><th style="width:160px;">작성자</th><th>내용</th><th style="width:200px;">작성일</th></tr>
      <% if(answers == null || answers.isEmpty()){ %>
        <tr>
          <td colspan="3" style="text-align:center; color:var(--gc-muted); padding:14px;">아직 답변이 없습니다. 첫 답변을 남겨보세요.</td>
        </tr>
      <% } else { for(Post a : answers){
           Member am = (answerAuthorMap == null) ? null : answerAuthorMap.get(a.getId());
           String nick = (am != null && am.getNickname()!=null) ? am.getNickname() : a.getAuthorLoginId();
           String img = (am != null) ? am.getProfileImage() : null;
           String imgUrl = (img == null || img.trim().isEmpty()) ? defaultSvg : (ctx + "/uploads/profile/" + img);
      %>
        <tr>
          <td>
            <a href="<%=ctx%>/member/viewProfile.me?id=<%=java.net.URLEncoder.encode(a.getAuthorLoginId(), "UTF-8")%>" style="text-decoration:none; display:inline-flex; align-items:center; gap:8px;">
              <img src="<%=imgUrl%>" style="width:22px; height:22px; border-radius:50%; object-fit:cover; border:1px solid rgba(255,255,255,0.12);" alt="p" />
              <%=nick%>
            </a>
          </td>
          <td style="white-space:pre-wrap;"><%=a.getContent()%></td>
          <td><%=a.getCreatedAt()%></td>
        </tr>
      <% }} %>
    </table>

    <% if(loginId != null){ %>
      <div style="margin-top:14px;">
        <div style="font-weight:800; margin-bottom:8px;">답변 작성</div>
        <form method="post" action="<%=ctx%>/community/answer.do">
          <input type="hidden" name="parentId" value="<%=post.getId()%>">
          <textarea class="gc-textarea" name="content" rows="4" required></textarea>
          <div style="margin-top:10px;">
            <button class="gc-btn gc-btn-accent" type="submit">답변 등록</button>
          </div>
        </form>
      </div>
    <% } else { %>
      <p style="margin-top:10px;"><a href="<%=ctx%>/member/loginForm.me">로그인</a> 후 답변을 작성할 수 있어요.</p>
    <% } %>
  <% } %>

  <div class="gc-divider"></div>
  <div class="gc-title" style="font-size:1.9rem;">댓글</div>

  <table class="gc-table">
    <tr><th style="width:160px;">작성자</th><th>내용</th><th style="width:220px;">작성일</th></tr>
    <% if(comments != null){ for(Comment c : comments){
         Member cm = (commentAuthorMap == null) ? null : commentAuthorMap.get(c.getAuthorLoginId());
         String nick = (cm != null && cm.getNickname()!=null) ? cm.getNickname() : c.getAuthorLoginId();
         String img = (cm != null) ? cm.getProfileImage() : null;
         String imgUrl = (img == null || img.trim().isEmpty()) ? defaultSvg : (ctx + "/uploads/profile/" + img);
    %>
      <tr>
        <td>
          <a href="<%=ctx%>/member/viewProfile.me?id=<%=java.net.URLEncoder.encode(c.getAuthorLoginId(), "UTF-8")%>" style="text-decoration:none; display:inline-flex; align-items:center; gap:8px;">
            <img src="<%=imgUrl%>" style="width:22px; height:22px; border-radius:50%; object-fit:cover; border:1px solid rgba(255,255,255,0.12);" alt="p" />
            <%=nick%>
          </a>
        </td>
        <td>
          <div style="white-space:pre-wrap;"><%=c.getContent()%></div>

          <% if(loginId != null && loginId.equals(c.getAuthorLoginId())) { %>
            <div style="margin-top:10px;">
              <button class="gc-btn" type="button" onclick="var el=document.getElementById('editBox-<%=c.getId()%>'); el.style.display = (el.style.display==='none'?'block':'none');">수정</button>
            </div>
            <div id="editBox-<%=c.getId()%>" style="display:none; margin-top:10px;">
              <form method="post" action="<%=ctx%>/comment/update.do">
                <input type="hidden" name="postId" value="<%=post.getId()%>">
                <input type="hidden" name="commentId" value="<%=c.getId()%>">
                <textarea class="gc-textarea" name="content" rows="3" required><%=c.getContent()%></textarea>
                <div style="margin-top:10px;">
                  <button class="gc-btn gc-btn-accent" type="submit">댓글 수정 저장</button>
                </div>
              </form>
            </div>
          <% } %>
        </td>
        <td><%=c.getCreatedAt()%></td>
      </tr>
    <% }} %>
  </table>

  <% if(loginId != null){ %>
    <div style="margin-top:14px;">
      <div style="font-weight:800; margin-bottom:8px;">댓글 작성</div>
      <form method="post" action="<%=ctx%>/comment/write.do">
        <input type="hidden" name="postId" value="<%=post.getId()%>">
        <textarea class="gc-textarea" name="content" rows="3" required></textarea>
        <div style="margin-top:10px;">
          <button class="gc-btn" type="submit">댓글 등록</button>
        </div>
      </form>
    </div>
  <% } else { %>
    <p style="margin-top:10px;"><a href="<%=ctx%>/member/loginForm.me">로그인</a> 후 댓글을 작성할 수 있어요.</p>
  <% } %>

  <div style="display:flex; justify-content:space-between; margin-top:14px;">
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=<%=post.getLevel()%>">목록</a>
    <a class="gc-btn" href="<%=ctx%>/community/list.do?level=5">홈</a>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />