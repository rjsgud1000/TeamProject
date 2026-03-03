package service;

import java.time.LocalDateTime;
import java.util.List;

import dao.MemberDAO;
import vo.Member;

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

    public void sanctionMember(String loginId, int days) {
        LocalDateTime until = LocalDateTime.now().plusDays(days);
        memberDAO.updateSanctionUntil(loginId, until);
    }
}