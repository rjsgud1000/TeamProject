<%@ page import="java.sql.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- ==========================================================
📌 join.jsp - 회원가입 입력 폼
========================================================== --%>

<%
	request.setCharacterEncoding("UTF-8");
	String contextPath = request.getContextPath();
%>

<!doctype html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>회원가입</title>

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
html,body{ height:100%; }
body{
	margin:0;
	background: var(--bg);
	color: var(--text);
	font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, "Apple SD Gothic Neo","Noto Sans KR","Malgun Gothic", Arial, sans-serif;
}

a{ color:inherit; text-decoration:none; }

.container{
	width:min(980px, calc(100% - 32px));
	margin: 0 auto;
}

/* 카드 */
.join-card{
	margin: 44px 0;
	background: var(--surface);
	border: 1px solid var(--line);
	border-radius: var(--r16);
	box-shadow: var(--shadow);
	overflow:hidden;
}

.join-hero{
	padding: 26px 22px 22px;
	background: linear-gradient(135deg, var(--accent2) 0%, var(--accent) 45%, #0ea5e9 100%);
	color:#fff;
}
.join-hero .step{
	display:inline-flex;
	align-items:center;
	gap:8px;
	height: 32px;
	padding: 0 12px;
	border-radius: 999px;
	background: rgba(255,255,255,.18);
	border: 1px solid rgba(255,255,255,.22);
	font-weight: 900;
	font-size: 12px;
	letter-spacing:.3px;
}
.join-hero h1{
	margin: 14px 0 0;
	font-size: 26px;
	font-weight: 900;
}
.join-hero p{
	margin: 8px 0 0;
	opacity:.92;
	font-size: 13px;
}
.join-hero .hero-right{
	margin-top: 10px;
	font-size: 13px;
	opacity:.92;
}
.join-hero .hero-right a{
	color:#fff;
	text-decoration: underline;
	font-weight: 900;
}

.join-body{ padding: 22px; }

.section-title{
	margin: 0 0 10px;
	font-size: 16px;
	font-weight: 900;
}
.help-hint{ color: var(--sub); font-size: 12px; margin-top:6px; }

.grid{
	display:grid;
	grid-template-columns: 1fr 1fr;
	gap: 14px;
}
@media (max-width: 820px){
	.grid{ grid-template-columns: 1fr; }
	.join-card{ margin: 26px 0; }
}

.field{ margin-top: 14px; }
.label{
	display:block;
	font-size: 13px;
	font-weight: 900;
	margin-bottom: 8px;
}
.req{ color:#dc2626; font-weight:900; }

input[type="text"],
input[type="password"],
input[type="email"]{
	width:100%;
	padding: 12px 14px;
	border: 1px solid #d8e3f2;
	border-radius: 12px;
	font-size: 14px;
	outline: none;
	transition: border-color .2s, box-shadow .2s;
	background: #fff;
}
input[type="text"]:focus,
input[type="password"]:focus,
input[type="email"]:focus{
	border-color: rgba(37, 99, 235, .65);
	box-shadow: 0 0 0 4px rgba(37, 99, 235, .14);
}

/* 입력 + 버튼(중복확인) */
.input-row{
	display:flex;
	gap: 10px;
	align-items: stretch;
}
.input-row .btn{ flex: 0 0 auto; }

/* 버튼 */
.btn{
	height: 44px;
	padding: 0 14px;
	border-radius: 12px;
	border: 1px solid transparent;
	font-size: 14px;
	font-weight: 900;
	cursor: pointer;
	display:inline-flex;
	align-items:center;
	justify-content:center;
	gap: 8px;
	user-select:none;
}
.btn:active{ transform: translateY(1px); }

.btn-primary{
	border:0;
	color:#fff;
	background: linear-gradient(135deg, var(--accent) 0%, var(--accent2) 100%);
}
.btn-outline{
	background:#fff;
	border-color:#d8e3f2;
	color:#0f172a;
}
.btn-outline:hover{ background:#f8fafc; }
.btn-outline-primary{
	background:#fff;
	border-color: rgba(37, 99, 235, .30);
	color: var(--accent2);
}
.btn-outline-primary:hover{ background: rgba(37, 99, 235, .06); }
.btn-block{ width:100%; }

/* 메시지(기존 join.js가 form-text 클래스를 사용하므로 함께 지원) */
.form-text{ font-size: 12px; margin-top: 6px; }
.text-success{ color:#16a34a; font-weight: 900; }
.text-danger{ color:#dc2626; font-weight: 900; }

/* 경고/에러 */
.alert{
	border-radius: 12px;
	padding: 10px 12px;
	font-size: 13px;
	border: 1px solid rgba(220, 38, 38, .25);
	background: rgba(220, 38, 38, .08);
	color: #b91c1c;
	font-weight: 900;
	text-align:center;
	margin: 18px 0;
}

/* 약관 아코디언 */
.accordion{
	border: 1px solid var(--line);
	border-radius: 14px;
	overflow:hidden;
	background:#fff;
}
.details{
	border-top: 1px solid var(--line);
}
.details:first-child{ border-top:0; }
.details summary{
	list-style:none;
	cursor:pointer;
	padding: 14px 14px;
	font-weight: 900;
	color:#0f172a;
	background: #f9fbff;
}
.details summary::-webkit-details-marker{ display:none; }
.details summary:after{
	content:'▾';
	float:right;
	opacity:.7;
}
.details[open] summary:after{ content:'▴'; }
.details .content{
	padding: 14px;
	color: #334155;
	font-size: 13px;
	line-height: 1.5;
}

/* 체크/라디오 */
.check-row{
	display:flex;
	gap:10px;
	align-items:flex-start;
	margin-top: 12px;
}
.check-row input{ margin-top: 3px; }

.radio-group{
	display:flex;
	gap: 14px;
	flex-wrap: wrap;
}
.radio{
	display:flex;
	gap: 8px;
	align-items:center;
	padding: 10px 12px;
	border: 1px solid #d8e3f2;
	border-radius: 12px;
	background:#fff;
}

/* 구분선 */
.hr{ height:1px; background: var(--line); margin: 18px 0; }

.actions{
	display:grid;
	grid-template-columns: 1fr 1fr;
	gap: 12px;
	margin-top: 18px;
}
@media (max-width: 520px){
	.actions{ grid-template-columns: 1fr; }
}

/* 비밀번호 인풋 + 토글 버튼 (login.jsp와 동일 UX) */
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
</style>
</head>
<body>

<%-- 회원가입 실패 메시지 출력 --%>
<%
	String joinError = (String) request.getAttribute("joinError");
	if (joinError != null) {
%>
	<div class="container">
		<div class="alert" role="alert"><%= joinError %></div>
	</div>
<% } %>

<div class="container">
	<div class="join-card">
		<div class="join-hero">
			<div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; flex-wrap:wrap;">
				<div>
					<span class="step">STEP 01</span>
					<h1>회원가입</h1>
					<p>필수 항목을 입력하고, 중복확인을 완료해 주세요.</p>
				</div>
				<div class="hero-right">
					이미 계정이 있나요? <a href="<%=contextPath%>/member/login.me">로그인</a>
				</div>
			</div>
		</div>

		<div class="join-body">
			<form action="<%=contextPath%>/member/joinPro.me" method="post">

				<div class="section-title">이용약관</div>
				<div class="help-hint">서비스 이용을 위해 약관 동의가 필요합니다.</div>

				<div class="field">
					<div class="accordion" aria-label="이용약관">
						<details class="details">
							<summary>약관동의 내용1</summary>
							<div class="content">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</div>
						</details>
						<details class="details">
							<summary>약관동의 내용2</summary>
							<div class="content">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</div>
						</details>
						<details class="details">
							<summary>약관동의 내용3</summary>
							<div class="content">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</div>
						</details>
					</div>

					<div class="check-row">
						<input type="checkbox" name="agree" id="agree">
						<label for="agree" style="font-weight:900;">위의 약관의 내용에 동의합니다.</label>
					</div>
					<p id="agreeInput" class="form-text"></p>
				</div>

				<div class="hr"></div>

				<div class="section-title">기본 정보</div>
				<div class="help-hint">필수(*) 항목을 입력해주세요.</div>

				<div class="grid">
					<div>
						<div class="field">
							<label class="label" for="id">아이디 <span class="req">*</span></label>
							<div class="input-row">
								<input type="text" id="id" name="id" placeholder="아이디를 입력해 주세요">
								<button class="btn btn-outline-primary" type="button" onclick="checkDuplicateId()">중복확인</button>
							</div>
							<small id="idDupMsg" class="form-text"></small>
							<p id="idInput" class="form-text"></p>
						</div>
					</div>

					<div>
						<div class="field">
							<label class="label" for="pass">비밀번호 <span class="req">*</span></label>
							<div class="pw-wrap">
								<input type="password" id="pass" name="pass" placeholder="비밀번호를 입력해 주세요">
								<button class="pw-toggle" type="button" id="pwToggle" aria-label="비밀번호 보기/숨기기" title="비밀번호 보기/숨기기">👁</button>
							</div>
							<div class="notice" id="capsNotice" role="status">CapsLock이 켜져 있어요</div>
							<div class="help-hint">영문/숫자 조합을 권장합니다.</div>
							<p id="passInput" class="form-text"></p>
						</div>
					</div>
				</div>

				<div class="field">
					<label class="label" for="nickname">닉네임 <span class="req">*</span></label>
					<div class="input-row">
						<input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력해 주세요">
						<button class="btn btn-outline-primary" type="button" onclick="checkDuplicateNickname()">중복확인</button>
					</div>
					<small id="nickDupMsg" class="form-text"></small>
					<p id="nickInput" class="form-text"></p>
				</div>

				<div class="grid">
					<div>
						<div class="field">
							<div class="label">주소</div>
							<p id="addressInput" class="form-text"></p>

							<div class="grid" style="grid-template-columns: 1fr 140px; align-items: stretch;">
								<input type="text" id="sample4_postcode" name="address1" placeholder="우편번호">
								<input type="button" onclick="sample4_execDaumPostcode()" value="찾기" class="btn btn-outline-primary btn-block" style="height:44px;">
							</div>

							<div class="field" style="margin-top:10px;">
								<input type="text" id="sample4_roadAddress" name="address2" placeholder="도로명주소">
							</div>
							<div class="field" style="margin-top:10px;">
								<input type="text" id="sample4_jibunAddress" name="address3" placeholder="지번주소">
							</div>
							<span id="guide" class="help-hint" style="display:none"></span>
							<div class="field" style="margin-top:10px;">
								<input type="text" id="sample4_detailAddress" name="address4" placeholder="상세주소">
							</div>
							<div class="field" style="margin-top:10px;">
								<input type="text" id="sample4_extraAddress" name="address5" placeholder="참고항목">
							</div>
						</div>
					</div>

					<div>
						<div class="field">
							<div class="label">성별</div>
							<p id="genderInput" class="form-text"></p>
							<div class="radio-group">
								<label class="radio" for="genderMan"><input type="radio" class="gender" name="gender" id="genderMan" value="man"> 남성</label>
								<label class="radio" for="genderWoman"><input type="radio" class="gender" name="gender" id="genderWoman" value="woman"> 여성</label>
							</div>
							<div class="help-hint">선택 항목입니다.</div>
						</div>
					</div>
				</div>

				<div class="hr"></div>

				<div class="section-title">연락처</div>
				<div class="grid">
					<div>
						<div class="field">
							<label class="label" for="email">Email</label>
							<input type="email" id="email" name="email" placeholder="example@domain.com">
							<p id="emailInput" class="form-text"></p>
						</div>
					</div>
					<div>
						<div class="field">
							<label class="label" for="hp">핸드폰번호</label>
							<input type="text" id="hp" name="hp" placeholder="'-' 없이 숫자만 입력 (10~11자리)">
							<p id="hpInput" class="form-text"></p>
						</div>
					</div>
				</div>

				<div class="actions">
					<a href="<%=contextPath%>/main.jsp" class="btn btn-outline btn-block">취소</a>
					<button type="button" class="btn btn-primary btn-block" onclick="check();">회원가입하기</button>
				</div>

			</form>
		</div>
	</div>
</div>

<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
	function sample4_execDaumPostcode() {
		new daum.Postcode({
			oncomplete: function(data) {
				var roadAddr = data.roadAddress;
				var extraRoadAddr = '';

				if(data.bname !== '' && /[\ub3d9|\ub85c|\uac00]$/g.test(data.bname)){
					extraRoadAddr += data.bname;
				}
				if(data.buildingName !== '' && data.apartment === 'Y'){
					extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
				}
				if(extraRoadAddr !== ''){
					extraRoadAddr = ' (' + extraRoadAddr + ')';
				}

				document.getElementById('sample4_postcode').value = data.zonecode;
				document.getElementById("sample4_roadAddress").value = roadAddr;
				document.getElementById("sample4_jibunAddress").value = data.jibunAddress;

				if(roadAddr !== ''){
					document.getElementById("sample4_extraAddress").value = extraRoadAddr;
				} else {
					document.getElementById("sample4_extraAddress").value = '';
				}

				var guideTextBox = document.getElementById("guide");
				if(data.autoRoadAddress) {
					var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
					guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
					guideTextBox.style.display = 'block';
				} else if(data.autoJibunAddress) {
					var expJibunAddr = data.autoJibunAddress;
					guideTextBox.innerHTML = '(예상 지번 주소 : ' + expJibunAddr + ')';
					guideTextBox.style.display = 'block';
				} else {
					guideTextBox.innerHTML = '';
					guideTextBox.style.display = 'none';
				}
			}
		}).open();
	}
</script>

<script>var joinContextPath = "<%=contextPath%>";</script>
<script src="<%=request.getContextPath()%>/js/join.js"></script>
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