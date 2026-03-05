package Dao;

import Vo.BoardVO;
import util.DBCPUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {

    // ===== 게시글 전체 조회 =====
    public List<BoardVO> getBoardList() {
        List<BoardVO> list = new ArrayList<>();
        String sql = "SELECT board_id, title, content, writer, created_date, view_count FROM board ORDER BY board_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BoardVO board = new BoardVO();
                board.setBoardId(rs.getInt("board_id"));
                board.setTitle(rs.getString("title"));
                board.setContent(rs.getString("content"));
                board.setWriterId(rs.getString("writer"));
                board.setRegDate(rs.getString("created_date"));
                board.setViewCount(rs.getInt("view_count"));
                list.add(board);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== 게시글 단일 조회 =====
    public BoardVO getBoard(int boardId) {
        BoardVO board = null;
        String sql = "SELECT board_id, title, content, writer, created_date, view_count FROM board WHERE board_id=?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    board = new BoardVO();
                    board.setBoardId(rs.getInt("board_id"));
                    board.setTitle(rs.getString("title"));
                    board.setContent(rs.getString("content"));
                    board.setWriterId(rs.getString("writer"));
                    board.setRegDate(rs.getString("created_date"));
                    board.setViewCount(rs.getInt("view_count"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return board;
    }

    // ===== 게시글 작성 =====
    public boolean insertBoard(BoardVO board) {
        String sql = "INSERT INTO board(title, content, writer, created_date, view_count) VALUES (?, ?, ?, NOW(), 0)";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, board.getTitle());
            ps.setString(2, board.getContent());
            ps.setString(3, board.getWriterId());

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== 조회수 증가 =====
    public void increaseViewCount(int boardId) {
        String sql = "UPDATE board SET view_count = view_count + 1 WHERE board_id=?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, boardId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== 게시글 삭제 =====
    public boolean deleteBoard(int boardId) {
        String sql = "DELETE FROM board WHERE board_id=?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, boardId);
            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}