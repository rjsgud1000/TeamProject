// 간단한 클라이언트 측 유효성 검사
// join.jsp에서 joinContextPath 전역변수를 주입함

// join.jsp에서 선언한 플래그 사용
var idDupOk = false;
var nickDupOk = false;
var joinEmailVerified = false;
var joinEmailCodeSent = false;
var MEMBER_ID_REGEX = /^[A-Za-z0-9]{4,20}$/;
var PASSWORD_REGEX = /^(?=.*[A-Za-z])(?=.*\d).{6,}$/;

function setMsg(el, message, isOk) {
	if (!el) return;
	el.textContent = message || '';
	if (typeof isOk === 'boolean') {
		el.style.color = isOk ? '#198754' : '#dc3545';
	}
}

function isValidEmail(email) {
	if (!email) return false;
	return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidHp(hp) {
	if (!hp) return false;
	return /^\d{10,11}$/.test(hp);
}

function syncEmailVerifyControls() {
	var email = document.getElementById('email');
	var code = document.getElementById('emailVerificationCode');
	var verifyBtn = document.getElementById('verifyEmailCodeBtn');
	if (email) {
		email.readOnly = !!joinEmailVerified;
	}
	if (code) {
		code.disabled = !joinEmailCodeSent || joinEmailVerified;
	}
	if (verifyBtn) {
		verifyBtn.disabled = !joinEmailCodeSent || joinEmailVerified;
	}
}

function setEmailVerifiedState(verified, message, isOk) {
	joinEmailVerified = !!verified;
	var hidden = document.getElementById('emailVerified');
	var chip = document.getElementById('emailVerifyChip');
	if (hidden) {
		hidden.value = joinEmailVerified ? 'true' : 'false';
	}
	if (chip) {
		chip.textContent = joinEmailVerified ? '인증 완료' : '인증 전';
		chip.style.color = joinEmailVerified ? '#198754' : '';
	}
	setMsg(document.getElementById('emailVerifyStatus'), message || '', isOk);
	syncEmailVerifyControls();
}

function resetEmailVerificationState(message) {
	joinEmailVerified = false;
	joinEmailCodeSent = false;
	var code = document.getElementById('emailVerificationCode');
	if (code) {
		code.value = '';
	}
	var hidden = document.getElementById('emailVerified');
	if (hidden) {
		hidden.value = 'false';
	}
	setMsg(document.getElementById('emailVerifyStatus'), message || '', false);
	var chip = document.getElementById('emailVerifyChip');
	if (chip) {
		chip.textContent = '인증 전';
		chip.style.color = '';
	}
	syncEmailVerifyControls();
}

function isValidMemberId(id) {
	if (!id) return false;
	return MEMBER_ID_REGEX.test(id);
}

function isValidPassword(password) {
	if (!password) return false;
	return PASSWORD_REGEX.test(password);
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
	if (!isValidMemberId(value)) {
		setMsg(msgEl, '아이디는 영문과 숫자만 사용 가능하며 4~20자로 입력해 주세요.', false);
		return;
	}

	fetch(joinContextPath + '/member/checkId.me?id=' + encodeURIComponent(value), {
		method: 'GET',
		headers: { 'Accept': 'application/json' }
	})
	.then(function (res) {
		if (!res.ok) {
			throw new Error('HTTP ' + res.status);
		}
		return res.json();
	})
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
	.then(function (res) {
		if (!res.ok) {
			throw new Error('HTTP ' + res.status);
		}
		return res.json();
	})
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
	var id = document.getElementById('id');
	var pass = document.getElementById('pass');
	var email = document.getElementById('email');
	var hp = document.getElementById('hp');

	if (id) {
		id.addEventListener('input', function () {
			var v = id.value.trim();
			if (v === '') {
				setMsg(document.getElementById('idInput'), '', true);
				return;
			}
			setMsg(document.getElementById('idInput'), isValidMemberId(v) ? '' : '아이디는 영문과 숫자만 사용 가능하며 4~20자로 입력해 주세요.', isValidMemberId(v));
		});
	}

	if (pass) {
		pass.addEventListener('input', function () {
			var v = pass.value;
			if (v === '') {
				setMsg(document.getElementById('passInput'), '', true);
				return;
			}
			setMsg(document.getElementById('passInput'), isValidPassword(v) ? '' : '비밀번호는 영문과 숫자를 포함해 6자 이상 입력해 주세요.', isValidPassword(v));
		});
	}

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
	if (!isValidMemberId(id.value.trim())) {
		alert('아이디는 영문과 숫자만 사용 가능하며 4~20자로 입력해 주세요.');
		id && id.focus();
		return false;
	}
	if (!pass || pass.value.trim() === '') {
		alert('비밀번호를 입력해 주세요.');
		pass && pass.focus();
		return false;
	}
	if (!isValidPassword(pass.value)) {
		alert('비밀번호는 영문과 숫자를 포함해 6자 이상 입력해 주세요.');
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