<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div style="max-width: 1000px; margin: 0 auto; padding: 20px;">
  <h2 style="margin-bottom: 16px;">질문과 답변</h2>

  <table style="width:100%; border-collapse: collapse;">
    <thead>
      <tr style="border-bottom: 1px solid #ddd;">
        <th style="text-align:left; padding:10px;">상태</th>
        <th style="text-align:left; padding:10px;">제목</th>
        <th style="text-align:left; padding:10px;">작성자</th>
        <th style="text-align:right; padding:10px;">조회</th>
        <th style="text-align:left; padding:10px;">작성일</th>
      </tr>
    </thead>

    <tbody>
      <c:choose>
        <c:when test="${empty qnaList}">
          <tr>
            <td colspan="5" style="padding: 14px; text-align:center; color:#777;">
              아직 등록된 질문이 없습니다.
            </td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="q" items="${qnaList}">
            <tr style="border-bottom: 1px solid #f0f0f0;">
              <td style="padding:10px;">
                <c:choose>
                  <c:when test="${q.acceptedCommentId ne null}">
                    해결됨✅
                  </c:when>
                  <c:otherwise>
                    미해결
                  </c:otherwise>
                </c:choose>
              </td>

              <td style="padding:10px;">
                <a href="${pageContext.request.contextPath}/qna/detail?postId=${q.postId}"
                   style="text-decoration:none;">
                  ${q.title}
                </a>
              </td>

              <td style="padding:10px;">${q.nickname}</td>
              <td style="padding:10px; text-align:right;">${q.viewcount}</td>
              <td style="padding:10px;">
                <fmt:formatDate value="${q.createAt}" pattern="yyyy-MM-dd HH:mm"/>
              </td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>

  <div style="margin-top: 16px; display:flex; gap:10px;">
    <a href="${pageContext.request.contextPath}/qna/list?page=${page-1}"
       style="padding:8px 12px; border:1px solid #ddd; border-radius:8px; text-decoration:none;">
      이전
    </a>
    <a href="${pageContext.request.contextPath}/qna/list?page=${page+1}"
       style="padding:8px 12px; border:1px solid #ddd; border-radius:8px; text-decoration:none;">
      다음
    </a>
  </div>
</div>