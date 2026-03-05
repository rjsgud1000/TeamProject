package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import util.DBCPUtil;
import vo.Member;

/**
 * [역할] member 테이블 접근 DAO
 *
 * - DAO 책임: SQL 실행과 ResultSet 매핑
 * - 트랜잭션 경계는 현재 단건 처리 위주(필요 시 Service에서 트랜잭션 관리 방식으로 확장)
 *
 * [예외 처리]
 * - SQLException은 호출부에서 처리하기 복잡하므로 RuntimeException으로 래핑하여 전파합니다.
 *   (운영에서는 로깅/에러 페이지 처리 전략을 별도로 두는 것을 권장)
 *
 * [유지보수 포인트]
 * - member 테이블 컬럼명/스키마가 변경되면 이 클래스의 SQL과 map(...)이 함께 수정 대상입니다.
 */
public class MemberDAO {

    /** 아이디(login_id) 존재 여부 */
    public boolean existsLoginId(String loginId) {
        String sql = "SELECT 1 FROM member WHERE login_id = ?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 닉네임(nickname) 존재 여부 */
    public boolean existsNickname(String nickname) {
        String sql = "SELECT 1 FROM member WHERE nickname = ?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 이메일(email) 존재 여부 */
    public boolean existsEmail(String email) {
        String sql = "SELECT 1 FROM member WHERE email = ?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> listAll() {
        String sql = "SELECT login_id, password_hash, nickname, email, profile_image, role, status, sanction_until, created_at FROM member ORDER BY created_at DESC";
        List<Member> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Member m) {
        String sql = "INSERT INTO member (login_id, password_hash, nickname, email, profile_image, role, status, sanction_until, created_at) VALUES (?,?,?,?,?,?,?,?,NOW())";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getLoginId());
            ps.setString(2, m.getPasswordHash());
            ps.setString(3, m.getNickname());
            ps.setString(4, m.getEmail());
            ps.setString(5, m.getProfileImage());
            ps.setString(6, m.getRole());
            ps.setString(7, m.getStatus());
            ps.setObject(8, m.getSanctionUntil());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Member findByLoginId(String loginId) {
        String sql = "SELECT login_id, password_hash, nickname, email, profile_image, role, status, sanction_until, created_at FROM member WHERE login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateByAdmin(String loginId, String nickname, String role) {
        String sql = "UPDATE member SET nickname=?, role=? WHERE login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setString(2, role);
            ps.setString(3, loginId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByLoginId(String loginId) {
        String sql = "DELETE FROM member WHERE login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSanctionUntil(String loginId, LocalDateTime until) {
        String sql = "UPDATE member SET sanction_until=? WHERE login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, until);
            ps.setString(2, loginId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProfile(String loginId, String nickname, String passwordHashOrNull) {
        String sql;
        boolean changePw = passwordHashOrNull != null;
        if (changePw) {
            sql = "UPDATE member SET nickname=?, password_hash=? WHERE login_id=?";
        } else {
            sql = "UPDATE member SET nickname=? WHERE login_id=?";
        }

        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (changePw) {
                ps.setString(1, nickname);
                ps.setString(2, passwordHashOrNull);
                ps.setString(3, loginId);
            } else {
                ps.setString(1, nickname);
                ps.setString(2, loginId);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProfileImage(String loginId, String profileImageOrNull) {
        String sql = "UPDATE member SET profile_image=? WHERE login_id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, profileImageOrNull);
            ps.setString(2, loginId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Member map(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setLoginId(rs.getString("login_id"));
        m.setPasswordHash(rs.getString("password_hash"));
        m.setNickname(rs.getString("nickname"));
        m.setEmail(rs.getString("email"));
        m.setProfileImage(rs.getString("profile_image"));
        m.setRole(rs.getString("role"));
        m.setStatus(rs.getString("status"));
        m.setSanctionUntil(rs.getObject("sanction_until", LocalDateTime.class));
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return m;
    }
}