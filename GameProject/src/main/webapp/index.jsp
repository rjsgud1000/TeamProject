<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%--
  index.jsp

  [역할]
  - 웹앱의 기본 진입점(루트: /)에서 사용자를 "커뮤니티 홈"으로 이동시킵니다.

  [유지보수 포인트]
  - 홈 화면/초기 랜딩 페이지가 바뀌면 아래 location.href 대상 URL만 변경하면 됩니다.
  - 서버 리다이렉트(response.sendRedirect)로 바꾸고 싶다면 JSP가 아니라
    web.xml의 welcome-file 또는 별도 서블릿/필터에서 처리하는 편이 더 안전합니다.
--%>

<script>
	// 커뮤니티 홈(인기 게시글)로 이동
	// level 파라미터는 게시글 필터/표시 정책에 사용됩니다.
	location.href="community/list.do?level=5";
</script>