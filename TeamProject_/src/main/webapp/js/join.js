// 간단한 클라이언트 측 유효성 검사
// join.jsp에서 joinContextPath 전역변수를 주입함

// join.jsp에서 선언한 플래그 사용
// var idDupOk = false;
// var nickDupOk = false;

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

function markDirtyAndResetDupFlags() {
	var id = document.getElementById('id');
	var nickname = document.getElementById('nickname');
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
}

function attachLiveFormatValidation() {
	var email = document.getElementById('email');
	var hp = document.getElementById('hp');

	if (email) {
		email.addEventListener('input', function () {
			var v = email.value.trim();
			var ok = isValidEmail(v);
			// 비어있으면 메시지 없음(선택 입력)
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

// DOM 로드 후 이벤트 부착
if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', function () {
		markDirtyAndResetDupFlags();
		attachLiveFormatValidation();
	});
} else {
	markDirtyAndResetDupFlags();
	attachLiveFormatValidation();
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
	if (!isValidEmail(emailVal)) {
		alert('이메일 형식이 올바르지 않습니다.');
		email && email.focus();
		return false;
	}

	var hpVal = hp ? hp.value.trim() : '';
	if (!isValidHp(hpVal)) {
		alert('핸드폰 번호는 숫자만 10~11자리로 입력해 주세요.');
		hp && hp.focus();
		return false;
	}

	document.forms[0].submit();
	return true;
}