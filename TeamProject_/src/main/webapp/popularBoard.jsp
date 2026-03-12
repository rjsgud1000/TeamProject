<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="width: 1000px; margin: 40px auto;">

	<h2 style="margin-bottom: 30px;">🔥 인기 게시글</h2>

	<!-- 추천 TOP -->
	<h3>👍 추천 TOP</h3>

	<table border="1" width="100%" style="margin-bottom: 40px;">
		<tr>
			<th>번호</th>
			<th>제목</th>
			<th>추천수</th>
			<th>조회수</th>
		</tr>

		<c:forEach var="post" items="${likeTopList}">
			<tr>
				<td>${post.postId}</td>

				<td><a
					href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${post.category}">
						${post.title} </a></td>

				<td>${post.likeCount}</td>
				<td>${post.viewcount}</td>
			</tr>
		</c:forEach>

	</table>


	<!-- 조회수 TOP -->
	<h3>👀 조회수 TOP</h3>

	<table border="1" width="100%">
		<tr>
			<th>번호</th>
			<th>제목</th>
			<th>조회수</th>
			<th>추천수</th>
		</tr>

		<c:forEach var="post" items="${viewTopList}">
			<tr>
				<td>${post.postId}</td>

				<td><a
					href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${post.category}">
						${post.title} </a></td>

				<td>${post.viewcount}</td>
				<td>${post.likeCount}</td>
			</tr>
		</c:forEach>

	</table>
	
	<!-- 댓글 TOP -->
	<h3>💬 댓글 TOP</h3>

	<table border="1">
		<tr>
			<th>번호</th>
			<th>제목</th>
			<th>댓글수</th>
			<th>조회수</th>
		</tr>

		<c:forEach var="p" items="${commentTopList}">
			<tr>
				<td>${p.postId}</td>
				<td><a
					href="${pageContext.request.contextPath}/board/detail?postId=${p.postId}">
						${p.title} </a></td>
				<td>${p.commentCount}</td>
				<td>${p.viewcount}</td>
			</tr>
		</c:forEach>

	</table>

</div>