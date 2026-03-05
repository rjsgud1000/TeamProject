<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Service.BoardService, Vo.BoardVO" %>
<%
    String writerId = (String) session.getAttribute("loginId");
    if (writerId == null) {
        response.sendRedirect("member/login.me");
        return;
    }

    String title = request.getParameter("title");
    String content = request.getParameter("content");
    String action = request.getParameter("action");

    if ("write".equals(action)) {
        BoardVO board = new BoardVO();
        board.setTitle(title);
        board.setContent(content);
        board.setWriterId(writerId);

        BoardService service = new BoardService();
        if (service.writeBoard(board)) {
            response.sendRedirect("BoardList.jsp");
            return;
        } else {
            out.println("<script>alert('글 작성에 실패했습니다.');</script>");
        }
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>자유게시판 - 글쓰기</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        h2 { margin-bottom: 20px; }
        form { display: flex; flex-direction: column; gap: 12px; max-width: 600px; }
        input[type=text], textarea { width: 100%; padding: 8px; font-size: 14px; border: 1px solid #ccc; border-radius: 4px; }
        textarea { height: 200px; resize: vertical; }
        button { width: 100px; padding: 8px; background-color: #1d4ed8; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #2563eb; }
        .back-btn { text-decoration: none; color: #1d4ed8; margin-top: 10px; display: inline-block; }
    </style>
</head>
<body>

<h2>자유게시판 글쓰기</h2>

<form method="post" action="BoardWrite.jsp">
    <input type="hidden" name="action" value="write"/>
    <label>제목:</label>
    <input type="text" name="title" required/>
    
    <label>내용:</label>
    <textarea name="content" required></textarea>
    
    <button type="submit">작성</button>
</form>

<a class="back-btn" href="BoardList.jsp">← 목록으로 돌아가기</a>

</body>
</html>