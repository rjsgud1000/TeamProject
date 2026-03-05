package service;

import java.util.List;

import dao.CommentDAO;
import vo.Comment;

/**
 * [역할] 댓글 비즈니스 로직(Service)
 *
 * - 댓글 작성/조회/수정의 정책을 정의합니다.
 * - 권한(본인만 수정 가능)은 DAO update 쿼리 조건(author_login_id)으로도 한 번 더 보강됩니다.
 */
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