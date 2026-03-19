package Controller;

import Dao.BoardDAO;
import Vo.BoardPostVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/board/write")
public class BoardWriteController extends HttpServlet {

    private final BoardDAO boardDAO = new BoardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        boolean isAdmin = "admin".equalsIgnoreCase(memberId);

        String categoryParam = request.getParameter("category");
        String parentPostIdParam = request.getParameter("parentPostId");

        int category = 1;
        Integer parentPostId = null;

        try {
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                category = Integer.parseInt(categoryParam);
            }
        } catch (Exception e) {
            category = 1;
        }

        try {
            if (parentPostIdParam != null && !parentPostIdParam.trim().isEmpty()) {
                parentPostId = Integer.parseInt(parentPostIdParam);
            }
        } catch (Exception e) {
            parentPostId = null;
        }

        if (category == 0 && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=0");
            return;
        }

        request.setAttribute("category", category);
        request.setAttribute("parentPostId", parentPostId);
        request.setAttribute("isAnswerWrite", category == 2 && parentPostId != null);
        request.setAttribute("center", "boardWrite.jsp");
        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        String nickname = (String) session.getAttribute("loginName");

        boolean isAdmin = "admin".equalsIgnoreCase(memberId);

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryParam = request.getParameter("category");
        String parentPostIdParam = request.getParameter("parentPostId");

        int category = 1;
        Integer parentPostId = null;

        try {
            category = Integer.parseInt(categoryParam);
        } catch (Exception e) {
            category = 1;
        }

        try {
            if (parentPostIdParam != null && !parentPostIdParam.trim().isEmpty()) {
                parentPostId = Integer.parseInt(parentPostIdParam);
            }
        } catch (Exception e) {
            parentPostId = null;
        }

        if (category == 0 && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=0");
            return;
        }

        if (title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {

            request.setAttribute("errorMessage", "제목과 내용을 모두 입력해 주세요.");
            request.setAttribute("category", category);
            request.setAttribute("parentPostId", parentPostId);
            request.setAttribute("isAnswerWrite", category == 2 && parentPostId != null);
            request.setAttribute("center", "boardWrite.jsp");
            request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
            return;
        }

        BoardPostVO vo = new BoardPostVO();
        vo.setMemberId(memberId);
        vo.setNickname(nickname);
        vo.setCategory(category);
        vo.setTitle(title);
        vo.setContent(content);

        if (category == 2 && parentPostId != null) {
            vo.setAcceptedCommentId(parentPostId);
        } else {
            vo.setAcceptedCommentId(null);
        }

        if (category == 3) {
            String recruitStatusParam = request.getParameter("recruitStatus");
            String currentMembersParam = request.getParameter("currentMembers");
            String maxMembersParam = request.getParameter("maxMembers");

            int recruitStatus = 1;
            int currentMembers = 1;
            int maxMembers = 4;

            try { recruitStatus = Integer.parseInt(recruitStatusParam); } catch (Exception ignored) {}
            try { currentMembers = Integer.parseInt(currentMembersParam); } catch (Exception ignored) {}
            try { maxMembers = Integer.parseInt(maxMembersParam); } catch (Exception ignored) {}

            if (currentMembers < 1) currentMembers = 1;
            if (maxMembers < 1) maxMembers = 1;
            if (currentMembers > maxMembers) currentMembers = maxMembers;

            if (currentMembers >= maxMembers) {
                recruitStatus = 0;
            }

            vo.setRecruitStatus(recruitStatus);
            vo.setCurrentMembers(currentMembers);
            vo.setMaxMembers(maxMembers);
        } else {
            vo.setRecruitStatus(null);
            vo.setCurrentMembers(null);
            vo.setMaxMembers(null);
        }

        int result = boardDAO.insertPost(vo);

        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=" + category);
        } else {
            request.setAttribute("errorMessage", "게시글 등록에 실패했습니다.");
            request.setAttribute("category", category);
            request.setAttribute("parentPostId", parentPostId);
            request.setAttribute("isAnswerWrite", category == 2 && parentPostId != null);
            request.setAttribute("center", "boardWrite.jsp");
            request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
        }
    }
}