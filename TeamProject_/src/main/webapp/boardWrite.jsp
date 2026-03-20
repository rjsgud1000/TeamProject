<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="board-form-page">
	<div class="form-container">
		<h2 class="form-title">
			<c:choose>
				<c:when test="${isAnswerWrite}">답변 작성</c:when>
				<c:otherwise>게시글 작성</c:otherwise>
			</c:choose>
		</h2>

		<c:if test="${not empty errorMessage}">
			<div class="form-error">${errorMessage}</div>
		</c:if>

		<form action="${pageContext.request.contextPath}/board/write"
			method="post">
			<input type="hidden" name="category" value="${category}" /> <input
				type="hidden" name="parentPostId" value="${parentPostId}" />

			<div class="form-card">
				<table class="form-table">
					<tr>
						<th>카테고리</th>
						<td><c:choose>
								<c:when test="${category == 0}">공지사항</c:when>
								<c:when test="${category == 1}">자유 게시판</c:when>
								<c:when test="${category == 2}">
									<c:choose>
										<c:when test="${isAnswerWrite}">질문과 답변 - 답변글</c:when>
										<c:otherwise>질문과 답변 - 질문글</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${category == 3}">파티원 모집</c:when>
								<c:otherwise>게시판</c:otherwise>
							</c:choose></td>
					</tr>

					<c:if test="${category == 3}">
						<tr>
							<th>모집 설정</th>
							<td>
								<div class="form-inline-fields">
									<div class="form-inline-field">
										<span>모집 상태</span> <select name="recruitStatus"
											class="form-select" style="width: 140px;">
											<option value="1" selected>모집중</option>
											<option value="0">모집완료</option>
										</select>
									</div>

									<div class="form-inline-field">
										<span>현재 인원</span> <input type="number" name="currentMembers"
											min="1" value="1" class="form-number" />
									</div>

									<div class="form-inline-field">
										<span>총 모집 인원</span> <input type="number" name="maxMembers"
											min="1" value="4" class="form-number" />
									</div>
								</div>
							</td>
						</tr>
					</c:if>

					<tr>
						<th>제목</th>
						<td><input type="text" name="title" class="form-input"
							maxlength="200" /></td>
					</tr>

					<tr>
						<th>내용</th>
						<td><textarea name="content" class="form-textarea"></textarea>
						</td>
					</tr>
				</table>
			</div>

			<div class="form-actions">
				<a
					href="${pageContext.request.contextPath}/board/list?category=${category}"
					class="action-btn btn-cancel">
					취소 </a>

				<button class="action-btn btn-submit">등록</button>
			</div>
		</form>
	</div>
</div>