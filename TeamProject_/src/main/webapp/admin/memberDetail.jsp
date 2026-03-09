<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="member" value="${requestScope.memberDetail}" />
<c:set var="flash" value="${sessionScope.adminMemberFlash}" />
<c:if test="${not empty flash}">
	<c:remove var="adminMemberFlash" scope="session" />
	<script>alert("${flash}");</script>
</c:if>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 상세관리</title>
<style>
.detail-wrap{max-width:1080px;margin:0 auto;padding:8px 0 24px;}
.detail-head{display:flex;justify-content:space-between;gap:16px;align-items:end;flex-wrap:wrap;margin-bottom:18px;}
.detail-head h1{margin:0;font-size:30px;}.detail-head p{margin:8px 0 0;color:#64748b;font-weight:700;}
.card{background:#fff;border:1px solid #e2e8f0;border-radius:22px;box-shadow:0 14px 34px rgba(15,23,42,.08);padding:22px;margin-bottom:18px;}
.summary{display:grid;grid-template-columns:1.1fr .9fr;gap:18px;}.meta{display:grid;grid-template-columns:140px 1fr;gap:12px 14px;align-items:center;}.meta dt{font-weight:900;color:#334155;}.meta dd{margin:0;color:#0f172a;word-break:break-all;}
.badge{display:inline-flex;align-items:center;justify-content:center;padding:6px 10px;border-radius:999px;font-size:12px;font-weight:900;}
.badge--ACTIVE{background:#dcfce7;color:#166534;}.badge--INACTIVE{background:#fef3c7;color:#92400e;}.badge--BANNED{background:#fee2e2;color:#b91c1c;}.badge--WITHDRAWN{background:#e2e8f0;color:#475569;}.badge--ADMIN{background:#dbeafe;color:#1d4ed8;}.badge--USER{background:#f1f5f9;color:#334155;}
.status-form{display:grid;gap:12px;align-content:start;}.status-form label{font-size:13px;font-weight:900;color:#334155;}.status-form select{height:44px;border:1px solid #cbd5e1;border-radius:12px;padding:0 14px;}
.btn{height:44px;padding:0 18px;border-radius:12px;border:1px solid #2563eb;background:#2563eb;color:#fff;font-weight:900;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;text-decoration:none;}.btn--ghost{background:#fff;color:#334155;border-color:#cbd5e1;}
.notice{margin-top:12px;padding:14px 16px;border-radius:16px;background:#fff7ed;border:1px solid #fed7aa;color:#9a3412;font-weight:700;}
.address{white-space:pre-line;line-height:1.7;}.actions{display:flex;gap:10px;flex-wrap:wrap;margin-top:10px;}
@media (max-width:860px){.summary{grid-template-columns:1fr;}.meta{grid-template-columns:1fr;}}
</style>
</head>
<body>
<div class="detail-wrap">
	<div class="detail-head">
		<div>
			<h1>회원 상세관리</h1>
			<p><strong>${member.memberId}</strong> 회원의 상세 정보와 상태를 관리합니다.</p>
		</div>
		<div class="actions">
			<a class="btn btn--ghost" href="${contextPath}/member/admin/list.me">목록으로</a>
			<a class="btn btn--ghost" href="${contextPath}/main.jsp">메인으로</a>
		</div>
	</div>

	<div class="card summary">
		<div>
			<dl class="meta">
				<dt>아이디</dt><dd>${member.memberId}</dd>
				<dt>이름</dt><dd>${member.username}</dd>
				<dt>닉네임</dt><dd>${member.nickname}</dd>
				<dt>권한</dt><dd><span class="badge badge--${member.role}">${member.role}</span></dd>
				<dt>상태</dt><dd><span class="badge badge--${member.status}">${member.status}</span></dd>
				<dt>이메일</dt><dd><c:out value="${empty member.email ? '-' : member.email}" /></dd>
				<dt>휴대폰</dt><dd><c:out value="${empty member.phone ? '-' : member.phone}" /></dd>
				<dt>가입일</dt><dd>${memberCreatedAtText}</dd>
				<dt>수정일</dt><dd>${memberUpdatedAtText}</dd>
			</dl>
		</div>
		<div>
			<c:if test="${not empty member.sanctionReason or not empty member.sanctionEndAt}">
				<div class="notice">
					<div>최근 제재 사유: <strong><c:out value="${empty member.sanctionReason ? '사유 없음' : member.sanctionReason}" /></strong></div>
					<div>최근 제재 종료일: <strong>${memberSanctionEndAtText}</strong></div>
				</div>
			</c:if>
		</div>
	</div>

	<div class="card">
		<h2 style="margin-top:0;">주소 정보</h2>
		<div class="address">우편번호: <c:out value="${empty member.zipcode ? '-' : member.zipcode}" />
도로명주소: <c:out value="${empty member.addr1 ? '-' : member.addr1}" />
지번주소: <c:out value="${empty member.addr2 ? '-' : member.addr2}" />
상세주소: <c:out value="${empty member.addr3 ? '-' : member.addr3}" />
참고항목: <c:out value="${empty member.addr4 ? '-' : member.addr4}" /></div>
	</div>
</div>
</body>
</html>