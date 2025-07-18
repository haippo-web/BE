package com.board.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BoardController {

    @FXML
    private void onUpload(ActionEvent event) {
        System.out.println("Upload 버튼 클릭됨!");
    }

    @FXML
    private void onStart(ActionEvent event) {
        System.out.println("Start 버튼 클릭됨!");
    }
}