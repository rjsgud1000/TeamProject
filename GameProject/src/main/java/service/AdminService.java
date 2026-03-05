package service;

import java.time.LocalDateTime;
import java.util.List;

import dao.MemberDAO;
import vo.Member;

/**
 * [역할] 관리자 기능용 Service
 *
 * - AdminController에서 호출하는 회원 관리(조회/수정/삭제/제재)의 비즈니스 로직을 담당합니다.
 * - 현재는 단순 DAO 위임 수준이며, 정책이 늘어나면(예: 삭제 전 감사 로그) 여기서 확장합니다.
 */
public class AdminService {

    private final MemberDAO memberDAO = new MemberDAO();

    public List<Member> listMembers() {
        return memberDAO.listAll();
    }

    public Member getMember(String loginId) {
        return memberDAO.findByLoginId(loginId);
    }

    public void updateMember(String loginId, String nickname, String role) {
        memberDAO.updateByAdmin(loginId, nickname, role);
    }

    public void deleteMember(String loginId) {
        memberDAO.deleteByLoginId(loginId);
    }

    /**
     * 회원 제재
     * @param days 제재 일수(오늘 기준 days만큼 더한 날짜까지 차단)
     */
    public void sanctionMember(String loginId, int days) {
        LocalDateTime until = LocalDateTime.now().plusDays(days);
        memberDAO.updateSanctionUntil(loginId, until);
    }
}