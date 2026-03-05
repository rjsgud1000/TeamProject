package Vo;

import java.sql.Timestamp;

public class QnaPostVO {
    private int postId;
    private String memberId;
    private String nickname;
    private String title;
    private int viewcount;
    private Timestamp createAt;
    private Integer acceptedCommentId; // null 가능

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getViewcount() { return viewcount; }
    public void setViewcount(int viewcount) { this.viewcount = viewcount; }

    public Timestamp getCreateAt() { return createAt; }
    public void setCreateAt(Timestamp createAt) { this.createAt = createAt; }

    public Integer getAcceptedCommentId() { return acceptedCommentId; }
    public void setAcceptedCommentId(Integer acceptedCommentId) { this.acceptedCommentId = acceptedCommentId; }
}