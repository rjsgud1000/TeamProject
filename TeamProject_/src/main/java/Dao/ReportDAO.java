package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Vo.ReportVO;
import util.DBCPUtil;

public class ReportDAO {

	public List<ReportVO> getCommentReports() {
		List<ReportVO> reports = new ArrayList<>();
		String sql = "SELECT cr.report_id, cr.comment_id, c.post_id, cr.member_id AS report_member_id, "
				+ "rm.nickname AS report_member_nickname, c.member_id AS target_member_id, "
				+ "tm.nickname AS target_member_nickname, cr.reason, cr.created_at, c.content, c.is_deleted "
				+ "FROM COMMENT_REPORT cr "
				+ "JOIN COMMENT c ON cr.comment_id = c.comment_id "
				+ "LEFT JOIN MEMBER rm ON cr.member_id = rm.member_id "
				+ "LEFT JOIN MEMBER tm ON c.member_id = tm.member_id "
				+ "ORDER BY cr.created_at DESC, cr.report_id DESC";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql);
				 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				reports.add(mapReport(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reports;
	}
	
	public int countCommentReports() {
		String sql = "SELECT COUNT(*) FROM COMMENT_REPORT";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql);
				 ResultSet rs = pstmt.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private ReportVO mapReport(ResultSet rs) throws Exception {
		ReportVO vo = new ReportVO();
		vo.setReportId(rs.getInt("report_id"));
		vo.setCommentId(rs.getInt("comment_id"));
		vo.setPostId(rs.getInt("post_id"));
		vo.setReportMemberId(rs.getString("report_member_id"));
		vo.setReportMemberNickname(rs.getString("report_member_nickname"));
		vo.setTargetMemberId(rs.getString("target_member_id"));
		vo.setTargetMemberNickname(rs.getString("target_member_nickname"));
		vo.setReason(rs.getString("reason"));
		vo.setCreatedAt(rs.getTimestamp("created_at"));
		vo.setCommentContent(rs.getString("content"));
		vo.setCommentDeleted(rs.getBoolean("is_deleted"));
		return vo;
	}
}
