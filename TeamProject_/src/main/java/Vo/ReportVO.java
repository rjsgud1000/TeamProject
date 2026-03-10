package Vo;

import java.sql.Timestamp;

public class ReportVO {
	private int reportId;
	private int commentId;
	private int postId;
	private String reportMemberId;
	private String reportMemberNickname;
	private String targetMemberId;
	private String targetMemberNickname;
	private String reason;
	private String commentContent;
	private boolean commentDeleted;
	private Timestamp createdAt;

	public int getReportId() { return reportId; }
	public void setReportId(int reportId) { this.reportId = reportId; }

	public int getCommentId() { return commentId; }
	public void setCommentId(int commentId) { this.commentId = commentId; }

	public int getPostId() { return postId; }
	public void setPostId(int postId) { this.postId = postId; }

	public String getReportMemberId() { return reportMemberId; }
	public void setReportMemberId(String reportMemberId) { this.reportMemberId = reportMemberId; }

	public String getReportMemberNickname() { return reportMemberNickname; }
	public void setReportMemberNickname(String reportMemberNickname) { this.reportMemberNickname = reportMemberNickname; }

	public String getTargetMemberId() { return targetMemberId; }
	public void setTargetMemberId(String targetMemberId) { this.targetMemberId = targetMemberId; }

	public String getTargetMemberNickname() { return targetMemberNickname; }
	public void setTargetMemberNickname(String targetMemberNickname) { this.targetMemberNickname = targetMemberNickname; }

	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }

	public String getCommentContent() { return commentContent; }
	public void setCommentContent(String commentContent) { this.commentContent = commentContent; }

	public boolean isCommentDeleted() { return commentDeleted; }
	public void setCommentDeleted(boolean commentDeleted) { this.commentDeleted = commentDeleted; }

	public Timestamp getCreatedAt() { return createdAt; }
	public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
