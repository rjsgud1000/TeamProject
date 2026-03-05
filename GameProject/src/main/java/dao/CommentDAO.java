package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBCPUtil;
import vo.Comment;

/**
 * [역할] comment 테이블 접근 DAO
 *
 * - 댓글 CRUD 중 이 프로젝트에서 사용하는 범위(추가/조회/수정)를 담당합니다.
 * - SQL/컬럼명이 바뀌면 이 클래스의 쿼리와 매핑 로직이 함께 수정 대상입니다.
 */
public class CommentDAO {

    /** 댓글 등록 후 생성된 PK(id) 반환 */
    public long insert(Comment c) {
        String sql = "INSERT INTO comment (post_id, author_login_id, content, created_at) VALUES (?,?,?,NOW())";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, c.getPostId());
            ps.setString(2, c.getAuthorLoginId());
            ps.setString(3, c.getContent());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("No generated key returned");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 특정 게시글(post_id)의 댓글 목록 조회(오래된 순) */
    public List<Comment> listByPostId(long postId) {
        String sql = "SELECT id, post_id, author_login_id, content, created_at FROM comment WHERE post_id=? ORDER BY id ASC";
        List<Comment> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getLong("id"));
                    c.setPostId(rs.getLong("post_id"));
                    c.setAuthorLoginId(rs.getString("author_login_id"));
                    c.setContent(rs.getString("content"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    out.add(c);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 특정 작성자(author_login_id)의 댓글 목록 조회(최신 순, 페이지네이션) */
    public List<Comment> listByAuthor(String authorLoginId, int offset, int limit) {
        String sql = "SELECT id, post_id, author_login_id, content, created_at FROM comment WHERE author_login_id=? ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Comment> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, authorLoginId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getLong("id"));
                    c.setPostId(rs.getLong("post_id"));
                    c.setAuthorLoginId(rs.getString("author_login_id"));
                    c.setContent(rs.getString("content"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    out.add(c);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Comment findById(long id) {
        String sql = "SELECT id, post_id, author_login_id, content, created_at FROM comment WHERE id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Comment c = new Comment();
                c.setId(rs.getLong("id"));
                c.setPostId(rs.getLong("post_id"));
                c.setAuthorLoginId(rs.getString("author_login_id"));
                c.setContent(rs.getString("content"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return c;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateContent(long commentId, String authorLoginId, String content) {
        String sql = "UPDATE comment SET content=? WHERE id=? AND author_login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setLong(2, commentId);
            ps.setString(3, authorLoginId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}