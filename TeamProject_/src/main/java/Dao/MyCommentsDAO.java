package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Comment.CommentDTO;
import util.DBCPUtil;

public class MyCommentsDAO {

    public List<CommentDTO> findCommentsByMember(String memberId, String nickname) {
        List<CommentDTO> list = new ArrayList<>();
        String sql = "SELECT c.comment_id, c.post_id, c.member_id, c.content, c.created_at, c.updated_at, c.is_deleted, c.parent_comment_id, " +
                     "       m.nickname AS memberNickname " +
                     "FROM COMMENT c " +
                     "LEFT JOIN MEMBER m ON c.member_id = m.member_id " +
                     "WHERE c.is_deleted = 0 AND (c.member_id = ? OR (? IS NOT NULL AND m.nickname = ?)) " +
                     "ORDER BY c.comment_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ps.setString(2, nickname);
            ps.setString(3, nickname);

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

    public int countCommentsByMember(String memberId, String nickname) {
        String sql = "SELECT COUNT(*) FROM COMMENT c LEFT JOIN MEMBER m ON c.member_id = m.member_id WHERE c.is_deleted = 0 AND (c.member_id = ? OR (? IS NOT NULL AND m.nickname = ?))";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, nickname);
            ps.setString(3, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}