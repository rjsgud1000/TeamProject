package service;

import java.util.List;

import dao.CommentDAO;
import vo.Comment;

public class CommentService {

    private final CommentDAO commentDAO = new CommentDAO();

    public long write(long postId, String authorLoginId, String content) {
        Comment c = new Comment();
        c.setPostId(postId);
        c.setAuthorLoginId(authorLoginId);
        c.setContent(content);
        return commentDAO.insert(c);
    }

    public List<Comment> listByPostId(long postId) {
        return commentDAO.listByPostId(postId);
    }

    public List<Comment> listByAuthor(String authorLoginId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return commentDAO.listByAuthor(authorLoginId, offset, pageSize);
    }

    public boolean update(long commentId, String authorLoginId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력하세요.");
        }
        return commentDAO.updateContent(commentId, authorLoginId, content.trim());
    }
}