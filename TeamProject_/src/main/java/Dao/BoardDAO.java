package Dao;

import Vo.BoardPostVO;
import util.DBCPUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Dto.BoardDTO;
import Dto.PostReportDTO;

public class BoardDAO {

    // 1. 전체보기용
    public List<BoardPostVO> selectAllBoardList() {
        List<BoardPostVO> list = new ArrayList<>();

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at, " +
                "       (SELECT COUNT(*) " +
                "        FROM COMMENT c " +
                "        WHERE c.post_id = bp.post_id " +
                "          AND c.is_deleted = 0 " +
                "          AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                // 삭제되지 않았고 블라인드되지 않은 게시글만 조회
                "WHERE bp.category IN (1, 2, 3) AND bp.is_deleted = 0 AND bp.is_blinded = 0 " +
                "ORDER BY bp.post_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BoardPostVO vo = new BoardPostVO();
                vo.setPostId(rs.getInt("post_id"));
                vo.setMemberId(rs.getString("member_id"));
                vo.setNickname(rs.getString("nickname"));
                vo.setCategory(rs.getInt("category"));
                vo.setTitle(rs.getString("title"));
                vo.setContent(rs.getString("content"));
                vo.setViewcount(rs.getInt("viewcount"));
                vo.setCreateAt(rs.getTimestamp("create_at"));
                vo.setCommentCount(rs.getInt("comment_count"));
                list.add(vo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 2. 카테고리별 조회용
    public List<BoardPostVO> selectBoardList(int category) {
        List<BoardPostVO> list = new ArrayList<>();

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at, " +
                "       (SELECT COUNT(*) " +
                "        FROM COMMENT c " +
                "        WHERE c.post_id = bp.post_id " +
                "          AND c.is_deleted = 0 " +
                "          AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                // 삭제되지 않았고 블라인드되지 않은 게시글만 조회
                "WHERE bp.category = ? AND bp.is_deleted = 0 AND bp.is_blinded = 0 " +
                "ORDER BY bp.post_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 3. 게시글 상세보기
    public BoardDTO selectPostById(int postId) {
        String sql = "SELECT post_id, member_id, category, nickname, title, content, "
                   + "viewcount, create_at, accepted_comment_id, "
                   + "recruit_status, current_members, max_members "
                   + "FROM BOARD_POST "
                   + "WHERE post_id = ? AND is_deleted = 0 AND is_blinded = 0";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BoardDTO dto = new BoardDTO();
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setCategory(String.valueOf(rs.getInt("category")));
                    dto.setWriter(rs.getString("nickname"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setViewCount(rs.getInt("viewcount"));
                    dto.setCreatedAt(rs.getTimestamp("create_at"));

                    int acceptedCommentId = rs.getInt("accepted_comment_id");
                    if (!rs.wasNull()) {
                        dto.setAcceptedCommentId(acceptedCommentId);
                    }

                    int recruitStatus = rs.getInt("recruit_status");
                    if (!rs.wasNull()) {
                        dto.setRecruitStatus(recruitStatus);
                    }

                    int currentMembers = rs.getInt("current_members");
                    if (!rs.wasNull()) {
                        dto.setCurrentMembers(currentMembers);
                    }

                    int maxMembers = rs.getInt("max_members");
                    if (!rs.wasNull()) {
                        dto.setMaxMembers(maxMembers);
                    }

                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 4. 조회수 증가
    public void increaseViewCount(int postId) {
        String sql = "UPDATE BOARD_POST SET viewcount = viewcount + 1 WHERE post_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. 게시글 작성
    public int insertPost(BoardPostVO vo) {
        int result = 0;

        String sql = "INSERT INTO BOARD_POST "
                   + "(member_id, nickname, category, title, content, viewcount, create_at, is_deleted, "
                   + "accepted_comment_id, recruit_status, current_members, max_members) "
                   + "VALUES (?, ?, ?, ?, ?, 0, NOW(), 0, ?, ?, ?, ?)";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vo.getMemberId());
            pstmt.setString(2, vo.getNickname());
            pstmt.setInt(3, vo.getCategory());
            pstmt.setString(4, vo.getTitle());
            pstmt.setString(5, vo.getContent());

            if (vo.getAcceptedCommentId() == null) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(6, vo.getAcceptedCommentId());
            }

            if (vo.getRecruitStatus() == null) {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(7, vo.getRecruitStatus());
            }

            if (vo.getCurrentMembers() == null) {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(8, vo.getCurrentMembers());
            }

            if (vo.getMaxMembers() == null) {
                pstmt.setNull(9, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(9, vo.getMaxMembers());
            }

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 6. 게시글 수정
    public void updatePost(int postId, String title, String content,
            Integer recruitStatus, Integer currentMembers, Integer maxMembers) {

		String sql = "UPDATE BOARD_POST "
		    + "SET title = ?, content = ?, "
		    + "    recruit_status = ?, current_members = ?, max_members = ?, "
		    + "    updated_at = NOW() "
		    + "WHERE post_id = ?";
		
				try (Connection conn = DBCPUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
				
				pstmt.setString(1, title);
				pstmt.setString(2, content);
				
				if (recruitStatus == null) pstmt.setNull(3, java.sql.Types.INTEGER);
				else pstmt.setInt(3, recruitStatus);
				
				if (currentMembers == null) pstmt.setNull(4, java.sql.Types.INTEGER);
				else pstmt.setInt(4, currentMembers);
				
				if (maxMembers == null) pstmt.setNull(5, java.sql.Types.INTEGER);
				else pstmt.setInt(5, maxMembers);
				
				pstmt.setInt(6, postId);
				
				pstmt.executeUpdate();
				
				} catch (Exception e) {
				e.printStackTrace();
				}
		}
    	//6-1 파티 모집 게시판 전용
		    public void togglePartyRecruitStatus(int postId) {
		        String selectSql = "SELECT recruit_status, current_members, max_members "
		                         + "FROM BOARD_POST "
		                         + "WHERE post_id = ?";
		
		        String updateSql = "UPDATE BOARD_POST "
		                         + "SET recruit_status = ?, current_members = ?, updated_at = NOW() "
		                         + "WHERE post_id = ?";
		
		        try (Connection conn = DBCPUtil.getConnection();
		             PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
		
		            selectPstmt.setInt(1, postId);
		
		            try (ResultSet rs = selectPstmt.executeQuery()) {
		                if (!rs.next()) {
		                    return;
		                }
		
		                int recruitStatus = rs.getInt("recruit_status");
		                int currentMembers = rs.getInt("current_members");
		                int maxMembers = rs.getInt("max_members");
		
		                if (maxMembers < 1) {
		                    maxMembers = 1;
		                }
		                if (currentMembers < 1) {
		                    currentMembers = 1;
		                }
		                if (currentMembers > maxMembers) {
		                    currentMembers = maxMembers;
		                }
		
		                int newRecruitStatus;
		                int newCurrentMembers;
		
		                if (recruitStatus == 1) {
		                    // 모집중 -> 모집완료
		                    newRecruitStatus = 0;
		                    newCurrentMembers = maxMembers;
		                } else {
		                    // 모집완료 -> 모집중
		                    newRecruitStatus = 1;
		                    newCurrentMembers = maxMembers - 1;
		
		                    if (newCurrentMembers < 1) {
		                        newCurrentMembers = 1;
		                    }
		                }
		
		                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
		                    updatePstmt.setInt(1, newRecruitStatus);
		                    updatePstmt.setInt(2, newCurrentMembers);
		                    updatePstmt.setInt(3, postId);
		                    updatePstmt.executeUpdate();
		                }
		            }
		
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		
		    public void updatePartyMembers(int postId, int currentMembers, int maxMembers) {
		        if (currentMembers < 1) currentMembers = 1;
		        if (maxMembers < 1) maxMembers = 1;
		        if (currentMembers > maxMembers) currentMembers = maxMembers;

		        int recruitStatus = (currentMembers >= maxMembers) ? 0 : 1;

		        String sql = "UPDATE BOARD_POST "
		                   + "SET current_members = ?, max_members = ?, recruit_status = ?, updated_at = NOW() "
		                   + "WHERE post_id = ?";

		        try (Connection conn = DBCPUtil.getConnection();
		             PreparedStatement pstmt = conn.prepareStatement(sql)) {

		            pstmt.setInt(1, currentMembers);
		            pstmt.setInt(2, maxMembers);
		            pstmt.setInt(3, recruitStatus);
		            pstmt.setInt(4, postId);

		            pstmt.executeUpdate();

		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }

    // 7. 게시글 삭제
    public void deletePost(int postId) {

        String sql = "UPDATE BOARD_POST SET is_deleted = 1 WHERE post_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 8. 전체글 페이징 조회
    public List<BoardPostVO> selectAllBoardListPaging(int startRow, int pageSize, String sort) {
        List<BoardPostVO> list = new ArrayList<>();

        String orderBy = "ORDER BY bp.post_id DESC";
        if ("view".equals(sort)) {
            orderBy = "ORDER BY bp.viewcount DESC, bp.post_id DESC";
        } else if ("like".equals(sort)) {
            orderBy = "ORDER BY like_count DESC, bp.post_id DESC";
        }

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                "bp.viewcount, bp.create_at, COUNT(pl.post_id) AS like_count, " +
                "(SELECT COUNT(*) FROM COMMENT c WHERE c.post_id = bp.post_id AND c.is_deleted = 0 AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "LEFT JOIN POST_LIKE pl ON bp.post_id = pl.post_id " +
                // 삭제되지 않았고 블라인드되지 않은 게시글만 페이징 조회
                "WHERE bp.category IN (1,2,3) AND bp.is_deleted = 0 AND bp.is_blinded = 0 " +
                "GROUP BY bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at " +
                orderBy + " " +
                "LIMIT ?, ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, startRow);
            pstmt.setInt(2, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setLikeCount(rs.getInt("like_count"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 9. 카테고리별 페이징 조회
    public List<BoardPostVO> selectBoardListPaging(int category, int startRow, int pageSize, String sort) {
        if (category == 2) {
            return selectQnaListPaging(startRow, pageSize, sort);
        }

        List<BoardPostVO> list = new ArrayList<>();

        String orderBy = "ORDER BY bp.post_id DESC";
        if ("view".equals(sort)) {
            orderBy = "ORDER BY bp.viewcount DESC, bp.post_id DESC";
        } else if ("like".equals(sort)) {
            orderBy = "ORDER BY like_count DESC, bp.post_id DESC";
        }

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                "bp.viewcount, bp.create_at, bp.recruit_status, bp.current_members, bp.max_members, " +
                "COUNT(pl.post_id) AS like_count, " +
                "(SELECT COUNT(*) FROM COMMENT c WHERE c.post_id = bp.post_id AND c.is_deleted = 0 AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "LEFT JOIN POST_LIKE pl ON bp.post_id = pl.post_id " +
                "WHERE bp.category = ? AND bp.is_deleted = 0 AND bp.is_blinded = 0 " +
                "GROUP BY bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                "bp.viewcount, bp.create_at, bp.recruit_status, bp.current_members, bp.max_members " +
                orderBy + " " +
                "LIMIT ?, ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category);
            pstmt.setInt(2, startRow);
            pstmt.setInt(3, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setLikeCount(rs.getInt("like_count"));
                    vo.setCommentCount(rs.getInt("comment_count"));

                    int recruitStatus = rs.getInt("recruit_status");
                    if (!rs.wasNull()) vo.setRecruitStatus(recruitStatus);

                    int currentMembers = rs.getInt("current_members");
                    if (!rs.wasNull()) vo.setCurrentMembers(currentMembers);

                    int maxMembers = rs.getInt("max_members");
                    if (!rs.wasNull()) vo.setMaxMembers(maxMembers);

                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 9-1 질문과 답변 게시판 한정 조회
    public int getQnaQuestionCount() {
        int count = 0;

        String sql = "SELECT COUNT(*) "
                   + "FROM BOARD_POST "
                   + "WHERE category = 2 "
                   + "AND is_deleted = 0 "
                   + "AND is_blinded = 0 "
                   + "AND accepted_comment_id IS NULL";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    
    public List<BoardPostVO> selectQnaListPaging(int startRow, int pageSize, String sort) {
        List<BoardPostVO> list = new ArrayList<>();
        List<Integer> questionIds = new ArrayList<>();

        String questionOrderBy = "ORDER BY q.post_id DESC";
        if ("view".equals(sort)) {
            questionOrderBy = "ORDER BY q.viewcount DESC, q.post_id DESC";
        } else if ("like".equals(sort)) {
            questionOrderBy = "ORDER BY like_count DESC, q.post_id DESC";
        }

        String questionSql =
                "SELECT q.post_id, COUNT(pl.post_id) AS like_count " +
                "FROM BOARD_POST q " +
                "LEFT JOIN POST_LIKE pl ON q.post_id = pl.post_id " +
                "WHERE q.category = 2 " +
                "AND q.is_deleted = 0 " +
                "AND q.is_blinded = 0 " +
                "AND q.accepted_comment_id IS NULL " +
                "GROUP BY q.post_id, q.viewcount " +
                questionOrderBy + " " +
                "LIMIT ?, ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(questionSql)) {

            pstmt.setInt(1, startRow);
            pstmt.setInt(2, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    questionIds.add(rs.getInt("post_id"));
                }
            }

            if (questionIds.isEmpty()) {
                return list;
            }

            StringBuilder inClause = new StringBuilder();
            StringBuilder fieldClause = new StringBuilder();

            for (int i = 0; i < questionIds.size(); i++) {
                if (i > 0) {
                    inClause.append(",");
                    fieldClause.append(",");
                }
                inClause.append("?");
                fieldClause.append("?");
            }

            String sql =
                    "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                    "bp.viewcount, bp.create_at, bp.accepted_comment_id, " +
                    "(SELECT COUNT(*) FROM POST_LIKE pl WHERE pl.post_id = bp.post_id) AS like_count, " +
                    "(SELECT COUNT(*) FROM COMMENT c " +
                    " WHERE c.post_id = bp.post_id AND c.is_deleted = 0 AND c.parent_comment_id IS NULL) AS comment_count, " +
                    "COALESCE(bp.accepted_comment_id, bp.post_id) AS group_post_id, " +
                    "CASE WHEN bp.accepted_comment_id IS NULL THEN 0 ELSE 1 END AS answer_sort " +
                    "FROM BOARD_POST bp " +
                    "WHERE bp.is_deleted = 0 " +
                    "AND bp.is_blinded = 0 " +
                    "AND (bp.post_id IN (" + inClause + ") OR bp.accepted_comment_id IN (" + inClause + ")) " +
                    "ORDER BY FIELD(COALESCE(bp.accepted_comment_id, bp.post_id), " + fieldClause + "), " +
                    "answer_sort ASC, bp.post_id ASC";

            try (PreparedStatement detailStmt = conn.prepareStatement(sql)) {
                int idx = 1;

                for (Integer id : questionIds) {
                    detailStmt.setInt(idx++, id);
                }
                for (Integer id : questionIds) {
                    detailStmt.setInt(idx++, id);
                }
                for (Integer id : questionIds) {
                    detailStmt.setInt(idx++, id);
                }

                try (ResultSet rs = detailStmt.executeQuery()) {
                    while (rs.next()) {
                        BoardPostVO vo = new BoardPostVO();
                        vo.setPostId(rs.getInt("post_id"));
                        vo.setMemberId(rs.getString("member_id"));
                        vo.setNickname(rs.getString("nickname"));
                        vo.setCategory(rs.getInt("category"));
                        vo.setTitle(rs.getString("title"));
                        vo.setContent(rs.getString("content"));
                        vo.setViewcount(rs.getInt("viewcount"));
                        vo.setCreateAt(rs.getTimestamp("create_at"));
                        vo.setLikeCount(rs.getInt("like_count"));
                        vo.setCommentCount(rs.getInt("comment_count"));

                        int acceptedId = rs.getInt("accepted_comment_id");
                        if (!rs.wasNull()) {
                            vo.setAcceptedCommentId(acceptedId);
                            vo.setAnswerPost(true);
                        } else {
                            vo.setAcceptedCommentId(null);
                            vo.setAnswerPost(false);
                        }

                        list.add(vo);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    // 10. 전체 글수 구하기
    public int getAllBoardCount() {
        int count = 0;

        // 블라인드 처리되지 않은 게시글만 개수에 포함
        String sql = "SELECT COUNT(*) FROM BOARD_POST " +
                     "WHERE category IN (1, 2, 3) AND is_deleted = 0 AND is_blinded = 0";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    // 11. 카테고리별 글수 구하기
    public int getBoardCount(int category) {
        if (category == 2) {
            return getQnaQuestionCount();
        }

        int count = 0;

        String sql = "SELECT COUNT(*) FROM BOARD_POST "
                   + "WHERE category = ? AND is_deleted = 0 AND is_blinded = 0";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    // 12. 게시글 추천 여부 확인
    public boolean isLikedByMember(int postId, String memberId) {
        String sql = "SELECT COUNT(*) FROM POST_LIKE WHERE post_id = ? AND member_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setString(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 13. 추천 추가
    public int insertLike(int postId, String memberId) {
        String sql = "INSERT INTO POST_LIKE (post_id, member_id) VALUES (?, ?)";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setString(2, memberId);

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 14. 추천 취소
    public int deleteLike(int postId, String memberId) {
        String sql = "DELETE FROM POST_LIKE WHERE post_id = ? AND member_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setString(2, memberId);

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 15. 추천 수 조회
    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) FROM POST_LIKE WHERE post_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 16. 추천 토글
    public boolean toggleLike(int postId, String memberId) {
        if (isLikedByMember(postId, memberId)) {
            deleteLike(postId, memberId);
            return false; // 추천 취소
        } else {
            insertLike(postId, memberId);
            return true; // 추천 완료
        }
    }

    // 17. 게시판 검색
    private String getSearchColumn(String searchType) {
        switch (searchType) {
            case "writer":
                return "nickname";
            case "content":
                return "content";
            case "title":
            default:
                return "title";
        }
    }

    // 18. 전체보기 + 게시판 검색
    public List<BoardPostVO> searchAllBoardListPaging(int startRow, int pageSize, String sort, String searchType, String keyword) {
        List<BoardPostVO> list = new ArrayList<>();

        String orderBy = "ORDER BY bp.post_id DESC";
        if ("view".equals(sort)) {
            orderBy = "ORDER BY bp.viewcount DESC";
        }

        String column = getSearchColumn(searchType);

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at, " +
                "       (SELECT COUNT(*) " +
                "        FROM COMMENT c " +
                "        WHERE c.post_id = bp.post_id " +
                "          AND c.is_deleted = 0 " +
                "          AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                // 삭제되지 않았고 블라인드되지 않은 게시글만 검색
                "WHERE bp.category IN (1,2,3) AND bp.is_deleted = 0 AND bp.is_blinded = 0 " +
                "AND bp." + column + " LIKE ? " +
                orderBy + " " +
                "LIMIT ?, ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setInt(2, startRow);
            pstmt.setInt(3, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 19. 카테고리별 검색 + 게시판
    public List<BoardPostVO> searchBoardListPaging(int category, int startRow, int pageSize, String sort, String searchType, String keyword) {
        List<BoardPostVO> list = new ArrayList<>();

        String orderBy = "ORDER BY bp.post_id DESC";
        if ("view".equals(sort)) {
            orderBy = "ORDER BY bp.viewcount DESC";
        }

        String column = getSearchColumn(searchType);

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at, " +
                "       bp.recruit_status, bp.current_members, bp.max_members, " +
                "       (SELECT COUNT(*) " +
                "        FROM COMMENT c " +
                "        WHERE c.post_id = bp.post_id " +
                "          AND c.is_deleted = 0 " +
                "          AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "WHERE bp.category = ? AND bp.is_deleted = 0 AND bp.is_blinded = 0 " +
                "AND bp." + column + " LIKE ? " +
                orderBy + " " +
                "LIMIT ?, ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category);
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setInt(3, startRow);
            pstmt.setInt(4, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setCommentCount(rs.getInt("comment_count"));

                    int recruitStatus = rs.getInt("recruit_status");
                    if (!rs.wasNull()) vo.setRecruitStatus(recruitStatus);

                    int currentMembers = rs.getInt("current_members");
                    if (!rs.wasNull()) vo.setCurrentMembers(currentMembers);

                    int maxMembers = rs.getInt("max_members");
                    if (!rs.wasNull()) vo.setMaxMembers(maxMembers);

                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 20. 전체보기 검색 결과개수 + 게시판
    public int getSearchAllBoardCount(String searchType, String keyword) {
        int count = 0;
        String column = getSearchColumn(searchType);

        // 블라인드 처리되지 않은 게시글만 검색 결과 개수에 포함
        String sql = "SELECT COUNT(*) FROM BOARD_POST " +
                     "WHERE category IN (1,2,3) AND is_deleted = 0 AND is_blinded = 0 " +
                     "AND " + column + " LIKE ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    // 21. 카테고리별 검색 결과 개수 + 게시판
    public int getSearchBoardCount(int category, String searchType, String keyword) {
        int count = 0;
        String column = getSearchColumn(searchType);

        // 블라인드 처리되지 않은 게시글만 검색 결과 개수에 포함
        String sql = "SELECT COUNT(*) FROM BOARD_POST " +
                     "WHERE category = ? AND is_deleted = 0 AND is_blinded = 0 " +
                     "AND " + column + " LIKE ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category);
            pstmt.setString(2, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    // 22. 인기글 기준 top5 (추천수 기준)
    public List<BoardPostVO> selectHotPostsByLike(int days, int limit) {
        List<BoardPostVO> list = new ArrayList<>();

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                "       bp.viewcount, bp.create_at, " +
                "       COUNT(pl.post_id) AS like_count, " +
                "       (SELECT COUNT(*) " +
                "          FROM COMMENT c " +
                "         WHERE c.post_id = bp.post_id " +
                "           AND c.is_deleted = 0 " +
                "           AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "LEFT JOIN POST_LIKE pl ON bp.post_id = pl.post_id " +
                "WHERE bp.category IN (1, 2, 3) " +
                "  AND bp.is_deleted = 0 " +
                "  AND bp.is_blinded = 0 " +
                "  AND bp.create_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                "GROUP BY bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at " +
                "ORDER BY like_count DESC, bp.viewcount DESC, bp.post_id DESC " +
                "LIMIT ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, days);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setLikeCount(rs.getInt("like_count"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 23. 조회수 기준 topN (기간별)
    public List<BoardPostVO> selectHotPostsByView(int days, int limit) {
        List<BoardPostVO> list = new ArrayList<>();

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                "       bp.viewcount, bp.create_at, " +
                "       COUNT(pl.post_id) AS like_count, " +
                "       (SELECT COUNT(*) " +
                "          FROM COMMENT c " +
                "         WHERE c.post_id = bp.post_id " +
                "           AND c.is_deleted = 0 " +
                "           AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "LEFT JOIN POST_LIKE pl ON bp.post_id = pl.post_id " +
                "WHERE bp.category IN (1, 2, 3) " +
                "  AND bp.is_deleted = 0 " +
                "  AND bp.is_blinded = 0 " +
                "  AND bp.create_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                "GROUP BY bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, bp.viewcount, bp.create_at " +
                "ORDER BY bp.viewcount DESC, like_count DESC, bp.post_id DESC " +
                "LIMIT ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, days);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setLikeCount(rs.getInt("like_count"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 24. 댓글 기준 topN (기간별)
    public List<BoardPostVO> selectHotPostsByComment(int days, int limit) {
        List<BoardPostVO> list = new ArrayList<>();

        String sql =
                "SELECT bp.post_id, bp.member_id, bp.nickname, bp.category, bp.title, bp.content, " +
                "       bp.viewcount, bp.create_at, " +
                "       (SELECT COUNT(*) FROM POST_LIKE pl WHERE pl.post_id = bp.post_id) AS like_count, " +
                "       (SELECT COUNT(*) " +
                "          FROM COMMENT c " +
                "         WHERE c.post_id = bp.post_id " +
                "           AND c.is_deleted = 0 " +
                "           AND c.parent_comment_id IS NULL) AS comment_count " +
                "FROM BOARD_POST bp " +
                "WHERE bp.category IN (1, 2, 3) " +
                "  AND bp.is_deleted = 0 " +
                "  AND bp.is_blinded = 0 " +
                "  AND bp.create_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                "ORDER BY comment_count DESC, bp.viewcount DESC, bp.post_id DESC " +
                "LIMIT ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, days);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardPostVO vo = new BoardPostVO();
                    vo.setPostId(rs.getInt("post_id"));
                    vo.setMemberId(rs.getString("member_id"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setCategory(rs.getInt("category"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setViewcount(rs.getInt("viewcount"));
                    vo.setCreateAt(rs.getTimestamp("create_at"));
                    vo.setLikeCount(rs.getInt("like_count"));
                    vo.setCommentCount(rs.getInt("comment_count"));
                    list.add(vo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 25. 게시글 신고 여부 확인
    public boolean hasReportedPost(int postId, String memberId) {
        String sql = "SELECT COUNT(*) FROM POST_REPORT WHERE post_id = ? AND member_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setString(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 26. 게시글 신고 등록
    public boolean reportPost(int postId, String memberId, String reason) {
        String sql = "INSERT INTO POST_REPORT (post_id, member_id, reason) VALUES (?, ?, ?)";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setString(2, memberId);
            pstmt.setString(3, reason);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 27. 신고 목록 조회
    public List<PostReportDTO> getPostReportList() {
        List<PostReportDTO> list = new ArrayList<>();

        String sql =
            "SELECT pr.report_id, pr.post_id, pr.member_id, pr.reason, pr.created_at, pr.status, " +
            "       bp.title AS post_title, bp.member_id AS post_writer_id, bp.is_blinded, " +
            "       reporter.nickname AS reporter_nickname, " +
            "       writer.nickname AS post_writer_nickname " +
            "FROM POST_REPORT pr " +
            "JOIN BOARD_POST bp ON pr.post_id = bp.post_id " +
            "JOIN MEMBER reporter ON pr.member_id = reporter.member_id " +
            "JOIN MEMBER writer ON bp.member_id = writer.member_id " +
            "ORDER BY pr.report_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PostReportDTO dto = new PostReportDTO();

                dto.setReportId(rs.getInt("report_id"));
                dto.setPostId(rs.getInt("post_id"));
                dto.setReporterId(rs.getString("member_id"));
                dto.setReporterNickname(rs.getString("reporter_nickname"));
                dto.setReason(rs.getString("reason"));
                dto.setPostTitle(rs.getString("post_title"));
                dto.setPostWriterId(rs.getString("post_writer_id"));
                dto.setPostWriterNickname(rs.getString("post_writer_nickname"));
                dto.setStatus(rs.getString("status"));
                dto.setReportedAt(rs.getTimestamp("created_at"));
                dto.setBlinded(rs.getInt("is_blinded"));

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 28. 상태 변경 메서드
    public boolean updatePostReportStatus(int reportId, String status, String adminId) {
        String sql = "UPDATE POST_REPORT SET status = ? WHERE report_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, reportId);

            int result = pstmt.executeUpdate();
            System.out.println("게시글 신고 상태 변경 결과: " + result);

            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 29. 대기 상태만 조회
    public List<PostReportDTO> getPendingPostReportList() {
        List<PostReportDTO> list = new ArrayList<>();

        String sql =
            "SELECT pr.report_id, pr.post_id, pr.member_id, pr.reason, pr.created_at, pr.status, " +
            "       bp.title AS post_title, bp.member_id AS post_writer_id, bp.is_blinded, " +
            "       reporter.nickname AS reporter_nickname, " +
            "       writer.nickname AS post_writer_nickname " +
            "FROM POST_REPORT pr " +
            "JOIN BOARD_POST bp ON pr.post_id = bp.post_id " +
            "JOIN MEMBER reporter ON pr.member_id = reporter.member_id " +
            "JOIN MEMBER writer ON bp.member_id = writer.member_id " +
            "WHERE pr.status = 'PENDING' " +
            "ORDER BY pr.report_id DESC";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PostReportDTO dto = new PostReportDTO();
                dto.setReportId(rs.getInt("report_id"));
                dto.setPostId(rs.getInt("post_id"));
                dto.setReporterId(rs.getString("member_id"));
                dto.setReporterNickname(rs.getString("reporter_nickname"));
                dto.setReason(rs.getString("reason"));
                dto.setPostTitle(rs.getString("post_title"));
                dto.setPostWriterId(rs.getString("post_writer_id"));
                dto.setPostWriterNickname(rs.getString("post_writer_nickname"));
                dto.setStatus(rs.getString("status"));
                dto.setReportedAt(rs.getTimestamp("created_at"));
                dto.setBlinded(rs.getInt("is_blinded"));

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 30. 게시글 블라인드 처리
    public boolean blindPost(int postId) {
        String sql = "UPDATE BOARD_POST SET is_blinded = 1 WHERE post_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 31. 게시글 블라인드 해제
    public boolean unblindPost(int postId) {
        String sql = "UPDATE BOARD_POST SET is_blinded = 0 WHERE post_id = ?";

        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}