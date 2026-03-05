<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="Service.BoardService, Vo.BoardVO, java.util.List" %>

<%
    BoardService boardService = new BoardService();
    List<BoardVO> boardList = boardService.getBoardList();
%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>자유게시판</title>

<style>

body{
    font-family: Arial, sans-serif;
    background-color:#f5f6f7;
}

.container{
    width:900px;
    margin:50px auto;
    background:white;
    padding:30px;
    border-radius:10px;
    box-shadow:0 2px 10px rgba(0,0,0,0.1);
}

h2{
    margin-bottom:20px;
    color:#4da6ff;
}

.write-btn{
    float:right;
    background:#4da6ff;
    color:white;
    padding:8px 16px;
    border-radius:6px;
    text-decoration:none;
    font-size:14px;
}

.write-btn:hover{
    background:#3399ff;
}

table{
    width:100%;
    border-collapse:collapse;
    margin-top:20px;
}

th{
    background:#f2f4f6;
    padding:12px;
    border-bottom:2px solid #ddd;
}

td{
    padding:12px;
    border-bottom:1px solid #eee;
}

tr:hover{
    background:#f9fafb;
}

a{
    text-decoration:none;
    color:#333;
}

a:hover{
    color:#03c75a;
}

</style>

</head>

<body>

<div class="container">

<h2>자유게시판</h2>

<a class="write-btn" href="BoardWrite.jsp">글쓰기</a>

<table>

<thead>
<tr>
<th>번호</th>
<th>제목</th>
<th>작성자</th>
<th>작성일</th>
<th>조회수</th>
</tr>
</thead>

<tbody>

<%
if(boardList != null && !boardList.isEmpty()){
for(BoardVO board : boardList){
%>

<tr>
<td><%=board.getBoardId()%></td>

<td>
<a href="BoardView.jsp?boardId=<%=board.getBoardId()%>">
<%=board.getTitle()%>
</a>
</td>

<td><%=board.getWriterId()%></td>
<td><%=board.getRegDate()%></td>
<td><%=board.getViewCount()%></td>

</tr>

<%
}
}else{
%>

<tr>
<td colspan="5">게시글이 없습니다.</td>
</tr>

<%
}
%>

</tbody>
</table>

</div>

</body>
</html>