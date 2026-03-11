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
            <td width="35%">
                <c:choose>
                    <c:when test="${post.category == '0' || post.category == 0}">공지</c:when>
                    <c:when test="${post.category == '1' || post.category == 1}">자유</c:when>
                    <c:when test="${post.category == '2' || post.category == 2}">질문</c:when>
                    <c:when test="${post.category == '3' || post.category == 3}">파티</c:when>
                    <c:otherwise>기타</c:otherwise>
                </c:choose>
            </td>
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
            <td colspan="3" style="height:250px; vertical-align:top;">
                <pre style="white-space: pre-wrap; border:none;">${post.content}</pre>
            </td>
        </tr>
    </table>

	<div style="margin-top: 20px;">

		<a href="${pageContext.request.contextPath}/board/list?category=${category}&page=${page}">목록으로</a>

		<c:if test="${sessionScope.loginMember != null && sessionScope.loginMember.memberId == post.memberId}">
        |
        <a href="${pageContext.request.contextPath}/board/edit?postId=${post.postId}&category=${category}&page=${page}">수정</a>
        |
        <a href="${pageContext.request.contextPath}/board/delete?postId=${post.postId}&category=${category}&page=${page}"
   		   onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a>
		</c:if>

	</div>
	<div style="margin-top: 20px;">
		<c:if test="${sessionScope.loginMember != null}">
			<form action="${pageContext.request.contextPath}/board/like"
				method="post" style="display: inline-block;">
				<input type="hidden" name="postId" value="${post.postId}"> <input
					type="hidden" name="category" value="${category}"> <input
					type="hidden" name="page" value="${page}">

				<button type="submit">
					<c:choose>
						<c:when test="${liked}">추천 취소</c:when>
						<c:otherwise>추천</c:otherwise>
					</c:choose>
				</button>
			</form>
		</c:if>

		<span style="margin-left: 10px;">추천 수: ${likeCount}</span>

		<c:if test="${sessionScope.loginMember == null}">
			<span style="margin-left: 10px; color: gray;">추천은 로그인 후 가능합니다.</span>
		</c:if>
	</div>

	<!-- 댓글 영역 -->
    <div style="margin-top:50px;">
        <h3>댓글</h3>

        <c:forEach var="c" items="${comments}">
            <div style="border-bottom:1px solid #ccc; padding:8px;
                <c:if test='${c.parentCommentId != null}'>margin-left:40px; background:#f9f9f9;</c:if>">

                <b>${c.memberNickname}</b>
                <fmt:formatDate value="${c.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                <p>${c.content}</p>

                <c:if test="${sessionScope.loginMember != null}">
                    <!-- 좋아요 -->
                    <form action="${pageContext.request.contextPath}/board/detail" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="like"/>
                        <input type="hidden" name="commentId" value="${c.commentId}"/>
                        <input type="hidden" name="postId" value="${post.postId}"/>
                        <button type="submit">👍 ${c.likeCount}</button>
                    </form>

                    <!-- 싫어요 -->
                    <form action="${pageContext.request.contextPath}/board/detail" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="dislike"/>
                        <input type="hidden" name="commentId" value="${c.commentId}"/>
                        <input type="hidden" name="postId" value="${post.postId}"/>
                        <button type="submit">👎 ${c.dislikeCount}</button>
                    </form>

                    <!-- 수정 버튼 (작성자 또는 ADMIN) -->
                    <c:if test="${sessionScope.loginMember.role eq 'ADMIN' 
           							 || sessionScope.loginMember.memberId eq c.memberId}">
                        <button type="button" onclick="toggleEdit(${c.commentId})">수정</button>
                        <div id="editForm-${c.commentId}" style="display:none; margin-top:5px;">
                            <form action="${pageContext.request.contextPath}/board/detail" method="post">
                                <input type="hidden" name="action" value="update"/>
                                <input type="hidden" name="commentId" value="${c.commentId}"/>
                                <input type="hidden" name="postId" value="${post.postId}"/>
                                <textarea name="content" rows="2" cols="40"><c:out value="${c.content}"/></textarea>
                                <br>
                                <button type="submit">수정 완료</button>
                            </form>
                        </div>
                    </c:if>

                    <!-- 삭제 버튼 (작성자 또는 ADMIN) -->
                    <c:if test="${sessionScope.loginMember.role eq 'ADMIN' 
           						 || sessionScope.loginMember.memberId eq c.memberId}">
                        <form action="${pageContext.request.contextPath}/board/detail" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="commentId" value="${c.commentId}"/>
                            <input type="hidden" name="postId" value="${post.postId}"/>
                            <button type="submit">삭제</button>
                        </form>
                    </c:if>

                    <!-- 답글 버튼 -->
                    <button type="button" onclick="toggleReply(${c.commentId})">답글</button>
                    <div id="replyForm-${c.commentId}" style="display:none; margin-top:5px;">
                        <form action="${pageContext.request.contextPath}/board/detail" method="post">
                            <input type="hidden" name="action" value="insert"/>
                            <input type="hidden" name="postId" value="${post.postId}"/>
                            <input type="hidden" name="parentCommentId" value="${c.commentId}"/>
                            <textarea name="content" rows="2" cols="40" placeholder="답글을 입력하세요"></textarea>
                            <br>
                            <button type="submit">답글 작성</button>
                        </form>
                    </div>

                    <!-- 신고 버튼 -->
                    <button type="button" onclick="toggleReport(${c.commentId})">신고</button>
                    <div id="reportForm-${c.commentId}" style="display:none; margin-top:5px;">
                        <form action="${pageContext.request.contextPath}/board/detail" method="post">
                            <input type="hidden" name="action" value="report"/>
                            <input type="hidden" name="commentId" value="${c.commentId}"/>
                            <input type="hidden" name="postId" value="${post.postId}"/>
                            <textarea name="reason" rows="2" cols="30" placeholder="신고 사유"></textarea>
                            <br>
                            <button type="submit">신고하기</button>
                        </form>
                    </div>
                </c:if>
            </div>
        </c:forEach>

        <!-- 일반 댓글 작성 폼 (반복문 밖) -->
        <c:if test="${sessionScope.loginMember != null}">
            <div style="margin-top:20px;">
                <form action="${pageContext.request.contextPath}/board/detail" method="post">
                    <input type="hidden" name="action" value="insert"/>
                    <input type="hidden" name="postId" value="${post.postId}"/>
                    <textarea name="content" rows="3" cols="50" placeholder="댓글을 입력하세요"></textarea>
                    <br>
                    <button type="submit">댓글 달기</button>
                </form>
            </div>
        </c:if>
    </div>
</div>

<script>
function toggleReply(id){
    let f = document.getElementById("replyForm-" + id);
    f.style.display = (f.style.display === "none") ? "block" : "none";
}
function toggleReport(id){
    let f = document.getElementById("reportForm-" + id);
    f.style.display = (f.style.display === "none") ? "block" : "none";
}
function toggleEdit(id){
    let f = document.getElementById("editForm-" + id);
    f.style.display = (f.style.display === "none") ? "block" : "none";
}
</script>