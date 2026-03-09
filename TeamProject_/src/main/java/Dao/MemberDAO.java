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
		String sql = "SELECT REASON, end_at "
				+ "FROM SANCTION "
				+ "WHERE target_member_id=? AND member_status='BANNED' AND start_at<=NOW() AND end_at>=NOW() "
				+ "ORDER BY end_at DESC, action_id DESC LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					SanctionInfo info = new SanctionInfo();
					info.reason = rs.getString("REASON");
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

	public SanctionInfo findLatestSanction(String memberId) {
		String sql = "SELECT REASON, end_at FROM SANCTION WHERE target_member_id=? ORDER BY end_at DESC, action_id DESC LIMIT 1";
		try (Connection con = DBCPUtil.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					SanctionInfo info = new SanctionInfo();
					info.reason = rs.getString("REASON");
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

	private Timestamp safeTimestamp(ResultSet rs, String columnLabel) {
		try {
			return rs.getTimestamp(columnLabel);
		} catch (Exception ignore) {
			return null;
		}
	}

	private String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String v = s.trim();
		return v.isEmpty() ? null : v;
	}

	public static class SanctionInfo {
		public String reason;
		public LocalDateTime endAt;
	}
}