package Dao;

import Vo.QnaPostVO;
import util.DBCPUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QnaDAO {

    // accepted_comment_id 컬럼이 있는 경우 버전
    private static final String SQL_LIST_WITH_ACCEPT =
            "SELECT post_id, member_id, nickname, title, viewcount, create_at, accepted_comment_id " +
            "FROM BOARD_POST " +
            "WHERE category = 2 AND is_deleted = 0 " +
            "ORDER BY post_id DESC " +
            "LIMIT ?, ?";

    // accepted_comment_id 컬럼이 아직 없을 때 버전(안전용)
    private static final String SQL_LIST_NO_ACCEPT =
            "SELECT post_id, member_id, nickname, title, viewcount, create_at " +
            "FROM BOARD_POST " +
            "WHERE category = 2 AND is_deleted = 0 " +
            "ORDER BY post_id DESC " +
            "LIMIT ?, ?";

    public List<QnaPostVO> selectQnaList(int offset, int limit, boolean hasAcceptedColumn) {
        List<QnaPostVO> list = new ArrayList<>();
        String sql = hasAcceptedColumn ? SQL_LIST_WITH_ACCEPT : SQL_LIST_NO_ACCEPT;

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, offset);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    QnaPostVO vo = new QnaPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setTitle(rs.getString("title"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));

                    if (hasAcceptedColumn) {
                        int v = rs.getInt("accepted_comment_id");
                        vo.setAcceptedCommentId(rs.wasNull() ? null : v);
                    }

                    list.add(vo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 SQL 에러 뜸
        }
        return list;
    }
}