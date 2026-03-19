<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="max-width:900px; margin:0 auto; padding:20px;">
    <h2>
        <c:choose>
            <c:when test="${isAnswerWrite}">답변 작성</c:when>
            <c:otherwise>게시글 작성</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty errorMessage}">
        <div style="color:red; margin-bottom:15px; font-weight:bold;">
            ${errorMessage}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/board/write" method="post">
        <input type="hidden" name="category" value="${category}" />
        <input type="hidden" name="parentPostId" value="${parentPostId}" />

        <table style="width:100%; border-collapse:collapse; margin-top:15px;">
            <tr>
                <th style="width:15%; padding:10px; border:1px solid #ddd; background:#f8f8f8;">카테고리</th>
                <td style="padding:10px; border:1px solid #ddd;">
                    <c:choose>
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
                    </c:choose>
                </td>
            </tr>

            <c:if test="${category == 3}">
                <tr>
                    <th style="padding:10px; border:1px solid #ddd; background:#f8f8f8;">모집 상태</th>
                    <td style="padding:10px; border:1px solid #ddd;">
                        <select name="recruitStatus" style="padding:8px;">
                            <option value="1" selected>모집중</option>
                            <option value="0">모집완료</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th style="padding:10px; border:1px solid #ddd; background:#f8f8f8;">현재 인원</th>
                    <td style="padding:10px; border:1px solid #ddd;">
                        <input type="number" name="currentMembers" min="1" value="1" style="width:120px; padding:8px;" />
                    </td>
                </tr>
                <tr>
                    <th style="padding:10px; border:1px solid #ddd; background:#f8f8f8;">총 모집 인원</th>
                    <td style="padding:10px; border:1px solid #ddd;">
                        <input type="number" name="maxMembers" min="1" value="4" style="width:120px; padding:8px;" />
                    </td>
                </tr>
            </c:if>

            <tr>
                <th style="padding:10px; border:1px solid #ddd; background:#f8f8f8;">제목</th>
                <td style="padding:10px; border:1px solid #ddd;">
                    <input type="text" name="title" style="width:100%; padding:8px;" maxlength="200" />
                </td>
            </tr>

            <tr>
                <th style="padding:10px; border:1px solid #ddd; background:#f8f8f8;">내용</th>
                <td style="padding:10px; border:1px solid #ddd;">
                    <textarea name="content" rows="12" style="width:100%; padding:8px; resize:vertical;"></textarea>
                </td>
            </tr>
        </table>

        <div style="margin-top:20px; text-align:right;">
            <a href="${pageContext.request.contextPath}/board/list?category=${category}"
               style="display:inline-block; padding:10px 16px; background:#777; color:white; text-decoration:none; border-radius:4px;">
                취소
            </a>

            <button type="submit"
                    style="padding:10px 16px; background:#2d6cdf; color:white; border:none; border-radius:4px; cursor:pointer;">
                등록
            </button>
        </div>
    </form>
</div>