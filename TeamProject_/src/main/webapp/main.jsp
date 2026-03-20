<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    String loginFlash = (String) session.getAttribute("loginFlash");
    if (loginFlash != null) {
        session.removeAttribute("loginFlash");
%>

<script type="text/javascript">
    alert("<%= loginFlash.replace("\\", "\\\\").replace("\"", "\\\"") %>");
</script>
<%
    }
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>G-UNIVERSE - 홈</title>

<link rel="stylesheet"href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"rel="stylesheet">

</head>

<body>
	<c:set var="center" value="${requestScope.center}" />

	<c:if test="${empty requestScope.isAdmin}">
		<c:set var="isAdmin"
			value="${sessionScope.isAdmin or sessionScope.loginRole eq 'ADMIN'}"
			scope="request" />
	</c:if>

	<c:if test="${empty center}">
		<c:set var="center" value="Center.jsp" />
	</c:if>

	<jsp:include page="Top.jsp" />
	<div class="main-content">
		<jsp:include page="${center}" />
	</div>
	<jsp:include page="Bottom.jsp" />
</body>
</html>