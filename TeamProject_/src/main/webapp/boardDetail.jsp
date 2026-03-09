<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div style="width: 80%; margin: 30px auto;">
    <h2>게시글 상세보기</h2>

    <table border="1" width="100%" cellspacing="0" cellpadding="10">
        <tr>
            <th width="15%">번호</th>
            <td width="35%">${post.postId}</td>
			<th width="15%">카테고리</th>
			<td width="35%"><c:choose>
					<c:when test="${post.category == '0' || post.category == 0}">공지</c:when>
					<c:when test="${post.category == '1' || post.category == 1}">자유</c:when>
					<c:when test="${post.category == '2' || post.category == 2}">질문</c:when>
					<c:when test="${post.category == '3' || post.category == 3}">파티</c:when>
					<c:otherwise>기타</c:otherwise>
				</c:choose></td>
		</tr>
        <tr>
            <th>제목</th>
            <td colspan="3">${post.title}</td>
        </tr>
        <tr>
            <th>작성자</th>
            <td>${post.writer}</td>
            <th>조회수</th>
            <td>${post.viewCount}</td>
        </tr>
        <tr>
            <th>작성일</th>
            <td colspan="3">
                <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
        </tr>
        <tr>
            <th>내용</th>
            <td colspan="3" style="height: 250px; vertical-align: top;">
                <pre style="white-space: pre-wrap; border: none; margin: 0;">${post.content}</pre>
            </td>
        </tr>
    </table>

    <div style="margin-top: 20px;">
        <a href="${pageContext.request.contextPath}/board/list?category=${category}">목록으로</a>
    </div>
</div>