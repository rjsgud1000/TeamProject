package Vo;

public class BoardVO {
    private int boardId;        // 게시글 번호
    private String writerId;    // 작성자 아이디
    private String writerName;  // 작성자 이름 (닉네임)
    private String title;       // 제목
    private String content;     // 내용
    private String regDate;     // 작성일
    private int viewCount;      // 조회수
    private String status;      // 상태 (예: active, deleted)

    public BoardVO() { }

    // ===== Getters & Setters =====
    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
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

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}