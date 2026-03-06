<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- ==========================================================
📌 login.jsp - 로그인 입력 폼
========================================================== --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>로그인</title>

<style>
:root{
	--bg:#f6f8fb;
	--surface:#ffffff;
	--line:#e6edf7;
	--text:#0f172a;
	--sub:#64748b;
	--accent:#2563eb;
	--accent2:#1d4ed8;
	--shadow: 0 16px 44px rgba(2, 18, 52, .12);
	--r16:16px;
}

*{ box-sizing:border-box; }
body{
	margin:0;
	background: var(--bg);
	color: var(--text);
	font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, "Apple SD Gothic Neo","Noto Sans KR","Malgun Gothic", Arial, sans-serif;
}

/* 전체 페이지 가운데 정렬 */
.login-page{
	display:flex;
	align-items:center;
	justify-content:center;
	min-height: calc(100vh - 1px);
	padding: 34px 16px;
}

/* 로그인 카드 */
.login-card{
	width:100%;
	max-width: 420px;
	background: var(--surface);
	border: 1px solid var(--line);
	border-radius: var(--r16);
	box-shadow: var(--shadow);
	overflow:hidden;
}

.login-hero{
	padding: 22px 22px 18px;
	background: linear-gradient(135deg, var(--accent2) 0%, var(--accent) 45%, #0ea5e9 100%);
	color:#fff;
}
.login-hero h2{
	margin:0;
	font-size: 22px;
	font-weight: 900;
	letter-spacing:.2px;
}
.login-hero p{
	margin:8px 0 0;
	opacity:.92;
	font-size: 13px;
}

.login-body{
	padding: 22px;
}

.form-row{ margin-top: 14px; }
.label{
	display:block;
	font-size: 13px;
	font-weight: 800;
	margin-bottom: 8px;
	color: var(--text);
}

.login-card input[type="text"],
.login-card input[type="password"]{
	width:100%;
	padding: 12px 14px;
	border: 1px solid #d8e3f2;
	border-radius: 12px;
	font-size: 14px;
	outline: none;
	transition: border-color .2s, box-shadow .2s;
	background: #fff;
}

.login-card input[type="text"]:focus,
.login-card input[type="password"]:focus{
	border-color: rgba(37, 99, 235, .65);
	box-shadow: 0 0 0 4px rgba(37, 99, 235, .14);
}

/* 비밀번호 인풋 + 토글 버튼 */
.pw-wrap{ position: relative; }
.pw-wrap input{ padding-right: 46px; }
.pw-toggle{
	position:absolute;
	right: 10px;
	top: 50%;
	transform: translateY(-50%);
	width: 34px;
	height: 34px;
	border: 1px solid #d8e3f2;
	background: #fff;
	border-radius: 10px;
	cursor:pointer;
	display:grid;
	place-items:center;
	color:#334155;
}
.pw-toggle:hover{ background:#f8fafc; }
.pw-toggle:active{ transform: translateY(calc(-50% + 1px)); }

/* CapsLock 안내 */
.notice{
	border-radius: 12px;
	padding: 10px 12px;
	font-size: 13px;
	border: 1px solid rgba(245, 158, 11, .30);
	background: rgba(245, 158, 11, .12);
	color: #92400e;
	font-weight: 900;
	text-align:center;
	margin-top: 10px;
	display:none;
}

/* 로그인 버튼 */
.btn-login{
	width:100%;
	margin-top: 16px;
	padding: 12px 14px;
	border:0;
	border-radius: 12px;
	background: linear-gradient(135deg, var(--accent) 0%, var(--accent2) 100%);
	color:#fff;
	font-size: 15px;
	font-weight: 900;
	cursor:pointer;
	transition: transform .06s ease, filter .2s ease;
}
.btn-login:hover{ filter: brightness(1.02); }
.btn-login:active{ transform: translateY(1px); }

.alert{
	border-radius: 12px;
	padding: 10px 12px;
	font-size: 13px;
	border: 1px solid rgba(220, 38, 38, .25);
	background: rgba(220, 38, 38, .08);
	color: #b91c1c;
	font-weight: 800;
	text-align:center;
	margin-bottom: 12px;
}

.muted{
	margin-top: 14px;
	font-size: 13px;
	color: var(--sub);
	text-align:center;
}
.muted a{
	color: var(--accent2);
	font-weight: 900;
	text-decoration:none;
}
.muted a:hover{ text-decoration: underline; }

@media (max-width: 480px){
	.login-hero{ padding: 18px 16px 16px; }
	.login-body{ padding: 16px; }
}
</style>

</head>
<body>

<%
	String contextPath = request.getContextPath();

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
		alert('<%= joinFlash.replace("\\", "\\\\").replace("'", "\\'") %>');
	</script>
<% } %>

<div class="login-page">
	<div class="login-card" role="region" aria-label="로그인">

		<div class="login-hero">
			<h2>로그인</h2>
			<p>G-UNIVERSE에 오신 걸 환영합니다. 계정으로 로그인해 주세요.</p>
		</div>

		<div class="login-body">
			<%-- 로그인 실패 메시지 출력 --%>
			<%
			String loginError = (String) request.getAttribute("loginError");
			if (loginError != null) {
			%>
				<div class="alert" role="alert"><%= loginError %></div>
			<% } %>

			<%-- MemberController 서블릿에 로그인 처리 요청: id/pass 전달 --%>
			<form class="form-signin" method="post"
				action="<%=contextPath%>/member/loginPro.me" id="join">

				<div class="form-row">
					<label class="label" for="id">아이디</label>
					<input type="text" id="id" name="id" placeholder="아이디" required autofocus>
				</div>

				<div class="form-row">
					<label class="label" for="pass">비밀번호</label>
					<div class="pw-wrap">
						<input type="password" id="pass" name="pass" placeholder="비밀번호" required>
						<button class="pw-toggle" type="button" id="pwToggle" aria-label="비밀번호 보기/숨기기" title="비밀번호 보기/숨기기">👁</button>
					</div>
					<div class="notice" id="capsNotice" role="status">CapsLock이 켜져 있어요</div>
				</div>

				<button class="btn-login" type="submit">로그인</button>

				<div class="muted">
					아직 계정이 없나요? <a href="<%=contextPath%>/member/join.me">회원가입</a>
					&nbsp;|&nbsp;
					<a href="<%=contextPath%>/main.jsp">메인으로</a>
				</div>
			</form>
		</div>
	</div>
</div>

<script>
(function(){
	var passEl = document.getElementById('pass');
	var toggleBtn = document.getElementById('pwToggle');
	var capsNotice = document.getElementById('capsNotice');

	function setCapsNotice(on){
		if (!capsNotice) return;
		capsNotice.style.display = on ? 'block' : 'none';
	}

	if (toggleBtn && passEl) {
		toggleBtn.addEventListener('click', function(){
			var isPw = passEl.getAttribute('type') === 'password';
			passEl.setAttribute('type', isPw ? 'text' : 'password');
			toggleBtn.textContent = isPw ? '🙈' : '👁';
			passEl.focus();
		});
	}

	if (passEl) {
		var handler = function(e){
			try {
				if (typeof e.getModifierState === 'function') {
					setCapsNotice(e.getModifierState('CapsLock'));
				}
			} catch (err) {
				// ignore
			}
		};
		passEl.addEventListener('keydown', handler);
		passEl.addEventListener('keyup', handler);
		passEl.addEventListener('focus', function(){ setCapsNotice(false); });
		passEl.addEventListener('blur', function(){ setCapsNotice(false); });
	}
})();
</script>

</body>
</html>