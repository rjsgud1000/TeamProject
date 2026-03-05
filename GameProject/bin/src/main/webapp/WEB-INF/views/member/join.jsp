<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
  /WEB-INF/views/member/join.jsp

  [역할]
  - 회원가입 화면을 렌더링하고, 입력값에 대해 UX 차원의 즉시 검증(중복체크/AJAX, 이메일 형식)을 제공합니다.

  [입력/출력]
  - form action: /member/join.me (POST)
  - 파라미터: loginId, password, nickname, email

  [의도/주의]
  - 여기의 JS/HTML5 검증은 "사용자 경험(UX)" 개선용입니다.
  - 최종 검증(우회 방지)은 반드시 서버(MemberService.register)에서 수행합니다.

  [사용하는 request attribute]
  - error: 가입 실패 시(검증 실패) 컨트롤러가 설정하는 메시지
--%>

<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<h2>회원가입</h2>

<% String error = (String)request.getAttribute("error"); %>
<% if(error != null){ %>
  <p style="color:red;"><%=error%></p>
<% } %>

<form method="post" action="<%=request.getContextPath()%>/member/join.me" onsubmit="return window.gcJoinValidate && window.gcJoinValidate();">
  <div>
    <label>아이디</label>
    <input type="text" id="loginId" name="loginId" required>
    <small id="loginIdMsg"></small>
  </div>
  <div>
    <label>비밀번호</label>
    <input type="password" name="password" required>
  </div>
  <div>
    <label>닉네임</label>
    <input type="text" id="nickname" name="nickname" required>
    <small id="nicknameMsg"></small>
  </div>
  <div>
    <label>이메일</label>
    <input type="email" id="email" name="email" required
           pattern="[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+"
           title="example@domain.com 형식으로 입력하세요.">
    <small id="emailMsg"></small>
  </div>
  <button type="submit">가입</button>
</form>

<script>
(function(){
  var ctx = '<%=request.getContextPath()%>';
  var loginId = document.getElementById('loginId');
  var nickname = document.getElementById('nickname');
  var email = document.getElementById('email');
  var loginIdMsg = document.getElementById('loginIdMsg');
  var nicknameMsg = document.getElementById('nicknameMsg');
  var emailMsg = document.getElementById('emailMsg');

  function setMsg(el, ok, text){
    el.textContent = text || '';
    el.style.color = ok ? 'green' : 'red';
  }

  function isValidEmailFormat(v){
    if(!v) return false;
    v = v.trim();
    if(v.length > 320) return false;
    // 서버와 비슷한 수준의 형식 검증(UX용)
    var re = /^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$/;
    return re.test(v);
  }

  // [중복 체크 API 호출]
  // - /member/checkLoginId.me?loginId=
  // - /member/checkNickname.me?nickname=
  // - /member/checkEmail.me?email=
  // 응답은 공통적으로 {"ok": boolean}
  async function check(url, param){
    try{
      var res = await fetch(url + '?' + new URLSearchParams(param), {method:'GET'});
      return await res.json();
    }catch(e){
      return {ok:false};
    }
  }

  loginId.addEventListener('blur', async function(){
    if(!loginId.value.trim()) { setMsg(loginIdMsg, false, '아이디를 입력하세요.'); return; }
    var data = await check(ctx + '/member/checkLoginId.me', {loginId: loginId.value.trim()});
    setMsg(loginIdMsg, data.ok, data.ok ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.');
  });

  nickname.addEventListener('blur', async function(){
    if(!nickname.value.trim()) { setMsg(nicknameMsg, false, '닉네임을 입력하세요.'); return; }
    var data = await check(ctx + '/member/checkNickname.me', {nickname: nickname.value.trim()});
    setMsg(nicknameMsg, data.ok, data.ok ? '사용 가능한 닉네임입니다.' : '이미 사용 중인 닉네임입니다.');
  });

  email.addEventListener('blur', async function(){
    if(!email.value.trim()) { setMsg(emailMsg, false, '이메일을 입력하세요.'); return; }
    if(!isValidEmailFormat(email.value)) { setMsg(emailMsg, false, '이메일 형식이 올바르지 않습니다.'); return; }
    var data = await check(ctx + '/member/checkEmail.me', {email: email.value.trim()});
    setMsg(emailMsg, data.ok, data.ok ? '사용 가능한 이메일입니다.' : '이미 사용 중인 이메일입니다.');
  });

  window.gcJoinValidate = function(){
    // submit 직전 최종 UX 검증
    // - 브라우저 기본 validation(required/pattern) + 우리가 만든 메시지 상태를 함께 사용
    // 중복체크 결과(빨간 메시지면 제출 차단)
    if(loginIdMsg.style.color === 'red' || nicknameMsg.style.color === 'red' || emailMsg.style.color === 'red') return false;

    // 브라우저 기본 HTML5 validation도 같이 유도
    if(!loginId.value.trim()) { setMsg(loginIdMsg, false, '아이디를 입력하세요.'); loginId.focus(); return false; }
    if(!nickname.value.trim()) { setMsg(nicknameMsg, false, '닉네임을 입력하세요.'); nickname.focus(); return false; }

    if(!email.value.trim()) { setMsg(emailMsg, false, '이메일을 입력하세요.'); email.focus(); return false; }
    if(!isValidEmailFormat(email.value)) { setMsg(emailMsg, false, '이메일 형식이 올바르지 않습니다.'); email.focus(); return false; }

    return true;
  }
})();
</script>

<p>
  <a href="<%=request.getContextPath()%>/member/loginForm.me">로그인</a>
</p>
<jsp:include page="/WEB-INF/views/layout/footer.jsp" />