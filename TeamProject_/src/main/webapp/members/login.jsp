<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- ==========================================================
📌 login.jsp - 로그인 입력 폼
========================================================== --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>로그인 화면</title>

<style>
/* =============================================
   [로그인 페이지] 반응형 스타일
   ============================================= */

/* 전체 페이지 가운데 정렬 */
.login-page {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 400px;
    padding: 30px 15px;
    box-sizing: border-box;
}

/* 로그인 카드 */
.login-card {
    width: 100%;
    max-width: 400px;     /* 최대 너비 (수정: 여기서 너비 조정) */
    padding: 30px 25px;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1); /* 그림자 효과 */
    box-sizing: border-box;
}

/* 로그인 제목 */
.login-card h2 {
    text-align: center;
    margin-bottom: 25px;
    font-size: 22px;
    color: #333;
    font-weight: bold;
}

/* 스크린리더용 숨김 클래스 (접근성) */
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0,0,0,0);
    white-space: nowrap;
    border: 0;
}

/* 입력 필드 공통 스타일 */
.login-card input[type="text"],
.login-card input[type="password"] {
    width: 100%;
    padding: 10px 14px;
    margin-bottom: 15px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
    box-sizing: border-box;
    transition: border-color 0.2s; /* 포커스 애니메이션 */
}

/* 포커스 시 테두리 파랑 */
.login-card input[type="text"]:focus,
.login-card input[type="password"]:focus {
    outline: none;
    border-color: #0055cc;
    box-shadow: 0 0 0 2px rgba(0,85,204,0.15);
}

/* 로그인 버튼 */
.btn-login {
    width: 100%;
    padding: 12px;
    background-color: rgb(0, 0, 255);   /* 빨간 버튼 (수정: 여기서 색상 변경) */
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.2s;
}

.btn-login:hover {
    background-color: rgb(0, 0, 255);
}

/* =============================================
   [반응형] 모바일 (480px 이하)
   ============================================= */
@media (max-width: 480px) {

    .login-card {
        padding: 20px 15px;
    }

    .login-card h2 {
        font-size: 18px;
    }

}
/* 모바일 반응형 끝 */
</style>

</head>
<body>

<%
	// 회원가입 성공 메시지(1회성)
	String joinFlash = null;
	javax.servlet.http.HttpSession flashSession = request.getSession(false);
	if (flashSession != null) {
		joinFlash = (String) flashSession.getAttribute("joinFlash");
		if (joinFlash != null) {
			flashSession.removeAttribute("joinFlash");
		}
	}
%>
<% if (joinFlash != null) { %>
	<script>
		alert('<%= joinFlash.replace("'", "\\'") %>');
	</script>
<% } %>

    <!-- ==========================================
         로그인 화면
         ========================================== -->
    <div class="login-page">
        <div class="login-card">

            <%-- 로그인 실패 메시지 출력 --%>
            <%
            String loginError = (String) request.getAttribute("loginError");
            if (loginError != null) {
            %>
                <div style="margin-bottom:12px; color:#d00; font-weight:bold; text-align:center;">
                    <%= loginError %>
                </div>
            <% } %>

            <%--MemberController서블릿에.. 로그인 처리 요청시! 입력한 id와 패스워드 전달 --%>
            <form class="form-signin"
                  method="post"
                  action="<%=request.getContextPath()%>/member/loginPro.me" id="join">

                <h2 class="form-signin-heading">로그인 화면</h2>

                <!-- 아이디 입력 (스크린리더용 레이블) -->
                <label class="sr-only">아이디</label>
                <input type="text" id="id" name="id"
                       placeholder="아이디" required autofocus>

                <!-- 비밀번호 입력 -->
                <label for="inputPassword" class="sr-only">비밀번호</label>
                <input type="password" id="pass" name="pass"
                       class="form-control" placeholder="패스워드" required>

                <!-- 로그인 버튼 -->
                <button class="btn-login" type="submit">로그인</button>

            </form>

        </div>
    </div>

</body>
</html>