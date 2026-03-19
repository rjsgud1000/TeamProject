package Vo;

import java.sql.Timestamp;

public class BoardPostVO {
    private int postId;
    private String memberId;
    private String nickname;
    private int category;
    private String title;
    private String content;
    private int viewcount;
    private Timestamp createAt;
    private int likeCount;
    private int commentCount;

    private Integer acceptedCommentId;
    private boolean answerPost;

    // 파티 모집용
    private Integer recruitStatus;   // 1: 모집중, 0: 모집완료
    private Integer currentMembers;  // 현재 인원
    private Integer maxMembers;      // 총 모집 인원

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getViewcount() { return viewcount; }
    public void setViewcount(int viewcount) { this.viewcount = viewcount; }

    public Timestamp getCreateAt() { return createAt; }
    public void setCreateAt(Timestamp createAt) { this.createAt = createAt; }

    public Integer getAcceptedCommentId() { return acceptedCommentId; }
    public void setAcceptedCommentId(Integer acceptedCommentId) { this.acceptedCommentId = acceptedCommentId; }

    public boolean isAnswerPost() { return answerPost; }
    public void setAnswerPost(boolean answerPost) { this.answerPost = answerPost; }

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
        return category == 3;
    }
}