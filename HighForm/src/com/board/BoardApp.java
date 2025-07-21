package com.board;

import java.sql.SQLException;

import com.board.dao.BoardDao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BoardApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	BoardDao dao = BoardDao.getInstance();
    	dao.createTable();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/board/BoardMain2.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 750);
            
            primaryStage.setTitle("Board");
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