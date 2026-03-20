<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="board-form-page">
	<div class="form-container">
		<h2 class="form-title">게시글 수정</h2>

		<form method="post"
			action="${pageContext.request.contextPath}/board/edit">
			<input type="hidden" name="postId" value="${post.postId}"> <input
				type="hidden" name="category" value="${category}"> <input
				type="hidden" name="page" value="${page}">

			<div class="form-card">
				<table class="form-table">

					<c:if test="${post.category == '3' || post.category == 3}">
						<tr>
							<th>모집 설정</th>
							<td>
								<div class="form-inline-fields">
									<div class="form-inline-field">
										<span>모집 상태</span> <select name="recruitStatus"
											class="form-select" style="width: 140px;">
											<option value="1"
												${post.recruitStatus == 1 ? 'selected' : ''}>모집중</option>
											<option value="0"
												${post.recruitStatus == 0 ? 'selected' : ''}>모집완료</option>
										</select>
									</div>

									<div class="form-inline-field">
										<span>현재 인원</span> <input type="number" name="currentMembers"
											min="1"
											value="${post.currentMembers != null ? post.currentMembers : 1}"
											class="form-number" required>
									</div>

									<div class="form-inline-field">
										<span>총 모집 인원</span> <input type="number" name="maxMembers"
											min="1"
											value="${post.maxMembers != null ? post.maxMembers : 4}"
											class="form-number" required>
									</div>
								</div>

								<div class="form-help">현재 인원이 총 모집 인원과 같아지면 자동으로 모집완료로
									변경됩니다.</div>
							</td>
						</tr>
					</c:if>

					<tr>
						<th>제목</th>
						<td><input type="text" name="title" value="${post.title}"
							class="form-input" required></td>
					</tr>

					<tr>
						<th>내용</th>
						<td><textarea name="content" class="form-textarea" required>${post.content}</textarea>
						</td>
					</tr>

				</table>
			</div>

			<div class="form-actions">
				<a
					href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${page}"
					class="action-btn btn-cancel">
					취소 </a>

				<button class="action-btn btn-submit">수정완료</button>
			</div>
		</form>
	</div>
</div>