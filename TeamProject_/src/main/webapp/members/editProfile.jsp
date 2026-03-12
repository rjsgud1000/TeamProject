<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="member" value="${requestScope.memberDetail}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원정보 수정</title>
<link rel="stylesheet" href="${contextPath}/css/auth.css" />
<style>
.email-verify-card{
	margin-top:10px;
	padding:16px;
	border:1px solid #dbe7f8;
	border-radius:16px;
	background:linear-gradient(180deg, #f8fbff 0%, #fdfefe 100%);
	box-shadow:0 10px 24px rgba(37, 99, 235, .06);
}
.email-verify-head{
	display:flex;
	justify-content:space-between;
	align-items:center;
	gap:12px;
	margin-bottom:10px;
	flex-wrap:wrap;
}
.email-verify-title{
	display:flex;
	align-items:center;
	gap:8px;
	font-weight:900;
	font-size:14px;
	color:#0f172a;
}
.email-verify-step{
	display:inline-flex;
	align-items:center;
	justify-content:center;
	min-width:28px;
	height:28px;
	padding:0 8px;
	border-radius:999px;
	background:#dbeafe;
	color:#1d4ed8;
	font-size:12px;
	font-weight:900;
}
.email-verify-chip{
	display:inline-flex;
	align-items:center;
	gap:6px;
	padding:7px 10px;
	border-radius:999px;
	background:#eef2f7;
	color:#475569;
	font-size:12px;
	font-weight:900;
}
.email-verify-chip.is-success{
	background:#dcfce7;
	color:#15803d;
}
.email-verify-chip.is-pending{
	background:#dbeafe;
	color:#1d4ed8;
}
.email-verify-copy{
	font-size:12px;
	color:#64748b;
	line-height:1.5;
	margin-bottom:12px;
}
.email-verify-wrap{
	display:grid;
	gap:12px;
}
.email-verify-row{
	display:grid;
	grid-template-columns: 1fr 140px;
	gap:10px;
}
.email-verify-status{
	font-size:12px;
	font-weight:700;
	margin-top:2px;
}
@media (max-width: 640px){
	.email-verify-row{
		grid-template-columns:1fr;
	}
}
</style>
</head>
<body>
<div class="container">
	<div class="join-card">
		<div class="join-hero">
			<div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; flex-wrap:wrap;">
				<div>
					<span class="step">MY PAGE</span>
					<h1>회원정보 수정</h1>
					<p>닉네임, 연락처, 주소를 수정하고 비밀번호도 함께 변경할 수 있어요.</p>
				</div>
				<div class="hero-right">
					<a href="${contextPath}/member/mypage.me">마이페이지로 돌아가기</a>
				</div>
			</div>
		</div>

		<div class="join-body">
			<c:if test="${not empty requestScope.profileError}">
				<div class="alert" role="alert">${requestScope.profileError}</div>
			</c:if>

			<form action="${contextPath}/member/updateProfile.me" method="post" id="editProfileForm">
				<input type="hidden" id="profileEmailVerified" name="profileEmailVerified" value="false">
				<div class="section-title">기본 정보</div>
				<div class="grid">
					<div>
						<div class="field">
							<label class="label">아이디</label>
							<input type="text" value="${member.memberId}" readonly>
						</div>
					</div>
					<div>
						<div class="field">
							<label class="label">이름</label>
							<input type="text" value="${member.username}" readonly>
						</div>
					</div>
				</div>

				<div class="grid">
					<div>
						<div class="field">
							<label class="label" for="nickname">닉네임 <span class="req">*</span></label>
							<div class="input-row">
								<input type="text" id="nickname" name="nickname" value="${member.nickname}" required>
								<button class="btn btn-outline-primary" type="button" id="checkNicknameBtn">중복확인</button>
							</div>
							<small id="nickDupMsg" class="form-text"></small>
						</div>
					</div>
					<div>
						<div class="field">
							<label class="label" for="newPassword">새 비밀번호</label>
							<input type="password" id="newPassword" name="newPassword" placeholder="변경할 때만 입력하세요">
							<div class="help-hint">입력한 경우에만 비밀번호가 변경됩니다. 4자 이상 입력해 주세요.</div>
						</div>
					</div>
				</div>

				<div class="field">
					<label class="label" for="zipcode">주소</label>
					<div class="grid" style="grid-template-columns: 1fr 140px; align-items: stretch;">
						<input type="text" id="sample4_postcode" name="zipcode" value="${member.zipcode}" placeholder="우편번호" readonly>
						<input type="button" onclick="sample4_execDaumPostcode()" value="찾기" class="btn btn-outline-primary btn-block" style="height:44px;">
					</div>
					<div class="field" style="margin-top:10px;">
						<input type="text" id="sample4_roadAddress" name="addr1" value="${member.addr1}" placeholder="도로명주소" readonly>
					</div>
					<div class="field" style="margin-top:10px;">
						<input type="text" id="sample4_jibunAddress" name="addr2" value="${member.addr2}" placeholder="지번주소" readonly>
					</div>
					<div class="field" style="margin-top:10px;">
						<input type="text" id="sample4_detailAddress" name="addr3" value="${member.addr3}" placeholder="상세주소">
					</div>
					<div class="field" style="margin-top:10px;">
						<input type="text" id="sample4_extraAddress" name="addr4" value="${member.addr4}" placeholder="참고항목" readonly>
					</div>
				</div>

				<div class="grid">
					<div>
						<div class="field">
							<div class="label">성별</div>
							<div class="radio-group">
								<label class="radio" for="genderMan"><input type="radio" name="gender" id="genderMan" value="man" ${member.gender eq 'man' ? 'checked' : ''}> 남성</label>
								<label class="radio" for="genderWoman"><input type="radio" name="gender" id="genderWoman" value="woman" ${member.gender eq 'woman' ? 'checked' : ''}> 여성</label>
							</div>
						</div>
					</div>
				</div>

				<div class="section-title">연락처</div>
				<div class="grid">
					<div>
						<div class="field">
							<label class="label" for="email">Email</label>
							<div class="email-verify-card">
								<div class="email-verify-head">
									<div class="email-verify-title">
										<span class="email-verify-step">1</span>
										<span>이메일 인증</span>
									</div>
									<div id="emailVerifyChip" class="email-verify-chip">변경 없음</div>
								</div>
								<div class="email-verify-copy">이메일을 변경하면 인증번호를 받아 인증을 완료해야 저장할 수 있습니다.</div>
								<div class="email-verify-wrap">
									<div class="input-row">
										<input type="email" id="email" name="email" value="${member.email}" placeholder="example@domain.com">
										<button class="btn btn-outline-primary" type="button" id="sendProfileEmailCodeBtn">인증번호 받기</button>
									</div>
									<div class="email-verify-row">
										<input type="text" id="emailVerificationCode" placeholder="메일로 받은 인증번호를 입력해 주세요">
										<button class="btn btn-outline-primary" type="button" id="verifyProfileEmailCodeBtn">이메일 인증</button>
									</div>
								</div>
								<p id="emailVerifyStatus" class="email-verify-status form-text"></p>
							</div>
						</div>
					</div>
					<div>
						<div class="field">
							<label class="label" for="phone">핸드폰번호</label>
							<input type="text" id="phone" name="phone" value="${member.phone}" placeholder="'-' 없이 숫자만 입력 (10~11자리)">
						</div>
					</div>
				</div>

				<div class="actions">
					<a href="${contextPath}/member/mypage.me" class="btn btn-outline btn-block">취소</a>
					<button type="submit" class="btn btn-primary btn-block">저장하기</button>
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
				document.getElementById('sample4_roadAddress').value = roadAddr;
				document.getElementById('sample4_jibunAddress').value = data.jibunAddress;
				document.getElementById('sample4_extraAddress').value = roadAddr !== '' ? extraRoadAddr : '';
			}
		}).open();
	}

	(function() {
		var contextPath = '${contextPath}';
		var originalNickname = '${member.nickname}';
		var originalEmail = '${member.email}';
		var nicknameInput = document.getElementById('nickname');
		var emailInput = document.getElementById('email');
		var emailCodeInput = document.getElementById('emailVerificationCode');
		var emailVerifyStatus = document.getElementById('emailVerifyStatus');
		var emailVerifyChip = document.getElementById('emailVerifyChip');
		var emailVerifiedField = document.getElementById('profileEmailVerified');
		var sendEmailCodeBtn = document.getElementById('sendProfileEmailCodeBtn');
		var verifyEmailCodeBtn = document.getElementById('verifyProfileEmailCodeBtn');
		var msgEl = document.getElementById('nickDupMsg');
		var form = document.getElementById('editProfileForm');
		var nickDupOk = true;
		var emailVerified = false;

		function setMsg(msg, ok) {
			if (!msgEl) return;
			msgEl.textContent = msg || '';
			msgEl.className = ok ? 'form-text text-success' : 'form-text text-danger';
		}

		function setEmailStatus(msg, ok) {
			if (!emailVerifyStatus) return;
			emailVerifyStatus.textContent = msg || '';
			emailVerifyStatus.className = ok ? 'email-verify-status form-text text-success' : 'email-verify-status form-text text-danger';
		}

		function updateEmailChip(text, state) {
			if (!emailVerifyChip) return;
			emailVerifyChip.textContent = text;
			emailVerifyChip.className = 'email-verify-chip';
			if (state === 'success') {
				emailVerifyChip.classList.add('is-success');
			} else if (state === 'pending') {
				emailVerifyChip.classList.add('is-pending');
			}
		}

		function trimmedNickname() {
			return nicknameInput ? nicknameInput.value.trim() : '';
		}

		function trimmedEmail() {
			return emailInput ? emailInput.value.trim() : '';
		}

		function isOriginalNickname() {
			return trimmedNickname() === (originalNickname || '').trim();
		}

		function isOriginalEmail() {
			return trimmedEmail() === (originalEmail || '').trim();
		}

		function isValidEmail(email) {
			return !email || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
		}

		function isValidPhone(phone) {
			return !phone || /^\d{10,11}$/.test(phone);
		}

		function resetEmailVerificationState(silent) {
			emailVerified = false;
			if (emailVerifiedField) {
				emailVerifiedField.value = 'false';
			}
			if (emailCodeInput) {
				emailCodeInput.value = '';
			}
			if (isOriginalEmail()) {
				updateEmailChip('변경 없음');
				if (!silent) {
					setEmailStatus('현재 이메일을 유지하면 추가 인증 없이 저장할 수 있습니다.', true);
				}
			} else {
				updateEmailChip('인증 전', 'pending');
				if (!silent) {
					setEmailStatus('이메일을 변경했습니다. 인증번호를 받아 인증을 완료해 주세요.', false);
				}
			}
		}

		function markEmailVerified() {
			emailVerified = true;
			if (emailVerifiedField) {
				emailVerifiedField.value = 'true';
			}
			updateEmailChip('인증 완료', 'success');
			setEmailStatus('이메일 인증이 완료되었습니다. 저장할 수 있습니다.', true);
		}

		function checkProfileNickname() {
			var value = trimmedNickname();
			if (!value) {
				nickDupOk = false;
				setMsg('닉네임을 입력해 주세요.', false);
				return;
			}
			if (isOriginalNickname()) {
				nickDupOk = true;
				setMsg('현재 사용 중인 닉네임입니다.', true);
				return;
			}

			fetch(contextPath + '/member/checkProfileNickname.me?nickname=' + encodeURIComponent(value), {
				method: 'GET',
				headers: { 'Accept': 'application/json' }
			})
			.then(function(res) { return res.json(); })
			.then(function(data) {
				if (data && data.ok) {
					nickDupOk = true;
					setMsg('사용 가능한 닉네임입니다.', true);
				} else {
					nickDupOk = false;
					setMsg((data && data.message) ? data.message : '이미 사용 중인 닉네임입니다.', false);
				}
			})
			.catch(function() {
				nickDupOk = false;
				setMsg('중복확인 중 오류가 발생했습니다.', false);
			});
		}

		function sendProfileEmailCode() {
			var email = trimmedEmail();
			if (!email) {
				setEmailStatus('이메일을 입력해 주세요.', false);
				emailInput && emailInput.focus();
				return;
			}
			if (!isValidEmail(email)) {
				setEmailStatus('이메일 형식이 올바르지 않습니다.', false);
				emailInput && emailInput.focus();
				return;
			}
			if (isOriginalEmail()) {
				setEmailStatus('현재 이메일과 동일합니다. 이메일을 변경한 경우에만 인증이 필요합니다.', false);
				return;
			}

			resetEmailVerificationState(true);
			fetch(contextPath + '/member/sendProfileEmailCode.me', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
					'Accept': 'application/json'
				},
				body: 'email=' + encodeURIComponent(email)
			})
			.then(function(res) { return res.json(); })
			.then(function(data) {
				if (data && data.ok) {
					updateEmailChip('인증 대기', 'pending');
					setEmailStatus(data.message || '인증번호를 메일로 발송했습니다.', true);
				} else {
					setEmailStatus((data && data.message) ? data.message : '인증번호 발송에 실패했습니다.', false);
				}
			})
			.catch(function() {
				setEmailStatus('인증번호 발송 중 오류가 발생했습니다.', false);
			});
		}

		function verifyProfileEmailCode() {
			var email = trimmedEmail();
			var code = emailCodeInput ? emailCodeInput.value.trim() : '';
			if (!email) {
				setEmailStatus('이메일을 입력해 주세요.', false);
				emailInput && emailInput.focus();
				return;
			}
			if (!code) {
				setEmailStatus('인증번호를 입력해 주세요.', false);
				emailCodeInput && emailCodeInput.focus();
				return;
			}

			fetch(contextPath + '/member/verifyProfileEmailCode.me', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
					'Accept': 'application/json'
				},
				body: 'email=' + encodeURIComponent(email) + '&verificationCode=' + encodeURIComponent(code)
			})
			.then(function(res) { return res.json(); })
			.then(function(data) {
				if (data && data.ok) {
					markEmailVerified();
				} else {
					emailVerified = false;
					if (emailVerifiedField) {
						emailVerifiedField.value = 'false';
					}
					updateEmailChip('인증 전', 'pending');
					setEmailStatus((data && data.message) ? data.message : '이메일 인증에 실패했습니다.', false);
				}
			})
			.catch(function() {
				setEmailStatus('이메일 인증 중 오류가 발생했습니다.', false);
			});
		}

		if (nicknameInput) {
			setMsg('닉네임을 변경하면 중복확인을 다시 해주세요.', true);
			nicknameInput.addEventListener('input', function() {
				if (isOriginalNickname()) {
					nickDupOk = true;
					setMsg('현재 사용 중인 닉네임입니다.', true);
				} else {
					nickDupOk = false;
					setMsg('변경한 닉네임은 중복확인을 해주세요.', false);
				}
			});
		}

		if (emailInput) {
			resetEmailVerificationState(false);
			emailInput.addEventListener('input', function() {
				resetEmailVerificationState(false);
			});
		}

		var btn = document.getElementById('checkNicknameBtn');
		if (btn) {
			btn.addEventListener('click', checkProfileNickname);
		}
		if (sendEmailCodeBtn) {
			sendEmailCodeBtn.addEventListener('click', sendProfileEmailCode);
		}
		if (verifyEmailCodeBtn) {
			verifyEmailCodeBtn.addEventListener('click', verifyProfileEmailCode);
		}

		if (form) {
			form.addEventListener('submit', function(e) {
				var nick = trimmedNickname();
				var email = trimmedEmail();
				var phone = document.getElementById('phone');
				var newPassword = document.getElementById('newPassword');

				if (!nick) {
					alert('닉네임을 입력해 주세요.');
					nicknameInput && nicknameInput.focus();
					e.preventDefault();
					return;
				}
				if (!isOriginalNickname() && !nickDupOk) {
					alert('닉네임 중복확인을 해주세요.');
					nicknameInput && nicknameInput.focus();
					e.preventDefault();
					return;
				}
				if (emailInput && !isValidEmail(email)) {
					alert('이메일 형식이 올바르지 않습니다.');
					emailInput.focus();
					e.preventDefault();
					return;
				}
				if (!isOriginalEmail() && !emailVerified) {
					alert('이메일을 변경한 경우 이메일 인증을 완료해 주세요.');
					emailInput && emailInput.focus();
					e.preventDefault();
					return;
				}
				if (phone && !isValidPhone(phone.value.trim())) {
					alert('핸드폰 번호는 숫자만 10~11자리로 입력해 주세요.');
					phone.focus();
					e.preventDefault();
					return;
				}
				if (newPassword && newPassword.value.trim() !== '' && newPassword.value.trim().length < 4) {
					alert('새 비밀번호는 4자 이상 입력해 주세요.');
					newPassword.focus();
					e.preventDefault();
				}
			});
		}
	})();
</script>
</body>
</html>
