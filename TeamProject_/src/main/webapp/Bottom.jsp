<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String contextPath = request.getContextPath();
%>
<footer>
    <div class="container">
      <div class="foot">
        <div>
          <a class="brand" href="#">
            <img src="<%=contextPath%>/img/logo.png" alt="G-UNIVERSE 로고" style="width:70px; height:34px; border-radius:10px; object-fit:cover; display:inline-block; vertical-align:middle;" />
            <span class="brand__name">G-UNIVERSE</span>
          </a>

          <div class="foot-links">
            <a href="#">회사소개</a>
            <a href="#">개인정보처리방침</a>
            <a href="#">사이트맵</a>
            <a href="#">고객센터</a>
          </div>

          <small>
            대표전화 : 02-1234-5678 &nbsp;|&nbsp; 고객센터 : 010-1234-5678<br/>
            주소 : (예시) 서울특별시 구로구 시흥대로 110 4F<br/>
            Copyright © G-UNIVERSE. All rights reserved.
          </small>
        </div>

        <div class="foot-right">
          상담시간 : 평일 11:00 ~ 18:00<br/>
          점심시간 : 12:30 ~ 13:30
        </div>
      </div>
    </div>
  </footer>

<script>
    // 디자인용 탭 전환 (공지사항 / 최신작업)
    (function(){
      const tabs = document.querySelectorAll(".tab");
      const panes = document.querySelectorAll("[data-pane]");
      tabs.forEach(btn => {
        btn.addEventListener("click", () => {
          tabs.forEach(t => t.classList.remove("is-active"));
          btn.classList.add("is-active");

          const key = btn.getAttribute("data-tab");
          panes.forEach(p => p.style.display = (p.getAttribute("data-pane") === key) ? "flex" : "none");
        });
      });
    })();
  </script>