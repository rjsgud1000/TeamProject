<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
  Compatibility shim:
  기존 컨트롤러(Controller.MemberController)가 /CarMain.jsp 로 forward 하도록 되어 있어
  실제 메인 화면(main.jsp)을 호출하도록 연결합니다.
--%>
<jsp:forward page="main.jsp"/>
