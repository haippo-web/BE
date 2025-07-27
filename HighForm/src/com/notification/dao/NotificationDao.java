package com.notification.dao;

import com.notification.model.Notification;
import com.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    
    // 싱글톤 패턴
    private static NotificationDao instance;
    
    private NotificationDao() {}
    
    public static NotificationDao getInstance() {
        if (instance == null) {
            instance = new NotificationDao();
        }
        return instance;
    }
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // 테이블 생성
    public void createTable() throws SQLException {
        String createTableSQL = NotificationSQL.CREATE_TABLE;
        try (Connection conn = getConnection();
             CallableStatement psmt = conn.prepareCall(createTableSQL)) {
            psmt.executeUpdate();
            System.out.println("알림 테이블 생성 완료");
        } catch (Exception e) {
            System.err.println("알림 테이블 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 오래된 알림 정리 프로시저 생성
    public void createCleanupProcedure() throws SQLException {
        String createProcedureSQL = NotificationSQL.CREATE_CLEANUP_PROCEDURE;
        try (Connection conn = getConnection();
             CallableStatement psmt = conn.prepareCall(createProcedureSQL)) {
            psmt.executeUpdate();
            System.out.println("오래된 알림 정리 프로시저 생성 완료");
        } catch (Exception e) {
            System.err.println("오래된 알림 정리 프로시저 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 오래된 알림 자동 삭제
    public int cleanupOldNotifications(int daysOld) {
        String sql = NotificationSQL.CLEANUP_OLD_NOTIFICATIONS;
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, daysOld);
            cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            
            cstmt.execute();
            
            int deletedCount = cstmt.getInt(2);
            System.out.println("오래된 알림 정리 결과: " + deletedCount + "건 삭제");
            
            return deletedCount;
        } catch (Exception e) {
            System.err.println("알림 정리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    // 알림 생성
    public Long createNotification(Notification notification) {
        String sql = NotificationSQL.CREATE_NOTIFICATION;
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql, new String[] { "ID" })) {
            
            psmt.setString(1, notification.getTitle());
            psmt.setString(2, notification.getContent());
            psmt.setString(3, notification.getType());
            psmt.setLong(4, notification.getUserId());
            
            psmt.executeUpdate();
            
            ResultSet rs = psmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            System.err.println("알림 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // 최신 알림 1개 조회
    public Notification getLatestNotification(Long userId) {
        String sql = NotificationSQL.GET_LATEST_NOTIFICATION;
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, userId);
            ResultSet rs = psmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToNotification(rs);
            }
        } catch (Exception e) {
            System.err.println("최신 알림 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // 모든 알림 조회 (읽지 않은 것 우선)
    public List<Notification> getAllNotifications(Long userId) {
        String sql = NotificationSQL.GET_ALL_NOTIFICATIONS;
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, userId);
            ResultSet rs = psmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            
            System.out.println("알림 목록 조회 완료: " + notifications.size() + "건");
            
        } catch (Exception e) {
            System.err.println("알림 목록 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }
    
    // 알림 읽음 처리
    public boolean markAsRead(Long notificationId) {
        String sql = NotificationSQL.MARK_AS_READ;
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, notificationId);
            int result = psmt.executeUpdate();
            
            System.out.println("알림 읽음 처리 결과: " + result + "건");
            return result > 0;
            
        } catch (Exception e) {
            System.err.println("알림 읽음 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 알림 삭제
    public boolean deleteNotification(Long notificationId) {
        String sql = NotificationSQL.DELETE_NOTIFICATION;
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, notificationId);
            int result = psmt.executeUpdate();
            
            System.out.println("알림 삭제 결과: " + result + "건");
            return result > 0;
            
        } catch (Exception e) {
            System.err.println("알림 삭제 중 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 읽지 않은 알림 개수 조회
    public int getUnreadCount(Long userId) {
        String sql = NotificationSQL.GET_UNREAD_COUNT;
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, userId);
            ResultSet rs = psmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("읽지 않은 알림 개수 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // ResultSet을 Notification 객체로 매핑
 // ResultSet을 Notification 객체로 매핑
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getLong("ID"));
        notification.setTitle(rs.getString("TITLE"));
        notification.setContent(rs.getString("CONTENT"));
        notification.setType(rs.getString("TYPE"));
        notification.setUserId(rs.getLong("USER_ID"));
        notification.setIsRead("Y".equals(rs.getString("IS_READ")));
        notification.setCreatedAt(rs.getDate("CREATED_AT")); // 추가
        return notification;
    }
    
    // DB 연결 테스트 메서드
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Oracle DB 연결 성공 (NotificationDao)");
            return true;
        } catch (SQLException e) {
            System.err.println("Oracle DB 연결 실패 (NotificationDao): " + e.getMessage());
            return false;
        }
    }
}