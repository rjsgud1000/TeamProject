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
<link rel="stylesheet" href="${contextPath}/css/admin-member.css" />
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
				<dt>권한</dt><dd><span class="badge badge--${member.role}">${empty roleLabelMap[member.role] ? member.role : roleLabelMap[member.role]}</span></dd>
				<dt>상태</dt><dd><span class="badge badge--${member.status}">${empty statusLabelMap[member.status] ? member.status : statusLabelMap[member.status]}</span></dd>
				<dt>누적 경고</dt><dd>${member.warningCount}회</dd>
				<dt>누적 제재</dt><dd>${member.bannedCount}회</dd>
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
			<div class="notice" style="margin-top:16px;">
				<h2 style="margin:0 0 12px; font-size:18px;">회원 상태 변경</h2>
				<c:choose>
					<c:when test="${member.role eq 'ADMIN'}">
						<p style="margin:0; color:#666;">관리자 계정은 상태를 변경할 수 없습니다.</p>
					</c:when>
					<c:otherwise>
						<form method="post" action="${contextPath}/member/admin/updateStatus.me" onsubmit="return validateStatusForm();">
							<input type="hidden" name="memberId" value="${member.memberId}">
							<label for="status" style="display:block; margin-bottom:8px; font-weight:600;">변경 상태</label>
							<select id="status" name="status" style="width:100%; padding:10px; border-radius:10px; margin-bottom:12px;" onchange="toggleSanctionFields();">
								<option value="ACTIVE" <c:if test="${member.status eq 'ACTIVE'}">selected</c:if>>${statusLabelMap.ACTIVE}</option>
								<option value="WARNING" <c:if test="${member.status eq 'WARNING'}">selected</c:if>>${statusLabelMap.WARNING}</option>
								<option value="BANNED" <c:if test="${member.status eq 'BANNED'}">selected</c:if>>${statusLabelMap.BANNED}</option>
								<option value="WITHDRAWN" <c:if test="${member.status eq 'WITHDRAWN'}">selected</c:if>>${statusLabelMap.WITHDRAWN}</option>
							</select>
							<div id="sanctionFields">
								<label for="sanctionReason" style="display:block; margin-bottom:8px; font-weight:600;">경고/제재 사유</label>
								<textarea id="sanctionReason" name="sanctionReason" rows="4" placeholder="경고 또는 제재 처리 시 사유를 입력하세요." style="width:100%; padding:10px; border-radius:10px; margin-bottom:12px; resize:vertical;"></textarea>
								<label for="sanctionEndAt" style="display:block; margin-bottom:8px; font-weight:600;">종료 일시</label>
								<input type="datetime-local" id="sanctionEndAt" name="sanctionEndAt" style="width:100%; padding:10px; border-radius:10px; margin-bottom:12px;">
							</div>
							<p style="margin:0 0 12px; color:#666; font-size:13px;">시작일시는 저장 시점(NOW)으로 자동 처리됩니다.</p>
							<button class="btn" type="submit">상태 변경 저장</button>
						</form>
					</c:otherwise>
				</c:choose>
			</div>
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
<script>
function toggleSanctionFields() {
	var status = document.getElementById('status');
	var fields = document.getElementById('sanctionFields');
	if (!status || !fields) return;
	var show = status.value === 'WARNING' || status.value === 'BANNED';
	fields.style.display = show ? 'block' : 'none';
}
function validateStatusForm() {
	var status = document.getElementById('status').value;
	var reason = document.getElementById('sanctionReason').value.trim();
	var endAt = document.getElementById('sanctionEndAt').value;
	if ((status === 'WARNING' || status === 'BANNED') && !reason) {
		alert('경고 또는 제재 처리 시 사유를 입력해 주세요.');
		return false;
	}
	if ((status === 'WARNING' || status === 'BANNED') && !endAt) {
		alert('경고 또는 제재 종료일시를 입력해 주세요.');
		return false;
	}
	return confirm('회원 상태를 변경하시겠습니까?');
}
toggleSanctionFields();
</script>
</body>
</html>