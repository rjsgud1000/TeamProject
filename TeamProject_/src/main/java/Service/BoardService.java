package Service;

import Dao.BoardDAO;
import Vo.BoardVO;

import java.util.List;

public class BoardService {

    private BoardDAO boardDAO;

    public BoardService() {
        this.boardDAO = new BoardDAO();
    }

    // ===== 전체 게시글 조회 =====
    public List<BoardVO> getBoardList() {
        return boardDAO.getBoardList();
    }

    // ===== 게시글 상세 조회 =====
    public BoardVO getBoard(int boardId) {
        BoardVO board = boardDAO.getBoard(boardId);
        if (board != null) {
            boardDAO.increaseViewCount(boardId); // 조회수 증가
        }
        return board;
    }

    // ===== 게시글 작성 =====
    public boolean writeBoard(BoardVO board) {
        if (board.getTitle() == null || board.getTitle().trim().isEmpty()) return false;
        if (board.getContent() == null || board.getContent().trim().isEmpty()) return false;
        if (board.getWriterId() == null || board.getWriterId().trim().isEmpty()) return false;

        return boardDAO.insertBoard(board);
    }

    // ===== 게시글 삭제 =====
    public boolean deleteBoard(int boardId) {
        return boardDAO.deleteBoard(boardId);
    }
}