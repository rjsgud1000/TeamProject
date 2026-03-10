package Service;

import java.util.List;

import Dao.ReportDAO;
import Vo.CommentReportVO;

public class ReportService {
	private final ReportDAO reportDAO = new ReportDAO();

	public List<CommentReportVO> getCommentReports() {
		return reportDAO.findCommentReports();
	}

	public boolean processCommentReport(int reportId) {
		if (reportId <= 0) {
			return false;
		}
		return reportDAO.markCommentReportProcessed(reportId) == 1;
	}
}