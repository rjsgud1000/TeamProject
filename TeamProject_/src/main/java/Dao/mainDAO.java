package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Vo.mainVO;
import util.DBCPUtil;

//최신 게시글 가져오는 메소드

public class mainDAO {

    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;

    private void closeResource() {
        try { if(rs != null) rs.close(); } catch(Exception e) {}
        try { if(pstmt != null) pstmt.close(); } catch(Exception e) {}
        try { if(con != null) con.close(); } catch(Exception e) {}
    }

    public ArrayList<mainVO> mainList() {
        ArrayList<mainVO> list = new ArrayList<>();

        try {
            con = DBCPUtil.getConnection();

            String sql =
            	    "SELECT post_id, nickname, category, title, viewcount, create_at, is_deleted " +
            	    "FROM BOARD_POST " +
            	    "WHERE is_deleted = 0 " +
            	    "AND is_blinded = 0 " +
            	    "ORDER BY create_at DESC " +
            	    "LIMIT 5";

            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                mainVO vo = new mainVO();

                vo.setMa_post_id(rs.getInt("post_id"));
                vo.setMa_nickname(rs.getString("nickname"));
                vo.setMa_category(rs.getInt("category"));
                vo.setMa_title(rs.getString("title"));
                vo.setMa_viewcount(rs.getInt("viewcount"));
                vo.setMa_create_at(rs.getDate("create_at"));
                vo.setMa_is_deleted(rs.getInt("is_deleted"));

                list.add(vo);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }

        return list;
    }


public ArrayList<mainVO> noticeList(){

    ArrayList<mainVO> list = new ArrayList<>();

    try{

        con = DBCPUtil.getConnection();

        String sql = "SELECT post_id, nickname, category, title, viewcount, create_at, is_deleted "
                   + "FROM BOARD_POST "
                   + "WHERE category = 0 "
                   + "ORDER BY post_id DESC "
                   + "LIMIT 5";

        pstmt = con.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while(rs.next()){

            mainVO vo = new mainVO();

            vo.setMa_post_id(rs.getInt("post_id"));
            vo.setMa_nickname(rs.getString("nickname"));
            vo.setMa_category(rs.getInt("category"));
            vo.setMa_title(rs.getString("title"));
            vo.setMa_viewcount(rs.getInt("viewcount"));
            vo.setMa_create_at(rs.getDate("create_at"));
            vo.setMa_is_deleted(rs.getInt("is_deleted"));

            list.add(vo);
        }

    }catch(Exception e){
        e.printStackTrace();
    }finally{
        closeResource();
    }

    return list;
	}

public ArrayList<mainVO> popularList() {

    ArrayList<mainVO> list = new ArrayList<>();

    try {
        con = DBCPUtil.getConnection();

        String sql =
        	    "SELECT bp.post_id, bp.nickname, bp.category, bp.title, bp.viewcount, bp.create_at, bp.is_deleted, pl.like_count " +
        	    "FROM BOARD_POST bp " +
        	    "JOIN ( " +
        	    "    SELECT post_id, COUNT(*) AS like_count " +
        	    "    FROM POST_LIKE " +
        	    "    GROUP BY post_id " +
        	    "    HAVING COUNT(*) >= 5 " +
        	    ") pl ON bp.post_id = pl.post_id " +
        	    "WHERE bp.is_deleted = 0 " +
        	    "AND bp.is_blinded = 0 " +
        	    "ORDER BY bp.create_at DESC " +
        	    "LIMIT 5";

        pstmt = con.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            mainVO vo = new mainVO();

            vo.setMa_post_id(rs.getInt("post_id"));
            vo.setMa_nickname(rs.getString("nickname"));
            vo.setMa_category(rs.getInt("category"));
            vo.setMa_title(rs.getString("title"));
            vo.setMa_viewcount(rs.getInt("viewcount"));
            vo.setMa_create_at(rs.getDate("create_at"));
            vo.setMa_is_deleted(rs.getInt("is_deleted"));
            vo.setLike_count(rs.getInt("like_count"));

            list.add(vo);
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        closeResource();
    }

    return list;
}
}
