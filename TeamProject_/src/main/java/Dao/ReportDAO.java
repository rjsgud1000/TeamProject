package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import Vo.CommentReportVO;
import util.DBCPUtil;

public class ReportDAO {
	public List<CommentReportVO> findCommentReports() {
		return findCommentReports("ALL");
	}

	public List<CommentReportVO> findCommentReports(String filter) {
		List<CommentReportVO> reports = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT cr.report_id, cr.comment_id, c.post_id, cr.member_id AS report_member_id, "
				+ "c.member_id AS target_member_id, cr.reason, c.content AS comment_content, bp.title AS post_title, cr.created_at, cr.is_processed "
				+ "FROM COMMENT_REPORT cr "
				+ "JOIN COMMENT c ON cr.comment_id = c.comment_id "
				+ "JOIN BOARD_POST bp ON c.post_id = bp.post_id ");
		if ("PENDING".equals(filter)) {
			sql.append("WHERE cr.is_processed = 0 ");
		} else if ("COMPLETED".equals(filter)) {
			sql.append("WHERE cr.is_processed = 1 ");
		}
		sql.append("ORDER BY cr.created_at DESC, cr.report_id DESC");
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql.toString());
				 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				CommentReportVO vo = new CommentReportVO();
				vo.setReportId(rs.getInt("report_id"));
				vo.setCommentId(rs.getInt("comment_id"));
				vo.setPostId(rs.getInt("post_id"));
				vo.setReportMemberId(rs.getString("report_member_id"));
				vo.setTargetMemberId(rs.getString("target_member_id"));
				vo.setReason(rs.getString("reason"));
				vo.setCommentContent(rs.getString("comment_content"));
				vo.setPostTitle(rs.getString("post_title"));
				vo.setProcessed(rs.getBoolean("is_processed"));
				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) {
					vo.setCreatedAt(ts.toLocalDateTime());
				}
				reports.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reports;
	}

	public int markCommentReportProcessed(int reportId) {
		String sql = "UPDATE COMMENT_REPORT SET is_processed=1 WHERE report_id=? AND is_processed=0";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, reportId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}