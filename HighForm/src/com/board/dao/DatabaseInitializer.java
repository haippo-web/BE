package com.board.dao;

import java.sql.SQLException;

/**
 * 데이터베이스 View와 프로시저를 생성하는 초기화 클래스
 */
public class DatabaseInitializer {
    
    private final BoardDao boardDao;
    private final CommentDao commentDao;
    
    public DatabaseInitializer() {
        this.boardDao = BoardDao.getInstance();
        this.commentDao = CommentDao.getInstance();
    }
    
    /**
     * 모든 View와 프로시저를 생성합니다.
     */
    public void initializeDatabase() {
        try {
            System.out.println("=== 데이터베이스 View 및 프로시저 초기화 시작 ===");
            
            // 1. 게시글 목록 View 생성
            createBoardListView();
            
            // 2. 계층형 댓글 View 생성
            createHierarchicalCommentsView();
            
            // 3. 댓글 삭제 프로시저 생성
            createDeleteCommentProcedure();
            
            System.out.println("=== 데이터베이스 View 및 프로시저 초기화 완료 ===");
            
        } catch (Exception e) {
            System.err.println("데이터베이스 초기화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 게시글 목록 View를 생성합니다.
     */
    private void createBoardListView() {
        try {
            boardDao.createBoardListView();
        } catch (SQLException e) {
            System.err.println("게시글 목록 View 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 계층형 댓글 View를 생성합니다.
     */
    private void createHierarchicalCommentsView() {
        try {
            commentDao.createHierarchicalCommentsView();
        } catch (SQLException e) {
            System.err.println("계층형 댓글 View 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 댓글 삭제 프로시저를 생성합니다.
     */
    private void createDeleteCommentProcedure() {
        try {
            commentDao.createDeleteCommentProcedure();
        } catch (SQLException e) {
            System.err.println("댓글 삭제 프로시저 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 개별 View/프로시저 생성 메서드들
     */
    public void createBoardListViewOnly() {
        createBoardListView();
    }
    
    public void createHierarchicalCommentsViewOnly() {
        createHierarchicalCommentsView();
    }
    
    public void createDeleteCommentProcedureOnly() {
        createDeleteCommentProcedure();
    }
} 