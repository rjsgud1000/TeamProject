<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/layout/footer.jsp

  [역할]
  - 콘텐츠 영역 닫기 + 하단 푸터 + 공통 스크립트 로드

  [유지보수 포인트]
  - Bootstrap/jQuery CDN 버전 변경 시 이 파일을 수정합니다.
  - header.jsp에서 열어둔 .gc-container 구조와 짝이 맞아야 레이아웃이 깨지지 않습니다.
--%>
<%
	request.setCharacterEncoding("UTF-8");
	String contextPath = request.getContextPath();
%>

</div> <%-- gc-container (content) end --%>

<div class="gc-container gc-footer">
	<div class="gc-divider"></div>
	<div style="display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap;">
		<div>
			<b>Game Community</b>
			<span style="margin-left:12px;">이용약관</span>
			<span style="margin-left:12px;">개인정보 처리방침</span>
			<span style="margin-left:12px;">문의</span>
		</div>
		<div style="color: var(--gc-muted);">
			<small>학습/포트폴리오 목적 커뮤니티 예시</small>
		</div>
	</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-Fy6S3B9q64WdZWQUiU+q4/2Lc9npb8tCaSX9FK7E8HnRr0Jz8D6OP9dO5Vg3Q9ct" crossorigin="anonymous"></script>

</body>
</html>