package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBCPUtil;

/**
 * [역할] post_like 테이블 접근 DAO
 *
 * - 특정 사용자(login_id)가 특정 게시글(post_id)을 좋아요 했는지 여부를 기록합니다.
 * - toggle()은 post_like 삽입/삭제 + post.likes 카운터 갱신을 하나의 트랜잭션으로 묶습니다.
 *
 * [유지보수 포인트]
 * - 동시성/무결성: toggle에서 autoCommit(false) 후 commit/rollback을 직접 관리합니다.
 * - likes가 음수가 되지 않도록 UPDATE에서 GREATEST(0, ...) 방어 로직이 들어가 있습니다.
 */
public class PostLikeDAO {

    public boolean exists(long postId, String loginId) {
        String sql = "SELECT 1 FROM post_like WHERE post_id=? AND login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return true if liked after toggle, false if unliked after toggle
     */
    public boolean toggle(long postId, String loginId) {
        try (Connection con = DBCPUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                boolean already = existsTx(con, postId, loginId);
                if (already) {
                    deleteTx(con, postId, loginId);
                    updatePostLikesTx(con, postId, -1);
                    con.commit();
                    return false;
                } else {
                    insertTx(con, postId, loginId);
                    updatePostLikesTx(con, postId, +1);
                    con.commit();
                    return true;
                }
            } catch (SQLException e) {
                try { con.rollback(); } catch (SQLException ignore) {}
                throw new RuntimeException(e);
            } finally {
                try { con.setAutoCommit(true); } catch (SQLException ignore) {}
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean existsTx(Connection con, long postId, String loginId) throws SQLException {
        String sql = "SELECT 1 FROM post_like WHERE post_id=? AND login_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertTx(Connection con, long postId, String loginId) throws SQLException {
        String sql = "INSERT INTO post_like(post_id, login_id, created_at) VALUES (?,?,NOW())";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, loginId);
            ps.executeUpdate();
        }
    }

    private void deleteTx(Connection con, long postId, String loginId) throws SQLException {
        String sql = "DELETE FROM post_like WHERE post_id=? AND login_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, loginId);
            ps.executeUpdate();
        }
    }

    private void updatePostLikesTx(Connection con, long postId, int delta) throws SQLException {
        // likes 컬럼이 음수가 되지 않게 방어
        String sql = "UPDATE post SET likes = GREATEST(0, likes + ?) WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, postId);
            ps.executeUpdate();
        }
    }
}