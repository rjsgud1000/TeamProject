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
		String normalizedFilter = normalizeFilter(filter);
		StringBuilder sql = new StringBuilder(
				"SELECT cr.report_id, cr.comment_id, c.post_id, cr.member_id AS report_member_id, "
				+ "c.member_id AS target_member_id, cr.reason, c.content AS comment_content, bp.title AS post_title, cr.created_at, cr.status "
				+ "FROM COMMENT_REPORT cr "
				+ "JOIN COMMENT c ON cr.comment_id = c.comment_id "
				+ "JOIN BOARD_POST bp ON c.post_id = bp.post_id ");
		appendFilterCondition(sql, normalizedFilter);
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
				vo.setStatus(normalizeStatus(rs.getString("status")));
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

	public int processCommentReport(int reportId, String action) {
		String normalizedAction = normalizeAction(action);
		if (reportId <= 0 || normalizedAction == null) {
			return 0;
		}

		String selectSql = "SELECT comment_id FROM COMMENT_REPORT WHERE report_id=? AND status='PENDING'";
		String blindSql = "UPDATE COMMENT SET is_deleted=1, updated_at=NOW() WHERE comment_id=? AND is_deleted=0";
		String reportSql = "UPDATE COMMENT_REPORT SET status=? WHERE report_id=? AND status='PENDING'";
		String nextStatus = "BLIND".equals(normalizedAction) ? "RESOLVED" : "REJECTED";

		try (Connection con = DBCPUtil.getConnection()) {
			con.setAutoCommit(false);
			try (
				PreparedStatement selectPstmt = con.prepareStatement(selectSql);
				PreparedStatement blindPstmt = con.prepareStatement(blindSql);
				PreparedStatement reportPstmt = con.prepareStatement(reportSql)
			) {
				selectPstmt.setInt(1, reportId);
				int commentId = 0;
				try (ResultSet rs = selectPstmt.executeQuery()) {
					if (rs.next()) {
						commentId = rs.getInt("comment_id");
					} else {
						con.rollback();
						return 0;
					}
				}

				if ("BLIND".equals(normalizedAction)) {
					blindPstmt.setInt(1, commentId);
					blindPstmt.executeUpdate();
				}

				reportPstmt.setString(1, nextStatus);
				reportPstmt.setInt(2, reportId);

				int updated = reportPstmt.executeUpdate();
				if (updated == 1) {
					con.commit();
					return 1;
				}
				con.rollback();
				return 0;
			} catch (Exception e) {
				con.rollback();
				throw e;
			} finally {
				con.setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private void appendFilterCondition(StringBuilder sql, String filter) {
		if ("PENDING".equals(filter)) {
			sql.append("WHERE cr.status = 'PENDING' ");
		} else if ("COMPLETED".equals(filter)) {
			sql.append("WHERE cr.status IN ('RESOLVED', 'REJECTED') ");
		}
	}

	private String normalizeFilter(String filter) {
		if (filter == null) {
			return "ALL";
		}
		String normalized = filter.trim().toUpperCase();
		if ("PENDING".equals(normalized) || "COMPLETED".equals(normalized)) {
			return normalized;
		}
		return "ALL";
	}

	private String normalizeAction(String action) {
		if (action == null) {
			return null;
		}
		String normalized = action.trim().toUpperCase();
		if ("BLIND".equals(normalized) || "REJECT".equals(normalized)) {
			return normalized;
		}
		return null;
	}

	private String normalizeStatus(String status) {
		if (status == null || status.trim().isEmpty()) {
			return "PENDING";
		}
		String normalized = status.trim().toUpperCase();
		if ("REJECTED".equals(normalized) || "RESOLVED".equals(normalized) || "PENDING".equals(normalized)) {
			return normalized;
		}
		return "PENDING";
	}
}