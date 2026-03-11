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

        // 로그인 안 한 경우 차단
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");

        // ★ 관리자 여부 체크 (현재는 admin 아이디 기준으로 처리)
        boolean isAdmin = "admin".equalsIgnoreCase(memberId);

        String categoryParam = request.getParameter("category");
        int category = 1;

        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                category = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                category = 1;
            }
        }

        // ★ 공지사항(category=0)은 관리자만 작성 가능
        if (category == 0 && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=0");
            return;
        }

        request.setAttribute("category", category);
        request.setAttribute("center", "boardWrite.jsp");
        request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // 로그인 안 한 경우 차단
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        String nickname = (String) session.getAttribute("loginName");

        // ★ 관리자 여부 체크
        boolean isAdmin = "admin".equalsIgnoreCase(memberId);

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryParam = request.getParameter("category");

        int category = 1;
        try {
            category = Integer.parseInt(categoryParam);
        } catch (Exception e) {
            category = 1;
        }

        // ★ 공지사항(category=0)은 관리자만 작성 가능
        if (category == 0 && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=0");
            return;
        }

        if (title == null || title.trim().isEmpty() ||
            content == null || content.trim().isEmpty()) {

            request.setAttribute("errorMessage", "제목과 내용을 모두 입력해 주세요.");
            request.setAttribute("category", category);
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

        int result = boardDAO.insertPost(vo);

        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/board/list?category=" + category);
        } else {
            request.setAttribute("errorMessage", "게시글 등록에 실패했습니다.");
            request.setAttribute("category", category);
            request.setAttribute("center", "boardWrite.jsp");
            request.getRequestDispatcher("/GameMain.jsp").forward(request, response);
        }
    }
}