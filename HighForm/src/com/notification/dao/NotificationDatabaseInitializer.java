package com.notification.dao;

import java.sql.SQLException;

/**
 * Notification 데이터베이스 프로시저를 생성하는 초기화 클래스
 */
public class NotificationDatabaseInitializer {
    
    private final NotificationDao notificationDao;
    
    public NotificationDatabaseInitializer() {
        this.notificationDao = NotificationDao.getInstance();
    }
    
    /**
     * 모든 프로시저를 생성합니다.
     */
    public void initializeDatabase() {
        try {
            System.out.println("=== Notification 데이터베이스 프로시저 초기화 시작 ===");
            
            // 오래된 알림 정리 프로시저 생성
            createCleanupProcedure();
            
            System.out.println("=== Notification 데이터베이스 프로시저 초기화 완료 ===");
            
        } catch (Exception e) {
            System.err.println("Notification 데이터베이스 초기화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 오래된 알림 정리 프로시저를 생성합니다.
     */
    private void createCleanupProcedure() {
        try {
            notificationDao.createCleanupProcedure();
        } catch (SQLException e) {
            System.err.println("오래된 알림 정리 프로시저 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 개별 프로시저 생성 메서드들
     */
    public void createCleanupProcedureOnly() {
        createCleanupProcedure();
    }
} 