package vo;

/**
 * [역할] 댓글 VO
 * - comment 테이블 레코드 표현
 * - JSP에서는 줄바꿈/공백을 보존하기 위해 pre-wrap 스타일로 렌더링하는 경우가 있습니다.
 */
public class Comment {
    private long id;
    private long postId;
    private String authorLoginId;
    private String content;
    private LocalDateTime createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getAuthorLoginId() {
        return authorLoginId;
    }

    public void setAuthorLoginId(String authorLoginId) {
        this.authorLoginId = authorLoginId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}