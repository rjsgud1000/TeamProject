package Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dao.MemberDAO;
import Dao.MyPostsDAO;
import Vo.BoardPostVO;
import Vo.MemberVO;

@WebServlet("/mypage/posts")
public class MyPostsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 10;
    private static final int PAGE_BLOCK_SIZE = 10;

    private final MyPostsDAO myPostsDAO = new MyPostsDAO();
    private final MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MemberVO loginMember = getLoginMember(request);
        if (loginMember == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.me");
            return;
        }

        MemberVO memberDetail = memberDAO.findByMemberId(loginMember.getMemberId());
        String memberId = loginMember.getMemberId();
        String nickname = memberDetail != null ? memberDetail.getNickname() : null;

        int myPostCount = myPostsDAO.countPostsByMember(memberId, nickname);
        int totalPage = Math.max(1, (int) Math.ceil(myPostCount / (double) PAGE_SIZE));
        int currentPage = parsePage(request.getParameter("page"), totalPage);
        int offset = (currentPage - 1) * PAGE_SIZE;
        List<BoardPostVO> myPostList = myPostsDAO.findPostsByMember(memberId, nickname, offset, PAGE_SIZE);

        int startPage = ((currentPage - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;
        int endPage = Math.min(startPage + PAGE_BLOCK_SIZE - 1, totalPage);

        request.setAttribute("myPostList", myPostList);
        request.setAttribute("myPostCount", myPostCount);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("debugMemberId", memberId);
        request.setAttribute("debugNickname", nickname);
        request.setAttribute("debugListSize", myPostList != null ? myPostList.size() : -1);
        request.setAttribute("debugSource", "MyPostsController/MyPostsDAO");
        request.setAttribute("pageTitle", "내가 쓴 글 전체보기");
        request.setAttribute("center", "members/myPosts.jsp");
        forward(request, response, "/main.jsp");
    }

    private int parsePage(String pageParam, int totalPage) {
        int page = 1;
        try {
            page = Integer.parseInt(pageParam);
        } catch (Exception ignore) {
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        return page;
    }

    private MemberVO getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object loginMember = session.getAttribute("loginMember");
        return (loginMember instanceof MemberVO) ? (MemberVO) loginMember : null;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}