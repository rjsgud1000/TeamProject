package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
					vo.setUsername(rs.getString("username"));
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
		String sql = "SELECT member_id, username, password_hash, nickname, role, status "
				+ "FROM MEMBER WHERE member_id=?";
		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					MemberVO vo = new MemberVO();
					vo.setMemberId(rs.getString("member_id"));
					vo.setUsername(rs.getString("username"));
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
}