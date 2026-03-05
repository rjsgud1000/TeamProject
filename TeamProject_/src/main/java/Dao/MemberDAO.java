package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Vo.MemberVO;
import util.DBCPUtil;

public class MemberDAO {

	public MemberVO login(String username, String password) {
		String sql = "SELECT member_id, username, password_hash, nickname, role, status "
				+ "FROM MEMBER "
				+ "WHERE username=? AND password_hash=? AND status='ACTIVE'";

		try (Connection con = DBCPUtil.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, username);
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
}