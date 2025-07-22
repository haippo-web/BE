package com;




//public class Main extends Application {
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/view/board/boardList.fxml"));
//        primaryStage.setTitle("Board");
//        primaryStage.setScene(new Scene(root, 1000, 750));
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showSplash();
    }

    private void showSplash() throws Exception {
        Parent splash = FXMLLoader.load(getClass().getResource("/view/login/splash.fxml"));
        primaryStage.setScene(new Scene(splash, 1000, 750));
        primaryStage.show();

        // 3초 후 로딩 화면으로 전환
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                try { showLoading(); } catch (Exception e) { e.printStackTrace(); }
            });
        }).start();
    }

    private void showLoading() throws Exception {
        Parent loading = FXMLLoader.load(getClass().getResource("/view/login/loading.fxml"));
        primaryStage.setScene(new Scene(loading, 1000, 750));

        // 5초 후 로그인 화면으로 전환
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                try { 
                	showManagerMenuAfterLogin();
                	//showLogin(); 
                	} catch (Exception e) { e.printStackTrace(); }
            });
        }).start();
    }

    private void showLogin() throws Exception {
        Parent login = FXMLLoader.load(getClass().getResource("/view/login/login.fxml"));
        primaryStage.setScene(new Scene(login, 1000, 750));
    }
    
    //
    
    public void showManagerMenuAfterLogin() throws Exception {
		Parent managerMenu = FXMLLoader.load(getClass().getResource("/view/manager/menuSelect.fxml"));
		//Parent managerMenu = FXMLLoader.load(getClass().getResource("/view/manager/courseManagement.fxml"));
    	//Parent managerMenu = FXMLLoader.load(getClass().getResource("/view/manager/memberManagement.fxml"));
		primaryStage.setTitle("Manager System");
		primaryStage.setScene(new Scene(managerMenu, 1000, 750));
	}

    public static void main(String[] args) {
    	
        // JavaFX 버전 출력
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("JavaFX Runtime Version: " + System.getProperty("javafx.runtime.version"));
        
    	
        launch(args);
    }
}