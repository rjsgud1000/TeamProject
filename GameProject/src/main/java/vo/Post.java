package vo;

/**
 * [역할] 게시글 VO
 *
 * - post 테이블 레코드 표현
 * - Q&A 답변글은 parentId가 질문글 id를 가리킵니다.
 *
 * [유지보수 포인트]
 * - level 의미가 바뀌면 Controller/Service/JSP/DAO 전 영역에 영향이 있습니다.
 */
public class Post {
    private long id;
    private int level;
    private Long parentId; // Q&A 답변글인 경우 질문글 id
    private String authorLoginId;
    private String title;
    private String content;
    private String youtubeUrl;
    private String platform;
    private int views;
    private int likes;
    private LocalDateTime createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAuthorLoginId() {
        return authorLoginId;
    }

    public void setAuthorLoginId(String authorLoginId) {
        this.authorLoginId = authorLoginId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}