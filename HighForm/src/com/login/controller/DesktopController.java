package com.login.controller;

import com.login.model.User;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DesktopController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label timeLabel;
    @FXML private Button startButton;
    @FXML private Button logoutButton;
    
    private User currentUser;
    private Timeline timeline;
    
    @FXML
    public void initialize() {
        // 시간 업데이트 타이머 시작
        startClock();
    }
    
    // 현재 로그인한 사용자 정보 설정
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateWelcomeMessage();
    }
    
    private void updateWelcomeMessage() {
        if (currentUser != null) {
            String roleText = getRoleText(currentUser.getRole());
            welcomeLabel.setText("환영합니다, " + currentUser.getName() + "님! (" + roleText + ")");
        }
    }
    
    private String getRoleText(String role) {
        switch (role) {
            case "STUDENT": return "학생";
            case "PROFESSOR": return "교수";
            case "MANAGER": return "관리자";
            default: return role;
        }
    }
    
    private void startClock() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        updateTime(); // 즉시 시간 표시
    }
    
    private void updateTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        timeLabel.setText(now.format(formatter));
    }
    
    @FXML
    private void openMyComputer() {
        showAlert("내 컴퓨터", "내 컴퓨터 기능은 아직 구현되지 않았습니다.");
    }
    
    @FXML
    private void openFileManager() {
        showAlert("폴더", "파일 관리자 기능은 아직 구현되지 않았습니다.");
    }
    
    @FXML
    private void openTrash() {
        showAlert("휴지통", "휴지통 기능은 아직 구현되지 않았습니다.");
    }
    
    @FXML
    private void openBoard() {
        try {
            // 게시판 화면으로 이동
            Stage currentStage = (Stage) startButton.getScene().getWindow();
            Parent board = FXMLLoader.load(getClass().getResource("/view/board/boardList.fxml"));
            currentStage.setScene(new Scene(board, 1000, 750));
            currentStage.setTitle("HighForm - 알림판");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "알림판을 열 수 없습니다.");
        }
    }
    
    @FXML
    private void showStartMenu() {
        showAlert("시작 메뉴", "시작 메뉴 기능은 아직 구현되지 않았습니다.");
    }
    
    @FXML
    private void handleLogout() {
        // 확인 대화상자 표시
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("로그아웃");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("정말 로그아웃하시겠습니까?");
        
        // 윈도우 98 스타일 적용
        confirmAlert.getDialogPane().setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-font-family: 'MS Sans Serif';" +
            "-fx-font-size: 11px;"
        );
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                logout();
            }
        });
    }
    
    private void logout() {
        try {
            // 타임라인 정지
            if (timeline != null) {
                timeline.stop();
            }
            
            // 현재 스테이지 가져오기
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            
            // 로그인 FXML 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login/login.fxml"));
            Parent loginPage = loader.load();
            
            // 로그인 화면으로 전환
            Scene loginScene = new Scene(loginPage, 400, 300);
            currentStage.setScene(loginScene);
            currentStage.setTitle("HighForm Login");
            
            // 현재 사용자 정보 초기화
            currentUser = null;
            
            System.out.println("로그아웃 완료");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "로그아웃 처리 중 오류가 발생했습니다.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // 윈도우 98 스타일로 알럿 창 스타일링
        alert.getDialogPane().setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-font-family: 'MS Sans Serif';" +
            "-fx-font-size: 11px;"
        );
        
        alert.showAndWait();
    }}