package com.manager;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*		[					]
 * 		[	이지민    담당   	]
 * 		[					]
 */

public class ManagerApp extends Application {
	
	 @Override
	    public void start(Stage primaryStage) throws Exception {
		 try {
		        // 1. 폰트 로드
		        Font.loadFont(getClass().getResourceAsStream("/fonts/DungGeunMo.ttf"), 14);

		        // 2. FXML 로드 및 Scene 생성
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/manager/menuSelect.fxml"));
		        Scene scene = new Scene(loader.load(), 1000, 750);

		        // 3. CSS 적용 (폰트 패밀리 지정 포함)
		        scene.getStylesheets().add(getClass().getResource("/fonts/global.css").toExternalForm());

		        primaryStage.setTitle("Management Menu");
		        primaryStage.setScene(scene);
		        primaryStage.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public static void main(String[] args) {
	        launch(args);
	    }	
}