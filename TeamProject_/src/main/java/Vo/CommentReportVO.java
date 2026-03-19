package Vo;

import java.time.LocalDateTime;

public class CommentReportVO {
	private int reportId;
	private int commentId;
	private int postId;
	private String reportMemberId;
	private String targetMemberId;
	private String reason;
	private String commentContent;
	private String postTitle;
	private LocalDateTime createdAt;
	private String status;

	public int getReportId() { return reportId; }
	public void setReportId(int reportId) { this.reportId = reportId; }

	public int getCommentId() { return commentId; }
	public void setCommentId(int commentId) { this.commentId = commentId; }

	public int getPostId() { return postId; }
	public void setPostId(int postId) { this.postId = postId; }

	public String getReportMemberId() { return reportMemberId; }
	public void setReportMemberId(String reportMemberId) { this.reportMemberId = reportMemberId; }

	public String getTargetMemberId() { return targetMemberId; }
	public void setTargetMemberId(String targetMemberId) { this.targetMemberId = targetMemberId; }

	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }

	public String getCommentContent() { return commentContent; }
	public void setCommentContent(String commentContent) { this.commentContent = commentContent; }

	public String getPostTitle() { return postTitle; }
	public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public boolean isCompleted() {
		return "RESOLVED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status);
	}
}