package com.board.service;

import com.board.dao.BoardDao;
import com.board.model.Board;
import com.board.model.BoardCategory;
import com.board.model.dto.BoardDto;
import com.board.model.dto.BoardWriteRequestDto;

import java.util.List;

public class BoardService {
    
    private static BoardService instance;
    private final BoardDao boardDao;
    
    private BoardService() {
        this.boardDao = BoardDao.getInstance();
    }
    
    public static BoardService getInstance() {
        if (instance == null) {
            instance = new BoardService();
        }
        return instance;
    }
    
    // 게시글 목록 조회
    public List<BoardDto> getBoardList(BoardCategory type) {
        return boardDao.getBoardList(type);
    }
    
    // 게시글 상세 조회
    public Board getBoard(Long boardId) {
        return boardDao.getBoard(boardId);
    }
    
    // 게시글 생성
    public Long createBoard(Board board) {
        return boardDao.createBoard(board);
    }
    
    // 게시글 수정
    public boolean updateBoard(Long boardId, Board board) {
        return boardDao.updateBoard(boardId, board);
    }
    
    // 게시글 삭제
    public void deleteBoard(Long boardId) {
        boardDao.deleteBoard(boardId);
    }
    
    // 게시글 작성 요청 처리
    public Long createBoardFromRequest(BoardWriteRequestDto requestDto, Long userId) {
        Board board = requestDto.toEntity(requestDto, null, userId);
        return createBoard(board);
    }
    
    // 게시글 존재 여부 확인
    public boolean existsBoard(Long boardId) {
        return boardDao.getBoard(boardId) != null;
    }
}