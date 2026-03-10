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
							<input type="email" id="email" name="email" value="${member.email}" placeholder="example@domain.com">
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
		var nicknameInput = document.getElementById('nickname');
		var msgEl = document.getElementById('nickDupMsg');
		var form = document.getElementById('editProfileForm');
		var nickDupOk = true;

		function setMsg(msg, ok) {
			if (!msgEl) return;
			msgEl.textContent = msg || '';
			msgEl.className = ok ? 'form-text text-success' : 'form-text text-danger';
		}

		function trimmedNickname() {
			return nicknameInput ? nicknameInput.value.trim() : '';
		}

		function isOriginalNickname() {
			return trimmedNickname() === (originalNickname || '').trim();
		}

		function isValidEmail(email) {
			return !email || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
		}

		function isValidPhone(phone) {
			return !phone || /^\d{10,11}$/.test(phone);
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

		var btn = document.getElementById('checkNicknameBtn');
		if (btn) {
			btn.addEventListener('click', checkProfileNickname);
		}

		if (form) {
			form.addEventListener('submit', function(e) {
				var nick = trimmedNickname();
				var email = document.getElementById('email');
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
				if (email && !isValidEmail(email.value.trim())) {
					alert('이메일 형식이 올바르지 않습니다.');
					email.focus();
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