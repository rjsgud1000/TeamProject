<%@ page contentType="text/html; charset=UTF-8"%>

<div style="width: 80%; margin: 30px auto;">

	<h2>게시글 수정</h2>

	<form method="post" action="${pageContext.request.contextPath}/board/edit">

		<input type="hidden" name="postId" value="${post.postId}"> <input
			type="hidden" name="category" value="${category}">

		<table border="1" width="100%" cellpadding="10">

			<tr>
				<th width="15%">제목</th>
				<td><input type="text" name="title" value="${post.title}"
					style="width: 100%"></td>
			</tr>

			<tr>
				<th>내용</th>
				<td><textarea name="content"
						style="width: 100%; height: 250px;">${post.content}</textarea></td>
			</tr>

		</table>
		<div style="margin-top: 20px;">
			<button type="submit">수정완료</button>

			<a href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}">취소 </a>
		</div>

	</form>

</div>