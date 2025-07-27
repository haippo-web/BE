package com.notification.service;

import com.notification.dao.NotificationDao;
import com.notification.model.Notification;
import com.util.RedisLoginService;

import java.util.List;
import java.util.Map;

/*		[					]
 * 		[	배지원    담당   	]
 * 		[					]
 */

public class NotificationService {
    
    private static NotificationService instance;
    private final NotificationDao notificationDao;
    private final RedisLoginService redisService;
    
    private NotificationService() {
        this.notificationDao = NotificationDao.getInstance();
        this.redisService = new RedisLoginService();
    }
    
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    // 사용자별 알림 조회
    public List<Notification> getAllNotifications(Long userId) {
        return notificationDao.getAllNotifications(userId);
    }
    
    // 현재 로그인한 사용자의 알림 조회
    public List<Notification> getCurrentUserNotifications() {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        Long userId = Long.valueOf(userInfo.get("id"));
        return notificationDao.getAllNotifications(userId);
    }
    
    // 알림 읽음 처리
    public boolean markAsRead(Long notificationId) {
        return notificationDao.markAsRead(notificationId);
    }
    
    // 알림 삭제
    public boolean deleteNotification(Long notificationId) {
        return notificationDao.deleteNotification(notificationId);
    }
    
    // 알림 생성
    public Long createNotification(Notification notification) {
        return notificationDao.createNotification(notification);
    }
    
    // 새 알림 생성 (사용자 정보 포함)
    public Long createNewNotification(String title, String content, Long userId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setUserId(userId);
        notification.setIsRead(false);
        
        return notificationDao.createNotification(notification);
    }
    
    // 현재 로그인한 사용자에게 알림 생성
    public Long createNotificationForCurrentUser(String title, String content) {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        Long userId = Long.valueOf(userInfo.get("id"));
        return createNewNotification(title, content, userId);
    }
    
    // 읽지 않은 알림 개수 조회
    public int getUnreadNotificationCount(Long userId) {
        List<Notification> notifications = notificationDao.getAllNotifications(userId);
        return (int) notifications.stream()
                .filter(notification -> !notification.isRead())
                .count();
    }
    
    // 현재 사용자의 읽지 않은 알림 개수 조회
    public int getCurrentUserUnreadNotificationCount() {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        Long userId = Long.valueOf(userInfo.get("id"));
        return getUnreadNotificationCount(userId);
    }
    
    // 알림 내용 검증
    public boolean validateNotificationContent(String title, String content) {
        return title != null && !title.trim().isEmpty() &&
               content != null && !content.trim().isEmpty();
    }
    
    // 현재 로그인한 사용자 정보 조회
    public Map<String, String> getCurrentUserInfo() {
        return redisService.getLoginUserFromRedis();
    }
    
    // 오래된 알림 자동 정리 (기본 30일)
    public int cleanupOldNotifications() {
        return cleanupOldNotifications(30);
    }
    
    // 지정된 일수보다 오래된 알림 정리
    public int cleanupOldNotifications(int daysOld) {
        return notificationDao.cleanupOldNotifications(daysOld);
    }
    
    // 오래된 알림 정리 프로시저 생성
    public void createCleanupProcedure() {
        try {
            notificationDao.createCleanupProcedure();
        } catch (Exception e) {
            System.err.println("오래된 알림 정리 프로시저 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 