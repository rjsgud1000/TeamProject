package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Vo.MemberVO;
import util.DBCPUtil;

public class MemberDAO {

	// 로그인 회원 조회 메소드
	public MemberVO login(String memberId, String password) {
		String sql = "SELECT member_id, username, password_hash, nickname, role, status "
				+ "FROM MEMBER "
				+ "WHERE member_id=? AND password_hash=? AND status='ACTIVE'";

		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, memberId);
			pstmt.setString(2, password);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					MemberVO vo = new MemberVO();
					vo.setMemberId(rs.getString("member_id"));
					String username = rs.getString("username");
					vo.setUsername(username);
					vo.setNameReal(username);
					vo.setPasswordHash(rs.getString("password_hash"));
					vo.setNickname(rs.getString("nickname"));
					vo.setRole(rs.getString("role"));
					vo.setStatus(rs.getString("status"));
					return vo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 아이디 중복 확인 메소드
	public boolean existsMemberId(String memberId) {
		String sql = "SELECT 1 FROM MEMBER WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true; // 안전하게 막기
		}
	}

	// 이름 중복 확인 메소드
	public boolean existsUsername(String username) {
		String sql = "SELECT 1 FROM MEMBER WHERE username=?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true; // 안전하게 막기
		}
	}

	// 닉네임 중복 확인 메소드
	public boolean existsNickname(String nickname) {
		String sql = "SELECT 1 FROM MEMBER WHERE nickname=?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, nickname);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true; // 안전하게 막기
		}
	}

	// 본인 제외 닉네임 중복 확인 메소드
	public boolean existsNicknameExceptMemberId(String nickname, String memberId) {
		String sql = "SELECT 1 FROM MEMBER WHERE nickname=? AND member_id<>?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, nickname);
			pstmt.setString(2, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	// 이메일 중복 확인 메소드
	public boolean existsEmail(String email) {
		String sql = "SELECT 1 FROM MEMBER WHERE email=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	// 본인 제외 이메일 중복 확인 메소드
	public boolean existsEmailExceptMemberId(String email, String memberId) {
		String sql = "SELECT 1 FROM MEMBER WHERE email=? AND member_id<>?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, email);
			pstmt.setString(2, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	// 전화번호 중복 확인 메소드
	public boolean existsPhone(String phone) {
		String sql = "SELECT 1 FROM MEMBER WHERE phone=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, phone);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	// 본인 제외 전화번호 중복 확인 메소드
	public boolean existsPhoneExceptMemberId(String phone, String memberId) {
		String sql = "SELECT 1 FROM MEMBER WHERE phone=? AND member_id<>?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, phone);
			pstmt.setString(2, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	// 회원가입 정보 저장 메소드
	public int insertMember(MemberVO vo) {
		String sql = "INSERT INTO MEMBER (member_id, username, password_hash, nickname, zipcode, addr1, addr2, addr3, addr4, gender, email, phone, role, status, updated_at) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			int i = 1;
			pstmt.setString(i++, vo.getMemberId());
			pstmt.setString(i++, vo.getUsername());
			pstmt.setString(i++, vo.getPasswordHash());
			pstmt.setString(i++, vo.getNickname());

			pstmt.setString(i++, vo.getZipcode());
			pstmt.setString(i++, vo.getAddr1());
			pstmt.setString(i++, vo.getAddr2());
			pstmt.setString(i++, vo.getAddr3());
			pstmt.setString(i++, vo.getAddr4());
			pstmt.setString(i++, vo.getGender());
			pstmt.setString(i++, vo.getEmail());
			pstmt.setString(i++, vo.getPhone());

			pstmt.setString(i++, vo.getRole());
			pstmt.setString(i++, vo.getStatus());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 아이디로 회원 정보 조회 메소드
	public MemberVO findByMemberId(String memberId) {
		String sql = "SELECT member_id, username, password_hash, nickname, zipcode, addr1, addr2, addr3, addr4, gender, email, phone, role, status "
				+ "FROM MEMBER WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapMember(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 회원정보 수정 메소드
	public int updateProfile(MemberVO vo) {
		String sql = "UPDATE MEMBER SET nickname=?, zipcode=?, addr1=?, addr2=?, addr3=?, addr4=?, gender=?, email=?, phone=?, updated_at=NOW() WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			int i = 1;
			pstmt.setString(i++, vo.getNickname());
			pstmt.setString(i++, vo.getZipcode());
			pstmt.setString(i++, vo.getAddr1());
			pstmt.setString(i++, vo.getAddr2());
			pstmt.setString(i++, vo.getAddr3());
			pstmt.setString(i++, vo.getAddr4());
			pstmt.setString(i++, vo.getGender());
			pstmt.setString(i++, vo.getEmail());
			pstmt.setString(i++, vo.getPhone());
			pstmt.setString(i++, vo.getMemberId());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	// 비밀번호 해시 변경 메소드
	public int updatePasswordHash(String memberId, String newPasswordHash) {
		String sql = "UPDATE MEMBER SET password_hash=?, updated_at=NOW() WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, newPasswordHash);
			pstmt.setString(2, memberId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 현재 시점에 유효한 제재(BANNED) 정보를 조회합니다.
	 * 없으면 null.
	 */
	public SanctionInfo findActiveSanction(String memberId) {
		String sql = "SELECT TYPE, REASON, end_at, member_status "
				+ "FROM SANCTION "
				+ "WHERE target_member_id=? AND member_status='BANNED' AND start_at<=NOW() AND end_at>=NOW() "
				+ "ORDER BY end_at DESC, action_id DESC LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					SanctionInfo info = new SanctionInfo();
					info.type = rs.getString("TYPE");
					info.reason = rs.getString("REASON");
					info.memberStatus = rs.getString("member_status");
					Timestamp ts = rs.getTimestamp("end_at");
					info.endAt = (ts != null) ? ts.toLocalDateTime() : null;
					return info;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 활성 제재 여부 확인 메소드
	public boolean hasActiveBannedSanction(String memberId) {
		String sql = "SELECT 1 FROM SANCTION WHERE target_member_id=? AND member_status='BANNED' AND start_at<=NOW() AND end_at>=NOW() LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	// 최근 제재 만료 여부 확인 메소드
	public boolean isLatestBannedSanctionExpired(String memberId) {
		String sql = "SELECT end_at FROM SANCTION WHERE target_member_id=? AND TYPE='BAN' ORDER BY action_id DESC LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					return false;
				}
				Timestamp ts = rs.getTimestamp("end_at");
				return ts != null && ts.toLocalDateTime().isBefore(LocalDateTime.now());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 최근 제재 정보 조회 메소드
	public SanctionInfo findLatestBannedSanction(String memberId) {
		String sql = "SELECT TYPE, REASON, end_at, member_status FROM SANCTION WHERE target_member_id=? AND TYPE='BAN' ORDER BY action_id DESC LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					SanctionInfo info = new SanctionInfo();
					info.type = rs.getString("TYPE");
					info.reason = rs.getString("REASON");
					info.memberStatus = rs.getString("member_status");
					Timestamp ts = rs.getTimestamp("end_at");
					info.endAt = ts != null ? ts.toLocalDateTime() : null;
					return info;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 회원 상태 활성화 메소드
	public int activateMember(String memberId) {
		String sql = "UPDATE MEMBER SET status='ACTIVE', updated_at=NOW() WHERE member_id=? AND status<>'ACTIVE'";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 경고 제재 종료 메소드
	public int endWarningSanctions(String targetMemberId) {
		String sql = "UPDATE SANCTION SET member_status='END' WHERE target_member_id=? AND member_status='WARNING'";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, targetMemberId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 활성 제재 종료 메소드
	public int endActiveSanctions(String targetMemberId) {
		String sql = "UPDATE SANCTION SET member_status='END', end_at=NOW() WHERE target_member_id=? AND member_status IN ('WARNING','BANNED')";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, targetMemberId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 회원 탈퇴 처리 메소드
	public int withdrawMember(String memberId) {
		String sql = "UPDATE MEMBER SET status='WITHDRAWN', updated_at=NOW() WHERE member_id=? AND status<>'WITHDRAWN'";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 관리자용 회원 목록 조회 메소드
	public List<MemberVO> findMembers(String keyword, String status) {
		List<MemberVO> members = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT member_id, username, password_hash, nickname, zipcode, addr1, addr2, addr3, addr4, gender, email, phone, role, status, created_at, updated_at "
				+ "FROM MEMBER WHERE 1=1");
		String keywordTrim = trimToNull(keyword);
		String statusTrim = trimToNull(status);
		boolean hasKeyword = keywordTrim != null;
		boolean hasStatus = statusTrim != null && !"ALL".equalsIgnoreCase(statusTrim);
		if (hasKeyword) {
			sql.append(" AND (member_id LIKE ? OR username LIKE ? OR nickname LIKE ?)");
		}
		if (hasStatus) {
			sql.append(" AND status=?");
		}
		sql.append(" ORDER BY created_at DESC, member_id ASC");

		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql.toString())) {
			int i = 1;
			if (hasKeyword) {
				String like = "%" + keywordTrim + "%";
				pstmt.setString(i++, like);
				pstmt.setString(i++, like);
				pstmt.setString(i++, like);
			}
			if (hasStatus) {
				pstmt.setString(i++, statusTrim.toUpperCase());
			}
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					members.add(mapMember(rs));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return members;
	}

	// 상태별 회원 수 조회 메소드
	public int countMembersByStatus(String status) {
		String sql = "SELECT COUNT(*) FROM MEMBER WHERE status=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, status);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() ? rs.getInt(1) : 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 전체 회원 수 조회 메소드
	public int countMembers() {
		String sql = "SELECT COUNT(*) FROM MEMBER";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql);
				 ResultSet rs = pstmt.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 관리자용 회원 상세 조회 메소드
	public MemberVO findMemberDetailForAdmin(String memberId) {
		String sql = "SELECT member_id, username, password_hash, nickname, zipcode, addr1, addr2, addr3, addr4, gender, email, phone, role, status, created_at, updated_at "
				+ "FROM MEMBER WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				MemberVO vo = mapMember(rs);
				SanctionInfo info = findLatestSanction(memberId);
				if (info != null) {
					vo.setSanctionReason(info.reason);
					vo.setSanctionEndAt(info.endAt);
				}
				int[] counts = countSanctions(memberId);
				vo.setWarningCount(counts[0]);
				vo.setBannedCount(counts[1]);
				Timestamp createdAt = rs.getTimestamp("created_at");
				if (createdAt != null) {
					vo.setCreatedAt(createdAt.toLocalDateTime());
				}
				Timestamp updatedAt = rs.getTimestamp("updated_at");
				if (updatedAt != null) {
					vo.setUpdatedAt(updatedAt.toLocalDateTime());
				}
				return vo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 회원 제재 횟수 조회 메소드
	public int[] countSanctions(String memberId) {
		String sql = "SELECT TYPE, COUNT(*) cnt FROM SANCTION WHERE target_member_id=? GROUP BY TYPE";
		int warningCount = 0;
		int bannedCount = 0;
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String type = rs.getString("TYPE");
					int count = rs.getInt("cnt");
					if ("WARN".equalsIgnoreCase(type)) {
						warningCount = count;
					} else if ("BAN".equalsIgnoreCase(type)) {
						bannedCount = count;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new int[] { warningCount, bannedCount };
	}

	// 가장 최근 제재 정보 조회 메소드
	public SanctionInfo findLatestSanction(String memberId) {
		String sql = "SELECT TYPE, REASON, end_at, member_status FROM SANCTION WHERE target_member_id=? ORDER BY create_at DESC, action_id DESC LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					SanctionInfo info = new SanctionInfo();
					info.type = rs.getString("TYPE");
					info.reason = rs.getString("REASON");
					info.memberStatus = rs.getString("member_status");
					Timestamp ts = rs.getTimestamp("end_at");
					info.endAt = ts != null ? ts.toLocalDateTime() : null;
					return info;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 관리자 회원 상태 변경 메소드
	public int updateMemberStatus(String memberId, String status) {
		String sql = "UPDATE MEMBER SET status=?, updated_at=NOW() WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, status);
			pstmt.setString(2, memberId);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 관리자 제재 포함 상태 변경 메소드
	public boolean updateMemberStatusWithSanction(String targetMemberId, String adminMemberId, String nextStatus,
			String sanctionType, String reason, LocalDateTime startAt, LocalDateTime endAt, boolean endPreviousSanctions) {
		String updateMemberSql = "UPDATE MEMBER SET status=?, updated_at=NOW() WHERE member_id=?";
		String insertSanctionSql = "INSERT INTO SANCTION (target_member_id, admin_member_id, TYPE, REASON, start_at, end_at, member_status) VALUES (?,?,?,?,?,?,?)";
		String endSanctionSql = "UPDATE SANCTION SET member_status='END', end_at=NOW() WHERE target_member_id=? AND member_status IN ('WARNING','BANNED')";
		try (Connection con = DBCPUtil.getConnection()) {
			boolean oldAutoCommit = con.getAutoCommit();
			con.setAutoCommit(false);
			try {
				try (PreparedStatement pstmt = con.prepareStatement(updateMemberSql)) {
					pstmt.setString(1, nextStatus);
					pstmt.setString(2, targetMemberId);
					if (pstmt.executeUpdate() != 1) {
						con.rollback();
						return false;
					}
				}
				if (sanctionType != null) {
					try (PreparedStatement pstmt = con.prepareStatement(insertSanctionSql)) {
						int i = 1;
						pstmt.setString(i++, targetMemberId);
						pstmt.setString(i++, adminMemberId);
						pstmt.setString(i++, sanctionType);
						pstmt.setString(i++, reason);
						pstmt.setTimestamp(i++, Timestamp.valueOf(startAt));
						pstmt.setTimestamp(i++, Timestamp.valueOf(endAt));
						pstmt.setString(i++, nextStatus);
						if (pstmt.executeUpdate() != 1) {
							con.rollback();
							return false;
						}
					}
				}
				if (endPreviousSanctions) {
					try (PreparedStatement pstmt = con.prepareStatement(endSanctionSql)) {
						pstmt.setString(1, targetMemberId);
						pstmt.executeUpdate();
					}
				}
				con.commit();
				con.setAutoCommit(oldAutoCommit);
				return true;
			} catch (Exception e) {
				con.rollback();
				con.setAutoCommit(oldAutoCommit);
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 아이디와 이메일로 회원 조회 메소드
	public MemberVO findByMemberIdAndEmail(String memberId, String email) {
		String sql = "SELECT member_id, username, password_hash, nickname, zipcode, addr1, addr2, addr3, addr4, gender, email, phone, role, status "
				+ "FROM MEMBER WHERE member_id=? AND email=?";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			pstmt.setString(2, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapMember(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ResultSet -> MemberVO 매핑 메소드
	private MemberVO mapMember(ResultSet rs) throws Exception {
		MemberVO vo = new MemberVO();
		vo.setMemberId(rs.getString("member_id"));
		String username = rs.getString("username");
		vo.setUsername(username);
		vo.setNameReal(username);
		vo.setPasswordHash(rs.getString("password_hash"));
		vo.setNickname(rs.getString("nickname"));
		vo.setZipcode(rs.getString("zipcode"));
		vo.setAddr1(rs.getString("addr1"));
		vo.setAddr2(rs.getString("addr2"));
		vo.setAddr3(rs.getString("addr3"));
		vo.setAddr4(rs.getString("addr4"));
		vo.setGender(rs.getString("gender"));
		vo.setEmail(rs.getString("email"));
		vo.setPhone(rs.getString("phone"));
		vo.setRole(rs.getString("role"));
		vo.setStatus(rs.getString("status"));
		Timestamp createdAt = safeTimestamp(rs, "created_at");
		if (createdAt != null) {
			vo.setCreatedAt(createdAt.toLocalDateTime());
		}
		Timestamp updatedAt = safeTimestamp(rs, "updated_at");
		if (updatedAt != null) {
			vo.setUpdatedAt(updatedAt.toLocalDateTime());
		}
		return vo;
	}

	// 안전한 Timestamp 조회 메소드
	private Timestamp safeTimestamp(ResultSet rs, String columnLabel) {
		try {
			return rs.getTimestamp(columnLabel);
		} catch (Exception ignore) {
			return null;
		}
	}

	// 공백 문자열 null 변환 메소드
	private String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String v = s.trim();
		return v.isEmpty() ? null : v;
	}

	// 제재 정보 전달용 내부 클래스
	public static class SanctionInfo {
		public String type;
		public String reason;
		public String memberStatus;
		public LocalDateTime endAt;
	}
}