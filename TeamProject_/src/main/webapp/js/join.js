// 간단한 클라이언트 측 유효성 검사
// join.jsp에서 joinContextPath 전역변수를 주입함

// join.jsp에서 선언한 플래그 사용
// var idDupOk = false;
// var nickDupOk = false;
var joinEmailVerified = false;
var joinEmailCodeSent = false;

function updateEmailVerifyChip(state, label) {
	var chip = document.getElementById('emailVerifyChip');
	if (!chip) return;
	chip.className = 'email-verify-chip';
	if (state === 'pending') chip.classList.add('is-pending');
	if (state === 'success') chip.classList.add('is-success');
	chip.textContent = label || '인증 전';
}

function syncEmailVerifyControls() {
	var sendBtn = document.getElementById('sendEmailCodeBtn');
	var verifyBtn = document.getElementById('verifyEmailCodeBtn');
	var codeInput = document.getElementById('emailVerificationCode');
	var emailInput = document.getElementById('email');

	if (sendBtn) {
		sendBtn.textContent = joinEmailVerified ? '인증 완료' : (joinEmailCodeSent ? '재발송' : '인증번호 받기');
		sendBtn.disabled = false;
	}
	if (verifyBtn) {
		verifyBtn.textContent = joinEmailVerified ? '인증 완료됨' : '이메일 인증';
		verifyBtn.disabled = joinEmailVerified;
	}
	if (codeInput) {
		codeInput.disabled = joinEmailVerified;
	}
	if (emailInput) {
		emailInput.readOnly = joinEmailVerified;
	}
}

function setEmailVerifiedState(verified, message, ok) {
	joinEmailVerified = !!verified;
	var hidden = document.getElementById('emailVerified');
	if (hidden) hidden.value = verified ? 'true' : 'false';
	var statusEl = document.getElementById('emailVerifyStatus');
	if (statusEl) {
		statusEl.textContent = message || '';
		statusEl.className = 'email-verify-status ' + (ok ? 'form-text text-success' : 'form-text text-danger');
	}
	if (verified) {
		updateEmailVerifyChip('success', '인증 완료');
	} else if (joinEmailCodeSent && ok) {
		updateEmailVerifyChip('pending', '인증 대기');
	} else {
		updateEmailVerifyChip('default', '인증 전');
	}
	syncEmailVerifyControls();
}

function resetEmailVerificationState(message) {
	joinEmailCodeSent = false;
	var codeInput = document.getElementById('emailVerificationCode');
	var emailInput = document.getElementById('email');
	if (codeInput) {
		codeInput.disabled = false;
		codeInput.value = '';
	}
	if (emailInput) {
		emailInput.readOnly = false;
	}
	setEmailVerifiedState(false, message || '이메일 인증을 진행해 주세요.', false);
}

function setMsg(el, msg, ok) {
	if (!el) return;
	el.textContent = msg || '';
	el.className = ok ? 'form-text text-success' : 'form-text text-danger';
}

function isValidEmail(email) {
	if (!email) return true; // 선택 입력
	return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidHp(hp) {
	if (!hp) return true; // 선택 입력
	return /^\d{10,11}$/.test(hp);
}

function checkDuplicateId() {
	var id = document.getElementById('id');
	var msgEl = document.getElementById('idDupMsg');
	idDupOk = false;

	if (!id || id.value.trim() === '') {
		setMsg(msgEl, '아이디를 입력해 주세요.', false);
		return;
	}

	var value = id.value.trim();
	fetch(joinContextPath + '/member/checkId.me?id=' + encodeURIComponent(value), {
		method: 'GET',
		headers: { 'Accept': 'application/json' }
	})
	.then(function (res) { return res.json(); })
	.then(function (data) {
		if (data && data.ok) {
			idDupOk = true;
			setMsg(msgEl, '사용 가능한 아이디입니다.', true);
		} else {
			idDupOk = false;
			setMsg(msgEl, (data && data.message) ? data.message : '이미 사용 중인 아이디입니다.', false);
		}
	})
	.catch(function () {
		idDupOk = false;
		setMsg(msgEl, '중복확인 중 오류가 발생했습니다.', false);
	});
}

function checkDuplicateNickname() {
	var nickname = document.getElementById('nickname');
	var msgEl = document.getElementById('nickDupMsg');
	nickDupOk = false;

	if (!nickname || nickname.value.trim() === '') {
		setMsg(msgEl, '닉네임을 입력해 주세요.', false);
		return;
	}

	var value = nickname.value.trim();
	fetch(joinContextPath + '/member/checkNickname.me?nickname=' + encodeURIComponent(value), {
		method: 'GET',
		headers: { 'Accept': 'application/json' }
	})
	.then(function (res) { return res.json(); })
	.then(function (data) {
		if (data && data.ok) {
			nickDupOk = true;
			setMsg(msgEl, '사용 가능한 닉네임입니다.', true);
		} else {
			nickDupOk = false;
			setMsg(msgEl, (data && data.message) ? data.message : '이미 사용 중인 닉네임입니다.', false);
		}
	})
	.catch(function () {
		nickDupOk = false;
		setMsg(msgEl, '중복확인 중 오류가 발생했습니다.', false);
	});
}

function sendJoinEmailCode() {
	var email = document.getElementById('email');
	var emailVal = email ? email.value.trim() : '';
	if (!isValidEmail(emailVal) || emailVal === '') {
		setMsg(document.getElementById('emailInput'), '올바른 이메일을 입력해 주세요.', false);
		setEmailVerifiedState(false, '이메일을 정확히 입력한 뒤 인증번호를 요청해 주세요.', false);
		email && email.focus();
		return;
	}

	joinEmailVerified = false;
	if (email) {
		email.readOnly = false;
	}
	var btn = document.getElementById('sendEmailCodeBtn');
	if (btn) {
		btn.disabled = true;
		btn.textContent = '발송 중...';
	}
	fetch(joinContextPath + '/member/sendJoinEmailCode.me', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
			'Accept': 'application/json'
		},
		body: 'email=' + encodeURIComponent(emailVal)
	})
	.then(function (res) { return res.json(); })
	.then(function (data) {
		if (data && data.ok) {
			joinEmailCodeSent = true;
			setMsg(document.getElementById('emailInput'), '', true);
			setEmailVerifiedState(false, data.message || '인증번호를 발송했습니다.', true);
		} else {
			joinEmailCodeSent = false;
			setEmailVerifiedState(false, (data && data.message) ? data.message : '인증번호 받기에 실패했습니다.', false);
		}
	})
	.catch(function () {
		joinEmailCodeSent = false;
		setEmailVerifiedState(false, '인증번호 받기 요청 중 오류가 발생했습니다.', false);
	})
	.finally(function () {
		if (btn) btn.disabled = false;
		syncEmailVerifyControls();
	});
}

function verifyJoinEmailCode() {
	var email = document.getElementById('email');
	var code = document.getElementById('emailVerificationCode');
	var emailVal = email ? email.value.trim() : '';
	var codeVal = code ? code.value.trim() : '';
	if (!joinEmailCodeSent) {
		setEmailVerifiedState(false, '먼저 인증번호 받기를 눌러 주세요.', false);
		return;
	}
	if (emailVal === '' || !isValidEmail(emailVal)) {
		setEmailVerifiedState(false, '올바른 이메일을 입력해 주세요.', false);
		email && email.focus();
		return;
	}
	if (codeVal === '') {
		setEmailVerifiedState(false, '인증번호를 입력해 주세요.', false);
		code && code.focus();
		return;
	}

	var btn = document.getElementById('verifyEmailCodeBtn');
	if (btn) {
		btn.disabled = true;
		btn.textContent = '확인 중...';
	}
	fetch(joinContextPath + '/member/verifyJoinEmailCode.me', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
			'Accept': 'application/json'
		},
		body: 'email=' + encodeURIComponent(emailVal) + '&verificationCode=' + encodeURIComponent(codeVal)
	})
	.then(function (res) { return res.json(); })
	.then(function (data) {
		if (data && data.ok) {
			setEmailVerifiedState(true, data.message || '이메일 인증이 완료되었습니다.', true);
		} else {
			setEmailVerifiedState(false, (data && data.message) ? data.message : '이메일 인증에 실패했습니다.', false);
		}
	})
	.catch(function () {
		setEmailVerifiedState(false, '이메일 인증 중 오류가 발생했습니다.', false);
	})
	.finally(function () {
		syncEmailVerifyControls();
	});
}

function markDirtyAndResetDupFlags() {
	var id = document.getElementById('id');
	var nickname = document.getElementById('nickname');
	var email = document.getElementById('email');
	if (id) {
		id.addEventListener('input', function () {
			idDupOk = false;
			setMsg(document.getElementById('idDupMsg'), '중복확인을 해주세요.', false);
		});
	}
	if (nickname) {
		nickname.addEventListener('input', function () {
			nickDupOk = false;
			setMsg(document.getElementById('nickDupMsg'), '중복확인을 해주세요.', false);
		});
	}
	if (email) {
		email.addEventListener('input', function () {
			resetEmailVerificationState('이메일이 변경되었습니다. 다시 인증해 주세요.');
		});
	}
}

function attachLiveFormatValidation() {
	var email = document.getElementById('email');
	var hp = document.getElementById('hp');

	if (email) {
		email.addEventListener('input', function () {
			var v = email.value.trim();
			var ok = isValidEmail(v);
			if (v === '') {
				setMsg(document.getElementById('emailInput'), '', true);
				return;
			}
			setMsg(document.getElementById('emailInput'), ok ? '' : '올바른 형식이 아닙니다.', ok);
		});
	}

	if (hp) {
		hp.addEventListener('input', function () {
			var v = hp.value.trim();
			var ok = isValidHp(v);
			if (v === '') {
				setMsg(document.getElementById('hpInput'), '', true);
				return;
			}
			setMsg(document.getElementById('hpInput'), ok ? '' : '올바른 형식이 아닙니다.', ok);
		});
	}
}

if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', function () {
			markDirtyAndResetDupFlags();
			attachLiveFormatValidation();
			resetEmailVerificationState('이메일 인증을 진행해 주세요.');
			syncEmailVerifyControls();
		});
} else {
	markDirtyAndResetDupFlags();
	attachLiveFormatValidation();
	resetEmailVerificationState('이메일 인증을 진행해 주세요.');
	syncEmailVerifyControls();
}

function check() {
	var agree = document.getElementById('agree');
	var id = document.getElementById('id');
	var pass = document.getElementById('pass');
	var nickname = document.getElementById('nickname');
	var email = document.getElementById('email');
	var hp = document.getElementById('hp');

	if (!agree || !agree.checked) {
		alert('약관에 동의해 주세요.');
		return false;
	}
	if (!id || id.value.trim() === '') {
		alert('아이디를 입력해 주세요.');
		id && id.focus();
		return false;
	}
	if (!pass || pass.value.trim() === '') {
		alert('비밀번호를 입력해 주세요.');
		pass && pass.focus();
		return false;
	}
	if (!nickname || nickname.value.trim() === '') {
		alert('닉네임을 입력해 주세요.');
		nickname && nickname.focus();
		return false;
	}

	if (!idDupOk) {
		alert('아이디 중복확인을 해주세요.');
		return false;
	}
	if (!nickDupOk) {
		alert('닉네임 중복확인을 해주세요.');
		return false;
	}

	var emailVal = email ? email.value.trim() : '';
	if (!isValidEmail(emailVal) || emailVal === '') {
		alert('이메일 형식이 올바르지 않습니다.');
		email && email.focus();
		return false;
	}
	if (!joinEmailVerified) {
		alert('이메일 인증을 완료해 주세요.');
		return false;
	}

	var hpVal = hp ? hp.value.trim() : '';
	if (!isValidHp(hpVal)) {
		alert('핸드폰 번호는 숫자만 10~11자리로 입력해 주세요.');
		hp && hp.focus();
		return false;
	}

	document.getElementById('joinForm').submit();
	return true;
}