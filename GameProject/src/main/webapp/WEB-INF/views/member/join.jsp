<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    <input type="email" id="email" name="email" required>
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
    var data = await check(ctx + '/member/checkEmail.me', {email: email.value.trim()});
    setMsg(emailMsg, data.ok, data.ok ? '사용 가능한 이메일입니다.' : '이미 사용 중인 이메일입니다.');
  });

  window.gcJoinValidate = function(){
    // 서버에서 최종검증하지만, UX용으로 간단히 막기
    if(loginIdMsg.style.color === 'red' || nicknameMsg.style.color === 'red' || emailMsg.style.color === 'red') return false;
    return true;
  }
})();
</script>

<p>
  <a href="<%=request.getContextPath()%>/member/loginForm.me">로그인</a>
</p>
<jsp:include page="/WEB-INF/views/layout/footer.jsp" />