package Service;

import Dao.BoardDAO;
import Dto.BoardDTO;

public class BoardService {

    private BoardDAO boardDAO;

    public BoardService(BoardDAO boardDAO) {
        this.boardDAO = boardDAO;
    }

    public BoardDTO getPostById(int postId) {
        return boardDAO.selectPostById(postId);
    }

    public void increaseViewCount(int postId) {
        boardDAO.increaseViewCount(postId);
    }
}