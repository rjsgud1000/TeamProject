<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Vo.MemberVO" %>
<%
	request.setCharacterEncoding("UTF-8");
	String contextPath = request.getContextPath();

	MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
	String loginId = (String) session.getAttribute("loginId");
	String loginName = (String) session.getAttribute("loginName"); // 현재는 닉네임으로 저장됨
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>마이페이지</title>

<link rel="stylesheet" href="<%=contextPath%>/css/mypage.css" />

</head>
<body>

<%-- NOTE: main.jsp가 Top.jsp를 포함하므로, center로 로드되는 mypage.jsp에서는 Top.jsp를 include하지 않습니다. --%>

<div class="page">
	<div class="container">
		<div class="card">
			<div class="hero">
				<h1>마이페이지</h1>
				<p>내 정보와 계정 상태를 확인할 수 있어요. (JSP 화면만 구성)</p>
			</div>

			<div class="body">
				<% if (loginId == null || loginMember == null) { %>
					<div class="empty">
						<h2>로그인이 필요해요</h2>
						<p>마이페이지는 로그인 후 이용할 수 있습니다.</p>
						<div class="actions">
							<a class="btn primary" href="<%=contextPath%>/member/login.me">로그인</a>
							<a class="btn" href="<%=contextPath%>/main.jsp">메인으로</a>
						</div>
					</div>
				<% } else {
					String nickname = loginMember.getNickname();
					String username = loginMember.getUsername();
					String status = loginMember.getStatus();
					String role = loginMember.getRole();
					String email = loginMember.getEmail();
					String phone = loginMember.getPhone();

					String displayName = (nickname != null && !nickname.isBlank()) ? nickname : (loginName != null ? loginName : loginId);
					String avatarText = (displayName != null && !displayName.isBlank()) ? displayName.substring(0, 1) : "?";

					String badgeText = (status != null && !status.isBlank()) ? status : "ACTIVE";
					String badgeClass = "";
					String badgeLabel = badgeText;
					if ("ACTIVE".equalsIgnoreCase(badgeText)) {
						badgeClass = "ok";
						badgeLabel = "정상";
					} else if ("INACTIVE".equalsIgnoreCase(badgeText)) {
						badgeClass = "warn";
						badgeLabel = "휴면";
					} else if ("BANNED".equalsIgnoreCase(badgeText)) {
						badgeClass = "bad";
						badgeLabel = "제재";
					} else {
						badgeClass = "bad";
					}

					String emailText = (email != null && !email.isBlank()) ? email : "(미등록)";
					String phoneText = (phone != null && !phone.isBlank()) ? phone : "(미등록)";
					String usernameText = (username != null && !username.isBlank()) ? username : "";
					String roleText = (role != null && !role.isBlank()) ? role : "";
				%>

					<div class="grid">
						<div class="profile">
							<div class="section-title">프로필</div>
							<div style="display:flex; gap:12px; align-items:center;">
								<div class="avatar"><%= avatarText %></div>
								<div class="kv">
									<div class="name"><%= displayName %></div>
									<div class="sub">@<%= loginId %> · <span class="badge <%=badgeClass%>"><%= badgeLabel %></span></div>
								</div>
							</div>

							<div class="actions">
								<a class="btn" href="<%=contextPath%>/member/logout.me">로그아웃</a>
								<a class="btn primary" href="#" onclick="alert('비밀번호 변경 기능은 추후 연결해주세요.'); return false;">비밀번호 변경</a>
								<a class="btn" href="#" onclick="alert('내 정보 수정 기능은 추후 연결해주세요.'); return false;">내 정보 수정</a>
							</div>

							<div class="note">

							</div>
						</div>

						<div>
							<div class="section-title">내 정보</div>
							<div class="list" aria-label="내 정보">
								<div class="row">
									<div class="key">닉네임</div>
									<div class="val"><%= displayName %></div>
								</div>
								<div class="row">
									<div class="key">이름</div>
									<div class="val"><%= usernameText %></div>
								</div>
								<div class="row">
									<div class="key">권한</div>
									<div class="val"><%= roleText %></div>
								</div>
								<div class="row">
									<div class="key">계정 상태</div>
									<div class="val"><%= badgeLabel %></div>
								</div>
								<div class="row">
									<div class="key">이메일</div>
									<div class="val"><%= emailText %></div>
								</div>
								<div class="row">
									<div class="key">휴대폰</div>
									<div class="val"><%= phoneText %></div>
								</div>
							</div>
							<div class="note">
								<span class="small">TIP</span><br>
								로그인 상태 표시는 상단바(Top.jsp)와 동일하게 세션 기준으로 동작합니다.
							</div>
						</div>
					</div>
				<% } %>
			</div>
		</div>
	</div>
</div>

</body>
</html>