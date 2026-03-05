<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Service.BoardService, Vo.BoardVO"%>

<%
    String boardIdStr = request.getParameter("boardId");
    if (boardIdStr == null || boardIdStr.trim().isEmpty()) {
        out.println("<script>alert('게시글을 찾을 수 없습니다.'); history.back();</script>");
        return;
    }

    int boardId = Integer.parseInt(boardIdStr);
    BoardService boardService = new BoardService();
    BoardVO board = boardService.getBoard(boardId);

    if (board == null) {
        out.println("<script>alert('게시글이 존재하지 않습니다.'); history.back();</script>");
        return;
    }

    // 삭제 요청 처리
    String action = request.getParameter("action");
    if ("delete".equals(action)) {
        boolean deleted = boardService.deleteBoard(boardId);
        if (deleted) {
%>
<script>
    alert("게시글이 삭제되었습니다.");
    window.location.href = "BoardList.jsp";
</script>
<%
            return;
        } else {
%>
<script>
    alert("게시글 삭제에 실패했습니다.");
    history.back();
</script>
<%
            return;
        }
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>자유게시판 - 상세보기</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        .board-container { max-width: 800px; margin: auto; }
        .board-title { font-size: 24px; font-weight: bold; margin-bottom: 10px; }
        .board-meta { color: #555; margin-bottom: 20px; }
        .board-content { border-top: 1px solid #ccc; padding-top: 10px; }
        .btn-group { margin-top: 20px; }
        .btn { display: inline-block; padding: 6px 12px; margin-right: 8px; border-radius: 6px; text-decoration: none; color: #fff; }
        .btn-edit { background-color: #1d4ed8; }
        .btn-delete { background-color: #dc2626; }
        .btn-list { background-color: #6b7280; }
    </style>
</head>
<body>

<div class="board-container">
    <div class="board-title"><%= board.getTitle() %></div>
    <div class="board-meta">
        작성자: <%= board.getWriterId() %> | 작성일: <%= board.getRegDate() %> | 조회수: <%= board.getViewCount() %>
    </div>
    <div class="board-content">
        <pre><%= board.getContent() %></pre>
    </div>

    <div class="btn-group">
        <a class="btn btn-list" href="BoardList.jsp">목록으로</a>
        <a class="btn btn-delete" href="BoardView.jsp?boardId=<%= board.getBoardId() %>&action=delete"
           onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a>
        <!-- 편집 버튼은 추후 구현 가능 -->
        <!-- <a class="btn btn-edit" href="BoardEdit.jsp?boardId=<%= board.getBoardId() %>">수정</a> -->
    </div>
</div>

</body>
</html>