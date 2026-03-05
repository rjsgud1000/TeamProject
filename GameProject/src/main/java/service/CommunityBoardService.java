package service;

import java.util.List;

import dao.PostDAO;
import dao.PostLikeDAO;
import vo.Post;

/**
 * [역할] 커뮤니티 게시판 Service
 *
 * - 게시글 목록/검색/등록/조회수 증가/좋아요 토글 등의 정책을 담당합니다.
 * - Controller는 파라미터/권한 분기를 하고, 실제 정책/DB 작업은 Service/DAO로 위임합니다.
 *
 * [유지보수 포인트]
 * - 페이지네이션(page/pageSize) 정책 변경 시: offset 계산 로직 확인
 * - 좋아요 정책 변경 시: toggleLike()/PostLikeDAO
 */
public class CommunityBoardService {

    private final PostDAO postDAO = new PostDAO();
    private final PostLikeDAO postLikeDAO = new PostLikeDAO();

    public List<Post> listByLevel(int level, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return postDAO.listByLevel(level, offset, pageSize);
    }

    public List<Post> listPopular(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return postDAO.listPopular(offset, pageSize);
    }

    public long write(String authorLoginId, int level, String title, String content, String youtubeUrl, String platform) {
        Post p = new Post();
        p.setAuthorLoginId(authorLoginId);
        p.setLevel(level);
        p.setParentId(null);
        p.setTitle(title);
        p.setContent(content);
        p.setYoutubeUrl(youtubeUrl);
        p.setPlatform(platform);
        return postDAO.insert(p);
    }

    public long writeAnswer(String authorLoginId, long parentId, String content) {
        Post p = new Post();
        p.setAuthorLoginId(authorLoginId);
        p.setLevel(2);
        p.setParentId(parentId);
        p.setTitle("답변");
        p.setContent(content);
        p.setYoutubeUrl(null);
        p.setPlatform(null);
        return postDAO.insert(p);
    }

    public List<Post> listAnswers(long parentId) {
        return postDAO.listAnswers(parentId);
    }

    public Post getAndIncreaseViews(long id) {
        postDAO.increaseViews(id);
        return postDAO.findById(id);
    }

    public List<Post> listByAuthor(String authorLoginId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return postDAO.listByAuthor(authorLoginId, offset, pageSize);
    }

    public boolean toggleLike(long postId, String loginId) {
        if (loginId == null || loginId.trim().isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return postLikeDAO.toggle(postId, loginId.trim());
    }

    public boolean isLikedBy(long postId, String loginId) {
        if (loginId == null || loginId.trim().isEmpty()) return false;
        return postDAO.isLikedBy(postId, loginId.trim());
    }

    public void like(long postId) {
        // 기존 메서드는 외부 호출 호환용으로 남겨두되, 실제로는 토글로 유도해야 함
        postDAO.increaseLikes(postId);
    }

    public List<Post> searchByLevel(int level, String q, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return postDAO.searchByLevel(level, q, offset, pageSize);
    }
}