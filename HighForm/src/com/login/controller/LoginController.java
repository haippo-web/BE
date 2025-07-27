package com.login.controller;

import java.sql.Connection;

import com.attendance.service.AttendanceCodeService;
import com.attendance.service.AttendanceService;
import com.login.model.User;
import com.login.service.UserService;
import com.util.DBConnection;
import com.util.RedisLoginService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/*		[					]
 * 		[	최산하   담당   	]
 * 		[					]
 */

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private final UserService userService = UserService.getInstance();

    @FXML
    private void handleLogin() {
        String loginId = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("[LoginController] 입력받은 아이디: [" + loginId + "], 비밀번호: [" + password + "]");
        
        try {
            System.out.println("handleLogin() 호출됨");  // 디버깅용

            userService.validateLoginInput(loginId, password);
            User user = userService.login(loginId, password);
            showDesktop(user);

        } catch (RuntimeException e) {
            showAlert("Login failed", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("handleCancel() 호출됨");  // 디버깅용
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/fonts/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("retro-alert");

        alert.showAndWait();
    }

    private void showDesktop(User user) {
        try {
            System.out.println("showDesktop() 호출됨");  // 디버깅용

            RedisLoginService redisService = new RedisLoginService();
            redisService.saveLoginUserToRedis(user.getId(), user.getName(), user.getRole());
            
            Stage currentStage = (Stage) okButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login/desktop.fxml"));
            Parent desktop = loader.load();

            DesktopController desktopController = loader.getController();
            desktopController.setCurrentUser(user); //로그인한 사용자 정보 주입




            Connection conn = DBConnection.getConnection();    
            AttendanceService attendanceService = AttendanceService.createInstance(conn, AttendanceCodeService.getInstance());
            desktopController.setAttendanceService(attendanceService);

     
            currentStage.setScene(new Scene(desktop, 1000, 750));
            currentStage.setTitle("HighForm Desktop - " + user.getName() + " (" + user.getRole() + ")");


        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "데스크탑 화면을 로드할 수 없습니다.");
        }
    }
}
