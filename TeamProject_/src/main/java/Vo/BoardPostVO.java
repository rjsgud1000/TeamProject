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

    private Integer acceptedCommentId;   // 질문글 post_id
    private boolean answerPost;          // 답변글 여부

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
}