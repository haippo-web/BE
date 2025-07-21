package com.login.controller;

import com.login.model.User;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DesktopController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label timeLabel;
    @FXML private Button startButton;
    
    private User currentUser;
    private Timeline timeline;
    private Popup startMenu;
    
    @FXML
    public void initialize() {
        // 시간 업데이트 타이머 시작
        startClock();
        // 시작 메뉴 초기화
        initializeStartMenu();
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
    
    // 이미지 아이콘을 로드하는 헬퍼 메소드
    private ImageView loadIcon(String imageName) {
        try {
            // resources/img2/ 폴더에서 이미지 로드
            Image image = new Image(getClass().getResourceAsStream("/img2/" + imageName));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(30);  // 아이콘 크기 설정
            imageView.setFitHeight(30);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("이미지를 로드할 수 없습니다: " + imageName);
            e.printStackTrace();
            return null;
        }
    }
    
 // 시작 메뉴 초기화
    private void initializeStartMenu() {
        startMenu = new Popup();
        startMenu.setAutoHide(true);
        
        VBox menuBox = new VBox();
        menuBox.setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-border-color: #808080;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 5;" +
            "-fx-spacing: 3;" +
            "-fx-min-width: 300;" +
            "-fx-pref-width: 300;"
        );
        
        // 메뉴 아이템들 (이미지 파일명으로 변경)
        Button newItem = createMenuItemWithIcon("신청(R)", "application.png");
        newItem.setOnAction(e -> {
            startMenu.hide();
            showAlert("신청", "신청 기능은 아직 구현되지 않았습니다.");
        });
        
        Button helpItem = createMenuItemWithIcon("도움말(H)", "help.png");
        helpItem.setOnAction(e -> {
            startMenu.hide();
            showAlert("도움말", "도움말 기능은 아직 구현되지 않았습니다.");
        });
        
        Button settingsItem = createMenuItemWithIcon("설정(S)", "settings.png");
        settingsItem.setOnAction(e -> {
            startMenu.hide();
            showAlert("설정", "설정 기능은 아직 구현되지 않았습니다.");
        });
        
        Button findItem = createMenuItemWithIcon("찾기(F)", "search.png");
        findItem.setOnAction(e -> {
            startMenu.hide();
            showAlert("찾기", "찾기 기능은 아직 구현되지 않았습니다.");
        });
        
        Separator separator1 = new Separator();
        
        Button logoffItem = createMenuItemWithIcon("administrator 로그오프(L)", "logoff.png");
        logoffItem.setOnAction(e -> {
            startMenu.hide();
            handleLogout();
        });
        
        Button shutdownItem = createMenuItemWithIcon("시스템 종료(U)", "shutdown.png");
        shutdownItem.setOnAction(e -> {
            startMenu.hide();
            handleShutdown();
        });
        
        menuBox.getChildren().addAll(
            newItem, helpItem, settingsItem, findItem, 
            separator1, logoffItem, shutdownItem
        );
        
        startMenu.getContent().add(menuBox);
    }

    private Button createMenuItemWithIcon(String text, String iconFileName) {
        Button button = new Button("  " + text);
        
        // 이미지 아이콘 로드 및 설정
        ImageView icon = loadIcon(iconFileName);
        if (icon != null) {
            button.setGraphic(icon);
        }
        
        button.setStyle(
            "-fx-font-family: 'Malgun Gothic';" +
            "-fx-font-size: 15px;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-alignment: center-left;" +
            "-fx-padding: 8 25 8 15;" +
            "-fx-min-width: 290;" +
            "-fx-pref-width: 290;" +
            "-fx-min-height: 38;" +
            "-fx-pref-height: 38;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-font-family: 'Malgun Gothic';" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: #000080;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: transparent;" +
                "-fx-alignment: center-left;" +
                "-fx-padding: 8 25 8 15;" +
                "-fx-min-width: 290;" +
                "-fx-pref-width: 290;" +
                "-fx-min-height: 38;" +
                "-fx-pref-height: 38;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-font-family: 'Malgun Gothic';" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: transparent;" +
                "-fx-text-fill: black;" +
                "-fx-border-color: transparent;" +
                "-fx-alignment: center-left;" +
                "-fx-padding: 8 25 8 15;" +
                "-fx-min-width: 290;" +
                "-fx-pref-width: 290;" +
                "-fx-min-height: 38;" +
                "-fx-pref-height: 38;"
            );
        });
        
        return button;
    }

    @FXML
    private void showStartMenu() {
        if (startMenu.isShowing()) {
            startMenu.hide();
        } else {
            // 시작 버튼의 절대 위치 계산
            Stage stage = (Stage) startButton.getScene().getWindow();
            
            // 시작 버튼의 화면상 절대 좌표 계산
            double buttonX = stage.getX() + startButton.localToScene(0, 0).getX();
            double buttonY = stage.getY() + startButton.localToScene(0, 0).getY();
            
            // 메뉴 높이 (확대된 크기로 계산 - 메뉴 아이템 7개 * 43px + 패딩)
            double menuHeight = 241;
            
            // 시작 버튼 바로 위에 메뉴가 나타나도록 위치 조정
            double x = buttonX;
            double y = buttonY - menuHeight;
            
            // 화면 경계 체크 (메뉴가 화면 위로 벗어나지 않도록)
            if (y < 0) {
                y = buttonY + startButton.getHeight(); // 버튼 아래로 표시
            }
            
            startMenu.show(stage, x, y);
        }
    }
//    @FXML
//    private void showStartMenu() {
//        if (startMenu.isShowing()) {
//            startMenu.hide();
//        } else {
//            // 시작 버튼의 위치 계산
//            double x = startButton.getScene().getWindow().getX() + startButton.getScene().getX();
//            double y = startButton.getScene().getWindow().getY() + startButton.getScene().getY() - 180;
//            startMenu.show(startButton, x, y);
//        }
//    }
    
    // 바탕화면 아이콘 기능들
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
    private void openNotice() {
        showAlert("공지사항", "공지사항 기능은 아직 구현되지 않았습니다.");
    }
    
    @FXML
    private void openVacationRequest() {
        showAlert("휴가신청", "휴가신청 기능은 아직 구현되지 않았습니다.");
    }
    
    @FXML
    private void openAttendanceCheck() {
        // 출석체크 팝업창
        Alert attendanceAlert = new Alert(Alert.AlertType.INFORMATION);
        attendanceAlert.setTitle("출석체크");
        attendanceAlert.setHeaderText("출석 확인");
        attendanceAlert.setContentText("출석이 완료되었습니다!\n시간: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        attendanceAlert.getDialogPane().setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-font-family: 'Malgun Gothic';" +
            "-fx-font-size: 11px;"
        );
        
        attendanceAlert.showAndWait();
    }
    
    @FXML
    private void openClockOut() {
        // 퇴근 팝업창
        Alert clockOutAlert = new Alert(Alert.AlertType.CONFIRMATION);
        clockOutAlert.setTitle("퇴근하기");
        clockOutAlert.setHeaderText("퇴근 확인");
        clockOutAlert.setContentText("정말 퇴근하시겠습니까?");
        
        clockOutAlert.getDialogPane().setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-font-family: 'Malgun Gothic';" +
            "-fx-font-size: 11px;"
        );
        
        clockOutAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmAlert.setTitle("퇴근완료");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("수고하셨습니다!\n퇴근 시간: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                
                confirmAlert.getDialogPane().setStyle(
                    "-fx-background-color: #c0c0c0;" +
                    "-fx-font-family: 'Malgun Gothic';" +
                    "-fx-font-size: 11px;"
                );
                
                confirmAlert.showAndWait();
            }
        });
    }
    
    @FXML
    private void openCalculator() {
        showAlert("계산기", "계산기 기능은 아직 구현되지 않았습니다.");
    }
    
    private void handleLogout() {
        // 확인 대화상자 표시
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Log Off HighForm");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want log off?");
        
        // 윈도우 98 스타일 적용
        confirmAlert.getDialogPane().setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-font-family: 'MS Sans Serif';" +
            "-fx-font-size: 11px;"
        );
        
        // 버튼 텍스트 변경
        ((Button) confirmAlert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) confirmAlert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                logout();
            }
        });
    }
    
    private void handleShutdown() {
        Alert shutdownAlert = new Alert(Alert.AlertType.CONFIRMATION);
        shutdownAlert.setTitle("시스템 종료");
        shutdownAlert.setHeaderText(null);
        shutdownAlert.setContentText("정말 프로그램을 종료하시겠습니까?");
        
        shutdownAlert.getDialogPane().setStyle(
            "-fx-background-color: #c0c0c0;" +
            "-fx-font-family: 'Malgun Gothic';" +
            "-fx-font-size: 11px;"
        );
        
        shutdownAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 타임라인 정지
                if (timeline != null) {
                    timeline.stop();
                }
                // 프로그램 종료
                Platform.exit();
                System.exit(0);
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
            Stage currentStage = (Stage) startButton.getScene().getWindow();
            
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
            "-fx-font-family: 'Malgun Gothic';" +
            "-fx-font-size: 11px;"
        );
        
        alert.showAndWait();
    }
}