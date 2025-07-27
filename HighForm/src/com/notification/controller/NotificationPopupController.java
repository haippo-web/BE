package com.notification.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.notification.model.Notification;
import com.notification.service.NotificationService;
import java.util.List;
import java.util.Map;

public class NotificationPopupController {
    @FXML private VBox notificationList;
    @FXML private Button closeButton;
    
    private final NotificationService notificationService;
    private static Long currentUserId;
    private String UserName = ""; // 현재 로그인한 사용자 (실제로는 세션에서 가져와야 함)
    private String UserRole = "";
    private Long UserId = null;
    
    public NotificationPopupController() {
        this.notificationService = NotificationService.getInstance();
    }
    
    
    @FXML
    public void initialize() {
        Map<String, String> userInfo = notificationService.getCurrentUserInfo();
        UserName = userInfo.get("name");
        UserRole = userInfo.get("role");
        UserId = Long.valueOf(userInfo.get("id"));
        loadNotifications();
    }
    
    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
        loadNotifications();
    }
    
    private void loadNotifications() {
        if (UserId == null) return;
        
        notificationList.getChildren().clear();
        List<Notification> notifications = notificationService.getCurrentUserNotifications();
        
        for (Notification notification : notifications) {
            VBox notificationItem = createNotificationItem(notification);
            notificationList.getChildren().add(notificationItem);
        }
    }
    
    private VBox createNotificationItem(Notification notification) {
        VBox item = new VBox(5);
        item.setStyle("-fx-background-color: " + (notification.isRead() ? "#f0f0f0" : "#ffffff") + 
                     "; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 8;");
        
        // 제목
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        
        // 내용
        Label contentLabel = new Label(notification.getContent());
        contentLabel.setStyle("-fx-font-size: 11;");
        contentLabel.setWrapText(true);
        
        // 시간
        Label timeLabel = new Label(notification.getCreatedAt().toString());
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666666;");
        
        // 읽기 버튼
        Button readButton = new Button("읽기");
        readButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10;");
        readButton.setOnAction(e -> handleReadNotification(notification.getId()));
        
        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().add(readButton);
        
        item.getChildren().addAll(titleLabel, contentLabel, timeLabel, buttonBox);
        return item;
    }
    
    private void handleReadNotification(Long notificationId) {
        notificationService.markAsRead(notificationId);
        notificationService.deleteNotification(notificationId);
        loadNotifications(); // 목록 새로고침
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}