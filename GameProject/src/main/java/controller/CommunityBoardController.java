package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.CommentService;
import service.CommunityBoardService;
import service.MemberService;
import util.YoutubeUtil;
import vo.Comment;
import vo.Member;
import vo.Post;

/**
 * [역할] 커뮤니티 게시판 컨트롤러(MVC2)
 *
 * - 목록/상세/글쓰기/답변(Q&A)/좋아요 토글 등 게시판 기능의 라우팅을 담당합니다.
 * - Controller는 파라미터 수집/권한 분기/뷰 forward 중심이며, 정책/검증은 Service로 위임합니다.
 *
 * [Board level]
 *  0 = 공지(ADMIN)
 *  1 = 자유
 *  2 = Q&A (parent_id != null 인 경우 답변글)
 *  3 = 동영상(유튜브 URL을 embed URL로 변환)
 *  4 = 같이할 사람 찾기(플랫폼 배지)
 *  5 = 인기글(조회/좋아요 기반)
 *
 * [주요 URL]
 *  - GET  /community/list.do?level=&page=&q=
 *  - GET  /community/detail.do?id=
 *  - GET  /community/writeForm.do?level=
 *  - POST /community/write.do
 *  - POST /community/answer.do
 *  - POST /community/like.do
 *
 * [유지보수 포인트]
 * - level 정책/카테고리 추가 시: 여기 + list.jsp/detail.jsp/write.jsp + PostDAO 쿼리 동시 수정 가능
 */
@WebServlet("/community/*")
public class CommunityBoardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CommunityBoardService boardService = new CommunityBoardService();
    private final CommentService commentService = new CommentService();
    private final MemberService memberService = new MemberService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/list.do": {
                int level = parseInt(request.getParameter("level"), 1);
                int page = Math.max(1, parseInt(request.getParameter("page"), 1));
                int pageSize = 10;
                String q = request.getParameter("q");

                List<Post> posts;
                if (q != null && !q.trim().isEmpty() && level != 5) {
                    posts = boardService.searchByLevel(level, q, page, pageSize);
                } else if (level == 5) {
                    posts = boardService.listPopular(page, pageSize);
                } else {
                    posts = boardService.listByLevel(level, page, pageSize);
                }

                // 작성자 프로필(닉네임/프로필사진) 맵
                Map<String, Member> authorMap = new HashMap<>();
                if (posts != null) {
                    for (Post p : posts) {
                        String aid = p.getAuthorLoginId();
                        if (aid != null && !authorMap.containsKey(aid)) {
                            Member m = memberService.getByLoginId(aid);
                            if (m != null) authorMap.put(aid, m);
                        }
                    }
                }
                request.setAttribute("authorMap", authorMap);

                request.setAttribute("q", q);
                request.setAttribute("level", level);
                request.setAttribute("page", page);
                request.setAttribute("posts", posts);
                forward(request, response, "/WEB-INF/views/community/list.jsp");
                break;
            }
            case "/writeForm.do": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                int level = parseInt(request.getParameter("level"), 1);
                String role = (String) request.getSession().getAttribute("role");
                if (level == 0 && !"ADMIN".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                request.setAttribute("level", level);
                forward(request, response, "/WEB-INF/views/community/write.jsp");
                break;
            }
            case "/detail.do": {
                long postId = Long.parseLong(request.getParameter("id"));
                Post post = boardService.getAndIncreaseViews(postId);
                if (post == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // 현재 사용자 좋아요 여부
                String loginId = (request.getSession(false) == null) ? null : (String) request.getSession(false).getAttribute("id");
                boolean likedByMe = boardService.isLikedBy(postId, loginId);
                request.setAttribute("likedByMe", likedByMe);

                // 동영상 embed
                if (post.getLevel() == 3) {
                    String embedUrl = YoutubeUtil.toEmbedUrl(post.getYoutubeUrl());
                    request.setAttribute("youtubeEmbedUrl", embedUrl);
                }

                // 댓글
                List<Comment> comments = commentService.listByPostId(postId);
                request.setAttribute("comments", comments);

                // 작성자 정보
                Member author = memberService.getByLoginId(post.getAuthorLoginId());
                request.setAttribute("author", author);

                // 댓글 작성자 맵
                Map<String, Member> commentAuthorMap = new HashMap<>();
                if (comments != null) {
                    for (Comment c : comments) {
                        String aid = c.getAuthorLoginId();
                        if (aid != null && !commentAuthorMap.containsKey(aid)) {
                            Member m = memberService.getByLoginId(aid);
                            if (m != null) commentAuthorMap.put(aid, m);
                        }
                    }
                }
                request.setAttribute("commentAuthorMap", commentAuthorMap);

                // Q&A 답변글
                if (post.getLevel() == 2 && post.getParentId() == null) {
                    List<Post> answers = boardService.listAnswers(postId);
                    request.setAttribute("answers", answers);

                    Map<Long, Member> answerAuthorMap = new HashMap<>();
                    if (answers != null) {
                        for (Post a : answers) {
                            Member m = memberService.getByLoginId(a.getAuthorLoginId());
                            if (m != null) answerAuthorMap.put(a.getId(), m);
                        }
                    }
                    request.setAttribute("answerAuthorMap", answerAuthorMap);
                }

                request.setAttribute("post", post);
                forward(request, response, "/WEB-INF/views/community/detail.jsp");
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/write.do": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                String loginId = (String) request.getSession().getAttribute("id");
                String role = (String) request.getSession().getAttribute("role");

                int level = parseInt(request.getParameter("level"), 1);
                if (level == 0 && !"ADMIN".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                String title = request.getParameter("title");
                String content = request.getParameter("content");
                String youtubeUrl = request.getParameter("youtubeUrl");
                String platform = request.getParameter("platform");

                long newId = boardService.write(loginId, level, title, content, youtubeUrl, platform);
                response.sendRedirect(request.getContextPath() + "/community/detail.do?id=" + newId);
                break;
            }
            case "/answer.do": {
                // Q&A 답변
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                long parentId = Long.parseLong(request.getParameter("parentId"));
                String content = request.getParameter("content");
                String loginId = (String) request.getSession().getAttribute("id");

                boardService.writeAnswer(loginId, parentId, content);
                response.sendRedirect(request.getContextPath() + "/community/detail.do?id=" + parentId);
                break;
            }
            case "/like.do": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                long postId = Long.parseLong(request.getParameter("postId"));
                String loginId = (String) request.getSession().getAttribute("id");
                boardService.toggleLike(postId, loginId);
                response.sendRedirect(request.getContextPath() + "/community/detail.do?id=" + postId);
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(path);
        rd.forward(request, response);
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private void ensureLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("id") == null) {
            response.sendRedirect(request.getContextPath() + "/member/loginForm.me");
        }
    }
}
