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

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/auth.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/member-pages.css" />

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
			<div class="page-flex-head">
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
			<form action="<%=contextPath%>/member/joinPro.me" method="post" id="joinForm">
				<input type="hidden" id="emailVerified" name="emailVerified" value="false">

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
						<label for="agree" class="field-label-strong">위의 약관의 내용에 동의합니다.</label>
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
								<input type="text" id="id" name="id" placeholder="영문, 숫자 4~20자" pattern="[A-Za-z0-9]{4,20}" maxlength="20" minlength="4" autocomplete="username">
								<button class="btn btn-outline-primary" type="button" onclick="checkDuplicateId()">중복확인</button>
							</div>
							<small id="idDupMsg" class="form-text"></small>
							<p id="idInput" class="form-text"></p>
						</div>
					</div>

					<div>
						<div class="field">
							<label class="label" for="name">이름 <span class="req">*</span></label>
							<input type="text" id="name" name="name" placeholder="이름을 입력해 주세요" autocomplete="name">
							<p id="nameInput" class="form-text"></p>
						</div>
					</div>
				</div>

				<div class="grid">
					<div>
						<div class="field">
							<label class="label" for="pass">비밀번호 <span class="req">*</span></label>
							<div class="pw-wrap">
								<input type="password" id="pass" name="pass" placeholder="영문+숫자 포함 6자 이상" minlength="6" autocomplete="new-password">
								<button class="pw-toggle" type="button" id="pwToggle" aria-label="비밀번호 보기/숨기기" title="비밀번호 보기/숨기기">👁</button>
							</div>
							<div class="notice" id="capsNotice" role="status">CapsLock이 켜져 있어요</div>
							<div class="help-hint">비밀번호는 영문과 숫자를 포함해 6자 이상 입력해 주세요.</div>
							<p id="passInput" class="form-text"></p>
						</div>
					</div>
					
					<div>
						<div class="field">
							<label class="label" for="nickname">닉네임 <span class="req">*</span></label>
							<div class="input-row">
								<input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력해 주세요">
								<button class="btn btn-outline-primary" type="button" onclick="checkDuplicateNickname()">중복확인</button>
							</div>
							<small id="nickDupMsg" class="form-text"></small>
							<p id="nickInput" class="form-text"></p>
						</div>
					</div>
				</div>

				<div class="field">
					<label class="label" for="address">주소</label>
					<p id="addressInput" class="form-text"></p>

					<div class="grid address-zip-grid">
						<input type="text" id="sample4_postcode" name="address1" placeholder="우편번호" readonly>
						<input type="button" onclick="sample4_execDaumPostcode()" value="찾기" class="btn btn-outline-primary btn-block btn-fixed-height">
					</div>

					<div class="field mt-10">
						<input type="text" id="sample4_roadAddress" name="address2" placeholder="도로명주소" readonly>
					</div>
					<div class="field mt-10">
						<input type="text" id="sample4_jibunAddress" name="address3" placeholder="지번주소" readonly>
					</div>
					<span id="guide" class="help-hint help-hidden"></span>
					<div class="field mt-10">
						<input type="text" id="sample4_detailAddress" name="address4" placeholder="상세주소">
					</div>
					<div class="field mt-10">
						<input type="text" id="sample4_extraAddress" name="address5" placeholder="참고항목" readonly>
					</div>
				</div>

				<div class="grid">
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
							<label class="label" for="email">Email <span class="req">*</span></label>
							<div class="email-verify-card">
								<div class="email-verify-head">
									<div class="email-verify-title">
										<span class="email-verify-step">1</span>
										<span>이메일 인증</span>
									</div>
									<div id="emailVerifyChip" class="email-verify-chip">인증 전</div>
								</div>
								<div class="email-verify-copy">가입할 이메일로 인증번호를 발송합니다. 이메일을 변경하면 인증 상태가 초기화됩니다.</div>
								<div class="email-verify-wrap">
									<div class="input-row">
										<input type="email" id="email" name="email" placeholder="example@domain.com" autocomplete="email">
										<button class="btn btn-outline-primary" type="button" id="sendEmailCodeBtn" onclick="sendJoinEmailCode()">인증번호 받기</button>
									</div>
									<div class="email-verify-row">
										<input type="text" id="emailVerificationCode" placeholder="메일로 받은 인증번호를 입력해 주세요" inputmode="numeric" autocomplete="one-time-code">
										<button class="btn btn-outline-primary" type="button" id="verifyEmailCodeBtn" onclick="verifyJoinEmailCode()">이메일 인증</button>
									</div>
								</div>
								<p id="emailInput" class="form-text"></p>
								<p id="emailVerifyStatus" class="email-verify-status form-text"></p>
							</div>
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
<script src="<%=request.getContextPath()%>/js/auth.js"></script>

</body>
</html>