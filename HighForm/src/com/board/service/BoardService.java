package com.board.service;

import com.board.dao.BoardDao;
import com.board.dao.FileLocationDao;
import com.board.model.Board;
import com.board.model.BoardCategory;
import com.board.model.FileLocation;
import com.board.model.dto.BoardDto;
import com.board.model.dto.BoardWriteRequestDto;
import com.util.RedisLoginService;

import java.io.File;
import java.util.List;
import java.util.Map;

/*		[					]
 * 		[	배지원   담당   	]
 * 		[					]
 */

public class BoardService {
    
    private static BoardService instance;
    private final BoardDao boardDao;
    private final FileLocationDao fileLocationDao;
    private final RedisLoginService redisService;
    
    private BoardService() {
        this.boardDao = BoardDao.getInstance();
        this.fileLocationDao = FileLocationDao.getInstance();
        this.redisService = new RedisLoginService();
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
    
    // 게시글과 첨부파일 함께 조회
    public Board getBoardWithFiles(Long boardId) {
        Board board = boardDao.getBoard(boardId);
        if (board != null) {
            List<FileLocation> files = fileLocationDao.getFilesByBoardId(boardId);
            // Board 모델에 파일 정보를 설정하는 로직이 필요할 수 있음
        }
        return board;
    }
    
    // 게시글 생성
    public Long createBoard(Board board) {
        return boardDao.createBoard(board);
    }
    
    // 게시글과 파일 함께 생성
    public Long createBoardWithFile(Board board, File file, Long userId) {
        // 1. 게시글 먼저 저장
        Long boardId = boardDao.createBoard(board);
        
        // 2. 첨부파일이 있다면 파일 저장
        if (file != null) {
            try {
                Long fileId = fileLocationDao.saveFile(file, userId, boardId);
                System.out.println("파일 저장 완료: " + fileId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
            }
        }
        
        return boardId;
    }
    
    // 게시글 수정
    public boolean updateBoard(Long boardId, Board board) {
        return boardDao.updateBoard(boardId, board);
    }
    
    // 게시글과 파일 함께 수정
    public boolean updateBoardWithFile(Long boardId, Board board, File file) {
        boolean success = boardDao.updateBoard(boardId, board);
        
        if (success && file != null) {
            try {
                Map<String, String> userInfo = redisService.getLoginUserFromRedis();
                Long userId = Long.valueOf(userInfo.get("id"));
                Long fileId = fileLocationDao.saveFile(file, userId, boardId);
                System.out.println("파일 업데이트 완료: " + fileId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("파일 업데이트 중 오류가 발생했습니다.", e);
            }
        }
        
        return success;
    }
    
    // 게시글 삭제
    public void deleteBoard(Long boardId) {
        boardDao.deleteBoard(boardId);
    }
    
    // 게시글과 관련 파일들 함께 삭제
    public void deleteBoardWithFiles(Long boardId) {
        // 관련 파일들 먼저 삭제
        List<FileLocation> files = fileLocationDao.getFilesByBoardId(boardId);
        for (FileLocation file : files) {
            fileLocationDao.deleteFile(file.getId());
        }
        
        // 게시글 삭제
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
    
    // 카테고리별 게시글 목록 조회
    public List<BoardDto> getBoardListByCategory(BoardCategory category) {
        return boardDao.getBoardList(category);
    }
    
    // 새 게시글을 DTO로 변환하여 추가
    public BoardDto createBoardDto(Board board) {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        int no = 1; // 실제로는 전체 목록에서 계산해야 함
        Long userId = Long.valueOf(userInfo.get("id"));
        return new BoardDto(no, board.getTitle(), userInfo.get("name"), 
                          board.getCreatedAt(), board.getType(), board.getContent(), 
                          board.getBoardId(), userId);
    }
    
    // 현재 로그인한 사용자 정보 조회
    public Map<String, String> getCurrentUserInfo() {
        return redisService.getLoginUserFromRedis();
    }
    
    // 사용자 권한 확인
    public boolean hasWritePermission() {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        String userRole = userInfo.get("role");
        return "MANAGER".equals(userRole) || "PROFESSOR".equals(userRole);
    }
    
    // 작성자 권한 확인
    public boolean isAuthor(String authorName) {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        return authorName.equals(userInfo.get("name"));
    }
}