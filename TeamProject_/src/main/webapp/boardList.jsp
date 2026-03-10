<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div style="max-width:1000px; margin:0 auto; padding:20px;">

    <h2>${boardTitle}</h2>

	<div style="text-align: right; margin: 10px 0 15px 0;">
		<a href="${pageContext.request.contextPath}/board/write?category=${category}"
		   style="display: inline-block; padding: 10px 16px; background: #2d6cdf; color: white; text-decoration: none; border-radius: 4px;">
		   글쓰기 </a>
	</div>

	<table style="width:100%; border-collapse:collapse; margin-top:15px;">
        <thead>
            <tr style="border-bottom:1px solid #ccc;">
                <th style="padding:10px;">번호</th>
                <th style="padding:10px;">카테고리</th>
                <th style="padding:10px; text-align:left;">제목</th>
                <th style="padding:10px;">작성자</th>
                <th style="padding:10px;">조회수</th>
                <th style="padding:10px;">작성일</th>
            </tr>
        </thead>
        <tbody>
			<c:forEach var="post" items="${boardList}">
				<tr style="border-bottom: 1px solid #eee;">
					<td style="padding: 10px; text-align: center;">${post.postId}</td>

					<td style="padding: 10px; text-align: center;"><c:choose>
							<c:when test="${post.category == 0}">공지</c:when>
							<c:when test="${post.category == 1}">자유</c:when>
							<c:when test="${post.category == 2}">질문</c:when>
							<c:when test="${post.category == 3}">파티</c:when>
							<c:otherwise>기타</c:otherwise>
						</c:choose></td>

					<td style="padding: 10px; text-align: left;">
					<a href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${currentPage}">
    				${post.title}</a>

					<td style="padding: 10px; text-align: center;">${post.nickname}</td>
					<td style="padding: 10px; text-align: center;">${post.viewcount}</td>
					<td style="padding: 10px; text-align: center;"><fmt:formatDate
							value="${post.createAt}" pattern="yyyy-MM-dd" /></td>
				</tr>
			</c:forEach>

			<c:if test="${empty boardList}">
                <tr>
                    <td colspan="6" style="padding:20px; text-align:center;">
                        게시글이 없습니다.
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
		<div style="text-align: center; margin-top: 20px;">
	
			<c:if test="${startPage > 1}">
				<a
					href="${pageContext.request.contextPath}/board/list?category=${category}&page=${startPage - 1}"
					style="margin: 0 5px;">[이전]</a>
			</c:if>
	
			<c:forEach var="i" begin="${startPage}" end="${endPage}">
				<c:choose>
					<c:when test="${i == currentPage}">
						<strong style="margin: 0 8px; color: blue;">${i}</strong>
					</c:when>
					<c:otherwise>
						<a
							href="${pageContext.request.contextPath}/board/list?category=${category}&page=${i}"
							style="margin: 0 8px;">${i}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
	
			<c:if test="${endPage < totalPage}">
				<a
					href="${pageContext.request.contextPath}/board/list?category=${category}&page=${endPage + 1}"
					style="margin: 0 5px;">[다음]</a>
			</c:if>
	
		</div>
</div>