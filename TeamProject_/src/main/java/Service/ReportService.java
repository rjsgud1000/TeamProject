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

	public boolean processCommentReport(int reportId) {
		if (reportId <= 0) {
			return false;
		}
		return reportDAO.markCommentReportProcessed(reportId) == 1;
	}
}