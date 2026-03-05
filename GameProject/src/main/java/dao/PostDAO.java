package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBCPUtil;
import vo.Post;

/**
 * [역할] post 테이블 접근 DAO
 *
 * [데이터 모델 개요]
 * - level: 게시판 카테고리(Controller/Service/JSP와 동일한 의미를 공유)
 * - parent_id: Q&A 답변글인 경우 원글 id (null이면 일반 글/질문글)
 * - views/likes: 단순 카운터(인기글 정렬/필터에 사용)
 *
 * [유지보수 포인트]
 * - 인기글 정책(예: likes >= 10) 변경 시: listPopular() SQL 수정
 * - 검색 정책 변경 시: searchByLevel() SQL 수정
 */
public class PostDAO {

    /** 게시글 등록 후 생성된 PK(id) 반환 */
    public long insert(Post p) {
        String sql = "INSERT INTO post (level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at) VALUES (?,?,?,?,?,?,?,0,0,NOW())";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getLevel());
            if (p.getParentId() == null) {
                ps.setObject(2, null);
            } else {
                ps.setLong(2, p.getParentId());
            }
            ps.setString(3, p.getAuthorLoginId());
            ps.setString(4, p.getTitle());
            ps.setString(5, p.getContent());
            ps.setString(6, p.getYoutubeUrl());
            ps.setString(7, p.getPlatform());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("No generated key returned");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> listByLevel(int level, int offset, int limit) {
        String sql = "SELECT id, level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at "
                   + "FROM post WHERE level=? AND parent_id IS NULL ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Post> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, level);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> listPopular(int offset, int limit) {
        String sql = "SELECT id, level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at "
                   + "FROM post WHERE parent_id IS NULL AND likes >= 10 "
                   + "ORDER BY (likes*10 + views) DESC, id DESC LIMIT ? OFFSET ?";
        List<Post> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Post findById(long id) {
        String sql = "SELECT id, level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at FROM post WHERE id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> listAnswers(long parentId) {
        String sql = "SELECT id, level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at "
                   + "FROM post WHERE parent_id=? ORDER BY id ASC";
        List<Post> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void increaseViews(long id) {
        String sql = "UPDATE post SET views = views + 1 WHERE id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> listByAuthor(String authorLoginId, int offset, int limit) {
        String sql = "SELECT id, level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at "
                   + "FROM post WHERE author_login_id=? AND parent_id IS NULL ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Post> out = new ArrayList<>();
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, authorLoginId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void increaseLikes(long id) {
        String sql = "UPDATE post SET likes = likes + 1 WHERE id=?";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> searchByLevel(int level, String q, int offset, int limit) {
        String sql = "SELECT id, level, parent_id, author_login_id, title, content, youtube_url, platform, views, likes, created_at "
                   + "FROM post "
                   + "WHERE level=? AND parent_id IS NULL AND (title LIKE ? OR content LIKE ?) "
                   + "ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Post> out = new ArrayList<>();
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        try (Connection con = DBCPUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, level);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLikedBy(long postId, String loginId) {
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

    private Post map(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setId(rs.getLong("id"));
        p.setLevel(rs.getInt("level"));
        Object parentObj = rs.getObject("parent_id");
        if (parentObj == null) {
            p.setParentId(null);
        } else {
            p.setParentId(((Number) parentObj).longValue());
        }
        p.setAuthorLoginId(rs.getString("author_login_id"));
        p.setTitle(rs.getString("title"));
        p.setContent(rs.getString("content"));
        p.setYoutubeUrl(rs.getString("youtube_url"));
        p.setPlatform(rs.getString("platform"));
        p.setViews(rs.getInt("views"));
        p.setLikes(rs.getInt("likes"));
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return p;
    }
}