package Dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
		try (Connection con = DBCPUtil.getConnection()) {
			boolean hasStatusColumn = hasStatusColumn(con);
			StringBuilder sql = new StringBuilder(
					"SELECT cr.report_id, cr.comment_id, c.post_id, cr.member_id AS report_member_id, "
					+ "c.member_id AS target_member_id, cr.reason, c.content AS comment_content, bp.title AS post_title, cr.created_at, cr.is_processed");
			if (hasStatusColumn) {
				sql.append(", cr.status AS status ");
			} else {
				sql.append(", CASE WHEN cr.is_processed = 1 THEN 'RESOLVED' ELSE 'PENDING' END AS status ");
			}
			sql.append("FROM COMMENT_REPORT cr ")
			   .append("JOIN COMMENT c ON cr.comment_id = c.comment_id ")
			   .append("JOIN BOARD_POST bp ON c.post_id = bp.post_id ");
			if ("PENDING".equals(normalizedFilter)) {
				sql.append("WHERE cr.is_processed = 0 ");
			} else if ("COMPLETED".equals(normalizedFilter)) {
				sql.append("WHERE cr.is_processed = 1 ");
			}
			sql.append("ORDER BY cr.created_at DESC, cr.report_id DESC");

			try (PreparedStatement pstmt = con.prepareStatement(sql.toString());
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
					vo.setStatus(normalizeStatus(rs.getString("status"), rs.getBoolean("is_processed")));
					Timestamp ts = rs.getTimestamp("created_at");
					if (ts != null) {
						vo.setCreatedAt(ts.toLocalDateTime());
					}
					reports.add(vo);
				}
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

		String selectSql = "SELECT comment_id FROM COMMENT_REPORT WHERE report_id=? AND is_processed=0";
		String blindSql = "UPDATE COMMENT SET is_deleted=1, updated_at=NOW() WHERE comment_id=? AND is_deleted=0";

		try (Connection con = DBCPUtil.getConnection()) {
			boolean hasStatusColumn = hasStatusColumn(con);
			String reportSql = hasStatusColumn
					? "UPDATE COMMENT_REPORT SET is_processed=1, status=? WHERE report_id=? AND is_processed=0"
					: "UPDATE COMMENT_REPORT SET is_processed=1 WHERE report_id=? AND is_processed=0";
			String rejectedStatus = "REJECTED";
			String resolvedStatus = "RESOLVED";

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

				if (hasStatusColumn) {
					reportPstmt.setString(1, "BLIND".equals(normalizedAction) ? resolvedStatus : rejectedStatus);
					reportPstmt.setInt(2, reportId);
				} else {
					reportPstmt.setInt(1, reportId);
				}

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

	private boolean hasStatusColumn(Connection con) {
		try {
			DatabaseMetaData metaData = con.getMetaData();
			try (ResultSet rs = metaData.getColumns(con.getCatalog(), null, "COMMENT_REPORT", "status")) {
				if (rs.next()) {
					return true;
				}
			}
			try (ResultSet rs = metaData.getColumns(con.getCatalog(), null, "COMMENT_REPORT", "STATUS")) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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

	private String normalizeStatus(String status, boolean processed) {
		if (status == null || status.trim().isEmpty()) {
			return processed ? "RESOLVED" : "PENDING";
		}
		String normalized = status.trim().toUpperCase();
		if ("REJECTED".equals(normalized) || "RESOLVED".equals(normalized) || "PENDING".equals(normalized)) {
			return normalized;
		}
		return processed ? "RESOLVED" : "PENDING";
	}
}