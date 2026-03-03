<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<%
  int level = (Integer)request.getAttribute("level");
  String ctx = request.getContextPath();
%>

<div class="gc-card">
  <div class="gc-title">글쓰기</div>
  <div class="gc-subtitle">카테고리 level=<%=level%></div>

  <form method="post" action="<%=ctx%>/community/write.do">
    <input type="hidden" name="level" value="<%=level%>">

    <div style="margin-bottom:12px;">
      <label style="font-weight:800;">제목</label>
      <input class="gc-form-control" type="text" name="title" required style="max-width:none;" />
    </div>

    <div style="margin-bottom:12px;">
      <label style="font-weight:800;">내용</label>
      <textarea class="gc-textarea" name="content" rows="10"></textarea>
    </div>

    <% if(level == 3){ %>
      <div style="margin-bottom:12px;">
        <label style="font-weight:800;">유튜브 URL</label>
        <input class="gc-form-control" type="text" name="youtubeUrl" placeholder="https://www.youtube.com/watch?v=..." style="max-width:none;" />
      </div>
    <% } %>

    <% if(level == 4){ %>
      <div style="margin-bottom:12px;">
        <label style="font-weight:800;">플랫폼</label>
        <select class="gc-form-control" name="platform" style="max-width:260px;">
          <option value="PC">PC</option>
          <option value="PS">PlayStation</option>
          <option value="XBOX">XBOX</option>
          <option value="SWITCH">SWITCH</option>
          <option value="MOBILE">MOBILE</option>
        </select>
      </div>
    <% } %>

    <div style="display:flex; gap:8px;">
      <button class="gc-btn gc-btn-accent" type="submit">등록</button>
      <a class="gc-btn" href="<%=ctx%>/community/list.do?level=<%=level%>">목록</a>
    </div>
  </form>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />