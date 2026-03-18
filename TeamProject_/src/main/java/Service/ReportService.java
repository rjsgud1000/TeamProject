package Service;

import java.util.List;

import Dao.ReportDAO;
import Vo.CommentReportVO;

public class ReportService {
	private final ReportDAO reportDAO = new ReportDAO();

	public List<CommentReportVO> getCommentReports() {
		return getCommentReports("ALL");
	}

	public List<CommentReportVO> getCommentReports(String filter) {
		return reportDAO.findCommentReports(normalizeFilter(filter));
	}

	public String normalizeFilter(String filter) {
		if (filter == null) {
			return "ALL";
		}
		String normalized = filter.trim().toUpperCase();
		if ("PENDING".equals(normalized) || "COMPLETED".equals(normalized)) {
			return normalized;
		}
		return "ALL";
	}

	public String normalizeAction(String action) {
		if (action == null) {
			return null;
		}
		String normalized = action.trim().toUpperCase();
		if ("BLIND".equals(normalized) || "REJECT".equals(normalized)) {
			return normalized;
		}
		return null;
	}

	public boolean processCommentReport(int reportId, String action) {
		if (reportId <= 0 || normalizeAction(action) == null) {
			return false;
		}
		return reportDAO.processCommentReport(reportId, action) == 1;
	}
}