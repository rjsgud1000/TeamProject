package Dto;

import java.sql.Timestamp;

public class PostReportDTO {

    private int reportId;
    private int postId;
    private String reporterId;
    private String reporterNickname;
    private String postTitle;
    private String postWriterId;
    private String postWriterNickname;
    private String status;
    private Timestamp reportedAt;
    private Timestamp processedAt;
    private Integer processedBy;
    private String reason;
    private int blinded;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterNickname() {
        return reporterNickname;
    }

    public void setReporterNickname(String reporterNickname) {
        this.reporterNickname = reporterNickname;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostWriterId() {
        return postWriterId;
    }

    public void setPostWriterId(String postWriterId) {
        this.postWriterId = postWriterId;
    }

    public String getPostWriterNickname() {
        return postWriterNickname;
    }

    public void setPostWriterNickname(String postWriterNickname) {
        this.postWriterNickname = postWriterNickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(Timestamp reportedAt) {
        this.reportedAt = reportedAt;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    public Integer getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }
    
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public int getBlinded() {
        return blinded;
    }

    public void setBlinded(int blinded) {
        this.blinded = blinded;
    }
}