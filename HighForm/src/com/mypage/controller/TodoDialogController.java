package com.mypage.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.time.LocalDate;

import com.mypage.Model.TodoItem;
import com.mypage.dao.TodoDao;

public class TodoDialogController {

    @FXML private Label     lblDate;
    @FXML private TextField txtTitle;
    @FXML private TextArea  txtContent;

    private LocalDate selectedDate;
    private TodoDao   dao;

    public void init(LocalDate date, TodoDao dao) {
        this.selectedDate = date;
        this.dao = dao;
        lblDate.setText(date.toString());

        // 저장 버튼 핸들
        DialogPane pane = (DialogPane) lblDate.getScene().getRoot();
        Button btnSave = (Button) pane.lookupButton(ButtonType.OK);
        btnSave.setOnAction(e -> save());
    }

    private void save() {
        TodoItem item = new TodoItem(null,
                txtTitle.getText(),
                txtContent.getText(),
                selectedDate,
                false);
        dao.insert(item);
        ((DialogPane) lblDate.getScene().getRoot()).getScene().getWindow().hide();
    }
}
