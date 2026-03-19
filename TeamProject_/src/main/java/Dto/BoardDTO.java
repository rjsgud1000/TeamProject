package Dto;

import java.sql.Timestamp;

public class BoardDTO {
    private int postId;
    private String memberId;
    private String category;
    private String title;
    private String writer;
    private String content;
    private int viewCount;
    private Timestamp createdAt;

    private Integer acceptedCommentId;

    // 파티 모집용
    private Integer recruitStatus;   // 1: 모집중, 0: 모집완료
    private Integer currentMembers;  // 현재 인원
    private Integer maxMembers;      // 총 모집 인원

    public BoardDTO() {}

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAcceptedCommentId() {
        return acceptedCommentId;
    }

    public void setAcceptedCommentId(Integer acceptedCommentId) {
        this.acceptedCommentId = acceptedCommentId;
    }

    public boolean isAnswerPost() {
        return acceptedCommentId != null;
    }

    public Integer getRecruitStatus() {
        return recruitStatus;
    }

    public void setRecruitStatus(Integer recruitStatus) {
        this.recruitStatus = recruitStatus;
    }

    public Integer getCurrentMembers() {
        return currentMembers;
    }

    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public boolean isPartyPost() {
        return "3".equals(category);
    }
}