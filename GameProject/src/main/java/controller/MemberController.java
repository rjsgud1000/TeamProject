package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import service.CommentService;
import service.CommunityBoardService;
import service.MemberService;
import util.ProfileImageUtil;
import vo.Comment;
import vo.Member;
import vo.Post;

/**
 * MVC2 Controller for member features.
 *
 * URL patterns:
 *  - /member/joinForm.me
 *  - /member/join.me
 *  - /member/loginForm.me
 *  - /member/login.me
 *  - /member/logout.me
 *  - /member/myPage.me
 *  - /member/profileForm.me
 *  - /member/profileUpdate.me
 *  - /member/myPosts.me
 *  - /member/myComments.me
 */
@WebServlet("/member/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class MemberController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final MemberService memberService = new MemberService();
    private final CommunityBoardService boardService = new CommunityBoardService();
    private final CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/joinForm.me":
                forward(request, response, "/WEB-INF/views/member/join.jsp");
                break;
            case "/loginForm.me":
                forward(request, response, "/WEB-INF/views/member/login.jsp");
                break;
            case "/logout.me":
                HttpSession session = request.getSession(false);
                if (session != null) session.invalidate();
                response.sendRedirect(request.getContextPath() + "/community/list.do?level=5");
                break;
            case "/myPage.me":
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                String loginIdForPage = (String) request.getSession().getAttribute("id");
                Member me = memberService.getByLoginId(loginIdForPage);
                request.setAttribute("me", me);
                forward(request, response, "/WEB-INF/views/member/mypage.jsp");
                break;
            case "/profileForm.me":
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                Member me2 = memberService.getByLoginId((String) request.getSession().getAttribute("id"));
                request.setAttribute("me", me2);
                forward(request, response, "/WEB-INF/views/member/profile.jsp");
                break;
            case "/myPosts.me": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                int page = Math.max(1, parseInt(request.getParameter("page"), 1));
                int pageSize = 10;
                String loginIdForPosts = (String) request.getSession().getAttribute("id");

                List<Post> posts = boardService.listByAuthor(loginIdForPosts, page, pageSize);
                request.setAttribute("posts", posts);
                request.setAttribute("page", page);
                forward(request, response, "/WEB-INF/views/member/myPosts.jsp");
                break;
            }
            case "/myComments.me": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                int page = Math.max(1, parseInt(request.getParameter("page"), 1));
                int pageSize = 10;
                String loginIdForComments = (String) request.getSession().getAttribute("id");

                List<Comment> comments = commentService.listByAuthor(loginIdForComments, page, pageSize);
                request.setAttribute("comments", comments);
                request.setAttribute("page", page);
                forward(request, response, "/WEB-INF/views/member/myComments.jsp");
                break;
            }
            case "/viewProfile.me": {
                String loginId = request.getParameter("id");
                Member m = memberService.getPublicProfile(loginId);
                if (m == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("profile", m);
                forward(request, response, "/WEB-INF/views/member/viewProfile.jsp");
                break;
            }
            case "/checkLoginId.me": {
                response.setContentType("application/json; charset=UTF-8");
                String loginId = request.getParameter("loginId");
                boolean ok = memberService.isLoginIdAvailable(loginId);
                response.getWriter().write("{\"ok\":" + ok + "}");
                break;
            }
            case "/checkNickname.me": {
                response.setContentType("application/json; charset=UTF-8");
                String nickname = request.getParameter("nickname");
                boolean ok = memberService.isNicknameAvailable(nickname);
                response.getWriter().write("{\"ok\":" + ok + "}");
                break;
            }
            case "/checkEmail.me": {
                response.setContentType("application/json; charset=UTF-8");
                String email = request.getParameter("email");
                boolean ok = memberService.isEmailAvailable(email);
                response.getWriter().write("{\"ok\":" + ok + "}");
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/join.me": {
                String loginId = request.getParameter("loginId");
                String password = request.getParameter("password");
                String nickname = request.getParameter("nickname");
                String email = request.getParameter("email");

                try {
                    memberService.register(loginId, password, nickname, email);
                    response.sendRedirect(request.getContextPath() + "/member/loginForm.me");
                } catch (IllegalArgumentException ex) {
                    request.setAttribute("error", ex.getMessage());
                    forward(request, response, "/WEB-INF/views/member/join.jsp");
                }
                break;
            }
            case "/login.me": {
                String loginId = request.getParameter("loginId");
                String password = request.getParameter("password");

                service.LoginResult result = memberService.loginDetailed(loginId, password);
                if (result.getStatus() == service.LoginResult.Status.BLOCKED_UNTIL) {
                    String untilStr = memberService.formatBlockedUntil(result.getBlockedUntil());
                    request.setAttribute("error", "당신은 " + untilStr + "까지 임시차단 되었습니다.");
                    forward(request, response, "/WEB-INF/views/member/login.jsp");
                    return;
                }
                if (result.getStatus() != service.LoginResult.Status.SUCCESS) {
                    request.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                    forward(request, response, "/WEB-INF/views/member/login.jsp");
                    return;
                }

                Member member = result.getMember();

                HttpSession session = request.getSession(true);
                session.setAttribute("id", member.getLoginId());
                session.setAttribute("role", member.getRole()); // ADMIN / USER

                response.sendRedirect(request.getContextPath() + "/community/list.do?level=5");
                break;
            }
            case "/profileUpdate.me": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                String loginId = (String) request.getSession().getAttribute("id");
                String nickname = request.getParameter("nickname");
                String newPassword = request.getParameter("newPassword");

                try {
                    memberService.updateProfile(loginId, nickname, newPassword);
                    response.sendRedirect(request.getContextPath() + "/member/myPage.me");
                } catch (IllegalArgumentException ex) {
                    request.setAttribute("error", ex.getMessage());
                    Member me = memberService.getByLoginId(loginId);
                    request.setAttribute("me", me);
                    forward(request, response, "/WEB-INF/views/member/profile.jsp");
                }
                break;
            }
            case "/profileImageUpload.me": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                String loginId = (String) request.getSession().getAttribute("id");
                Part file = request.getPart("profileImage");
                if (file == null || file.getSize() == 0) {
                    response.sendRedirect(request.getContextPath() + "/member/profileForm.me");
                    return;
                }

                String ext = ProfileImageUtil.safeExt(file.getContentType());
                if (ext == null) {
                    request.setAttribute("error", "이미지 파일만 업로드할 수 있어요(png/jpg/gif/webp)." );
                    Member me = memberService.getByLoginId(loginId);
                    request.setAttribute("me", me);
                    forward(request, response, "/WEB-INF/views/member/profile.jsp");
                    return;
                }

                String uploadDirReal = getServletContext().getRealPath("/uploads/profile");
                if (uploadDirReal == null) {
                    throw new ServletException("Upload path not resolved");
                }
                Path uploadDir = Paths.get(uploadDirReal);
                Files.createDirectories(uploadDir);

                String base = ProfileImageUtil.sha256Hex(loginId + "|" + System.currentTimeMillis());
                String fileName = base + "." + ext;
                Path target = uploadDir.resolve(fileName);

                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }

                memberService.updateProfileImage(loginId, fileName);
                response.sendRedirect(request.getContextPath() + "/member/profileForm.me");
                break;
            }
            case "/profileImageDelete.me": {
                ensureLogin(request, response);
                if (response.isCommitted()) return;

                String loginId = (String) request.getSession().getAttribute("id");
                Member me = memberService.getByLoginId(loginId);

                // DB 먼저 제거
                memberService.updateProfileImage(loginId, null);

                // 파일도 있다면 삭제(실패해도 UX에 영향 없게)
                try {
                    if (me != null && me.getProfileImage() != null && !me.getProfileImage().trim().isEmpty()) {
                        String uploadDirReal = getServletContext().getRealPath("/uploads/profile");
                        if (uploadDirReal != null) {
                            Path p = Paths.get(uploadDirReal).resolve(me.getProfileImage());
                            Files.deleteIfExists(p);
                        }
                    }
                } catch (Exception ignore) {}

                response.sendRedirect(request.getContextPath() + "/member/profileForm.me");
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

    private void ensureLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id") == null) {
            response.sendRedirect(request.getContextPath() + "/member/loginForm.me");
        }
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}