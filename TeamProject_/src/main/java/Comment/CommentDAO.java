package Comment;

import util.DBCPUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // 1️⃣ 댓글 조회 (부모-자식 모두 포함)
    public List<CommentDTO> getCommentsByPostId(int postId) {
        List<CommentDTO> list = new ArrayList<>();
        String sql = "SELECT c.*, m.nickname AS memberNickname " +
                     "FROM COMMENT c " +
                     "JOIN MEMBER m ON c.member_id = m.member_id " +
                     "WHERE c.post_id = ? AND c.is_deleted = 0 " +
                     "ORDER BY IFNULL(c.parent_comment_id, c.comment_id), c.created_at ASC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(rs.getInt("comment_id"));
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    dto.setIsDeleted(rs.getBoolean("is_deleted"));
                    dto.setParentCommentId(rs.getObject("parent_comment_id") != null ? rs.getInt("parent_comment_id") : null);
                    dto.setMemberNickname(rs.getString("memberNickname"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 2️⃣ 댓글 / 답글 작성
    public boolean insertComment(CommentDTO comment) {
        String sql = "INSERT INTO COMMENT (post_id, member_id, content, is_deleted, created_at, parent_comment_id) " +
                     "VALUES (?, ?, ?, 0, ?, ?)";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, comment.getPostId());
            ps.setString(2, comment.getMemberId());
            ps.setString(3, comment.getContent());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            if (comment.getParentCommentId() != null) {
                ps.setInt(5, comment.getParentCommentId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3️⃣ 댓글 수정
    public boolean updateComment(int commentId, String newContent) {
        String sql = "UPDATE COMMENT SET content = ?, updated_at = ? WHERE comment_id = ? AND is_deleted = 0";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newContent);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, commentId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4️⃣ 댓글 삭제 (소프트 삭제)
    public boolean deleteComment(int commentId) {
        String sql = "UPDATE COMMENT SET is_deleted = 1, updated_at = ? WHERE comment_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, commentId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5️⃣ 댓글 + 대댓글 재귀 삭제
    public boolean deleteCommentRecursive(int commentId) {
        boolean result = deleteComment(commentId);
        List<CommentDTO> children = getCommentsByParentId(commentId);
        for (CommentDTO child : children) {
            deleteCommentRecursive(child.getCommentId());
        }
        return result;
    }

    // 6️⃣ parentCommentId로 자식 댓글 조회
    public List<CommentDTO> getCommentsByParentId(int parentCommentId) {
        List<CommentDTO> list = new ArrayList<>();
        String sql = "SELECT c.*, m.nickname AS memberNickname " +
                     "FROM COMMENT c " +
                     "JOIN MEMBER m ON c.member_id = m.member_id " +
                     "WHERE c.parent_comment_id = ? AND c.is_deleted = 0";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, parentCommentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(rs.getInt("comment_id"));
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    dto.setIsDeleted(rs.getBoolean("is_deleted"));
                    dto.setParentCommentId(rs.getObject("parent_comment_id") != null ? rs.getInt("parent_comment_id") : null);
                    dto.setMemberNickname(rs.getString("memberNickname"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 7️⃣ 단일 댓글 조회
    public CommentDTO getCommentById(int commentId) {
        String sql = "SELECT c.*, m.nickname AS memberNickname " +
                     "FROM COMMENT c " +
                     "JOIN MEMBER m ON c.member_id = m.member_id " +
                     "WHERE c.comment_id = ? AND c.is_deleted = 0";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(rs.getInt("comment_id"));
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    dto.setIsDeleted(rs.getBoolean("is_deleted"));
                    dto.setParentCommentId(rs.getObject("parent_comment_id") != null ? rs.getInt("parent_comment_id") : null);
                    dto.setMemberNickname(rs.getString("memberNickname"));
                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 8️⃣ 댓글 좋아요 / 취소
    public boolean likeComment(int commentId, String memberId) {
        try {
            if(hasLiked(commentId, memberId)) return unlikeComment(commentId, memberId);
            if(hasDisliked(commentId, memberId)) undislikeComment(commentId, memberId);

            String sql = "INSERT INTO COMMENT_LIKE (comment_id, member_id, created_at) VALUES (?, ?, ?)";
            try(Connection conn = DBCPUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, commentId);
                ps.setString(2, memberId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                return ps.executeUpdate() > 0;
            }
        } catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    public boolean unlikeComment(int commentId, String memberId) {
        String sql = "DELETE FROM COMMENT_LIKE WHERE comment_id = ? AND member_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setString(2, memberId);
            return ps.executeUpdate() > 0;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    public int getCommentLikeCount(int commentId) {
        String sql = "SELECT COUNT(*) AS likeCount FROM COMMENT_LIKE WHERE comment_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return rs.getInt("likeCount");
            }
        } catch(Exception e){ e.printStackTrace(); }
        return 0;
    }

    // 9️⃣ 댓글 싫어요 / 취소
    public boolean dislikeComment(int commentId, String memberId) {
        try {
            if(hasDisliked(commentId, memberId)) return undislikeComment(commentId, memberId);
            if(hasLiked(commentId, memberId)) unlikeComment(commentId, memberId);

            String sql = "INSERT INTO COMMENT_DISLIKE (comment_id, member_id, created_at) VALUES (?, ?, ?)";
            try(Connection conn = DBCPUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, commentId);
                ps.setString(2, memberId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                return ps.executeUpdate() > 0;
            }
        } catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    public boolean undislikeComment(int commentId, String memberId) {
        String sql = "DELETE FROM COMMENT_DISLIKE WHERE comment_id = ? AND member_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setString(2, memberId);
            return ps.executeUpdate() > 0;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    public int getCommentDislikeCount(int commentId) {
        String sql = "SELECT COUNT(*) AS dislikeCount FROM COMMENT_DISLIKE WHERE comment_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return rs.getInt("dislikeCount");
            }
        } catch(Exception e){ e.printStackTrace(); }
        return 0;
    }

    //  🔟 댓글 신고
    public boolean reportComment(int commentId, String memberId, String reason) {
        String sql = "INSERT INTO COMMENT_REPORT (comment_id, member_id, reason, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            ps.setString(2, memberId);
            ps.setString(3, reason);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() > 0;

        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    // ✅ 좋아요/싫어요 확인
    public boolean hasLiked(int commentId, String memberId) {
        String sql = "SELECT 1 FROM COMMENT_LIKE WHERE comment_id = ? AND member_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setString(2, memberId);
            try(ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    public boolean hasDisliked(int commentId, String memberId) {
        String sql = "SELECT 1 FROM COMMENT_DISLIKE WHERE comment_id = ? AND member_id = ?";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setString(2, memberId);
            try(ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    // 11️⃣ postId 가져오기
    public int getPostIdByCommentId(int commentId) {
        String sql = "SELECT post_id FROM COMMENT WHERE comment_id = ?";
        try(Connection conn = DBCPUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return rs.getInt("post_id");
            }
        } catch(Exception e){ e.printStackTrace(); }
        return 0;
    }
}