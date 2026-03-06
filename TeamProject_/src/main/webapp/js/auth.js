/*
  auth.js
  - 로그인/회원가입 화면 공통 UX
  - 비밀번호 보기/숨기기 토글
  - CapsLock 켜짐 안내

  기본 셀렉터(기본값)
  - password input: #pass
  - toggle button: #pwToggle
  - notice element: #capsNotice

  페이지에 따라 id가 다르면 data-*로 오버라이드 가능:
  - data-auth-pass="#pass2"
  - data-auth-toggle="#pwToggle2"
  - data-auth-caps="#capsNotice2"

  예) <body data-auth-pass="#pass" data-auth-toggle="#pwToggle" data-auth-caps="#capsNotice">
*/

(function(){
  function qs(sel){
    try { return document.querySelector(sel); } catch(e){ return null; }
  }

  function init(){
    var root = document.body || document.documentElement;
    var passSel = (root && root.getAttribute && root.getAttribute('data-auth-pass')) || '#pass';
    var toggleSel = (root && root.getAttribute && root.getAttribute('data-auth-toggle')) || '#pwToggle';
    var capsSel = (root && root.getAttribute && root.getAttribute('data-auth-caps')) || '#capsNotice';

    var passEl = qs(passSel);
    var toggleBtn = qs(toggleSel);
    var capsNotice = qs(capsSel);

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
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
