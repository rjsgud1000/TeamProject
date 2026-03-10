package Dao;

import Vo.BoardPostVO;
import util.DBCPUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Dto.BoardDTO;

public class BoardDAO {

    // 1. 전체보기용
    public List<BoardPostVO> selectAllBoardList() {
        List<BoardPostVO> list = new ArrayList<>();

        String sql = "SELECT post_id, member_id, nickname, category, title, content, viewcount, create_at " +
                     "FROM BOARD_POST " +
                     "WHERE category IN (1, 2, 3) AND is_deleted = 0 " +
                     "ORDER BY post_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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

                list.add(vo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 2. 카테고리별 조회용
    public List<BoardPostVO> selectBoardList(int category) {
        List<BoardPostVO> list = new ArrayList<>();

        String sql = "SELECT post_id, member_id, nickname, category, title, content, viewcount, create_at " +
                     "FROM BOARD_POST " +
                     "WHERE category = ? AND is_deleted = 0 " +
                     "ORDER BY post_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category);

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

                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 3. 게시글 상세보기
    public BoardDTO selectPostById(int postId) {
        String sql = "SELECT post_id, category, nickname, title, content, viewcount, create_at " +
                     "FROM BOARD_POST " +
                     "WHERE post_id = ? AND is_deleted = 0";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BoardDTO dto = new BoardDTO();
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setCategory(String.valueOf(rs.getInt("category")));
                    dto.setWriter(rs.getString("nickname"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setViewCount(rs.getInt("viewcount"));
                    dto.setCreatedAt(rs.getTimestamp("create_at"));
                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 4. 조회수 증가
    public void increaseViewCount(int postId) {
        String sql = "UPDATE BOARD_POST SET viewcount = viewcount + 1 WHERE post_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// 5. 게시글 작성
	public int insertPost(BoardPostVO vo) {
		int result = 0;

		String sql = "INSERT INTO BOARD_POST "
				+ "(member_id, nickname, category, title, content, viewcount, create_at, is_deleted) "
				+ "VALUES (?, ?, ?, ?, ?, 0, NOW(), 0)";

		try (Connection conn = DBCPUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, vo.getMemberId());
			pstmt.setString(2, vo.getNickname());
			pstmt.setInt(3, vo.getCategory());
			pstmt.setString(4, vo.getTitle());
			pstmt.setString(5, vo.getContent());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}