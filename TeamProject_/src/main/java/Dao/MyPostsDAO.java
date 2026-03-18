package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Vo.BoardPostVO;
import util.DBCPUtil;

public class MyPostsDAO {

    public List<BoardPostVO> findPostsByMember(String memberId, String nickname) {
        return findPostsByMember(memberId, nickname, 0, Integer.MAX_VALUE);
    }

    public List<BoardPostVO> findPostsByMember(String memberId, String nickname, int offset, int limit) {
        List<BoardPostVO> list = new ArrayList<>();
        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at, " +
                "       COUNT(pl.post_id) AS like_count, " +
                "       (SELECT COUNT(*) FROM COMMENT c WHERE c.post_id = bp.post_id AND c.is_deleted = 0 AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "LEFT JOIN POST_LIKE pl ON bp.post_id = pl.post_id " +
                "WHERE bp.is_deleted = 0 AND bp.is_blinded = 0 AND (bp.member_id = ? OR (? IS NOT NULL AND bp.nickname = ?)) " +
                "GROUP BY bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at " +
                "ORDER BY bp.post_id DESC " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, nickname);
            pstmt.setString(3, nickname);
            pstmt.setInt(4, limit);
            pstmt.setInt(5, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setLikeCount(rs.getInt("like_count"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countPostsByMember(String memberId, String nickname) {
        String sql = "SELECT COUNT(*) FROM BOARD_POST bp WHERE bp.is_deleted = 0 AND bp.is_blinded = 0 AND (bp.member_id = ? OR (? IS NOT NULL AND bp.nickname = ?))";
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.setString(2, nickname);
            pstmt.setString(3, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
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