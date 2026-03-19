<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="width: 80%; margin: 30px auto;">

    <h2>게시글 수정</h2>

    <form method="post" action="${pageContext.request.contextPath}/board/edit">

        <input type="hidden" name="postId" value="${post.postId}">
        <input type="hidden" name="category" value="${category}">
        <input type="hidden" name="page" value="${page}">

        <table border="1" width="100%" cellpadding="10">

            <c:if test="${post.category == '3' || post.category == 3}">
                <tr>
                    <th width="15%">모집 상태</th>
                    <td>
                        <select name="recruitStatus">
                            <option value="1" ${post.recruitStatus == 1 ? 'selected' : ''}>모집중</option>
                            <option value="0" ${post.recruitStatus == 0 ? 'selected' : ''}>모집완료</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>현재 인원</th>
                    <td>
                        <input type="number" name="currentMembers" min="1"
                               value="${post.currentMembers != null ? post.currentMembers : 1}" required>
                    </td>
                </tr>
                <tr>
                    <th>총 모집 인원</th>
                    <td>
                        <input type="number" name="maxMembers" min="1"
                               value="${post.maxMembers != null ? post.maxMembers : 4}" required>
                    </td>
                </tr>
            </c:if>

            <tr>
                <th width="15%">제목</th>
                <td>
                    <input type="text" name="title" value="${post.title}" style="width: 100%" required>
                </td>
            </tr>

            <tr>
                <th>내용</th>
                <td>
                    <textarea name="content" style="width: 100%; height: 250px;" required>${post.content}</textarea>
                </td>
            </tr>

        </table>

        <div style="margin-top: 20px;">
            <button type="submit">수정완료</button>

            <a href="${pageContext.request.contextPath}/board/detail?postId=${post.postId}&category=${category}&page=${page}">
               취소
            </a>
        </div>

    </form>

</div>