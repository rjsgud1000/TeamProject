package Vo;

import java.util.Date;

public class CommentVO {
    private String commentId;   // 댓글 고유 ID
    private String postId;      // 게시글 ID (게시글 준비되면 연결)
    private String memberId;    // 작성자 ID
    private String content;     // 댓글 내용
    private Date createdAt;     // 작성일
    private Date updatedAt;     // 수정일 (선택)

    // 생성자
    public CommentVO() {}

    // Getter / Setter
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}