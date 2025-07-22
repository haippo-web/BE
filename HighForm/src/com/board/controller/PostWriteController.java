package com.board.controller;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.board.model.BoardCategory;
import com.board.model.dto.BoardDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PostWriteController2 {
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField attachmentField;
    @FXML private Button browseBtn, submitBtn;

    private BoardController2 boardController;
    private BoardCategory selectedType = BoardCategory.DATA_ROOM;
    private String attachmentPath = "";

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("과제", "공지사항");
        typeComboBox.setValue("과제");
        typeComboBox.setOnAction(e -> selectedType = typeComboBox.getValue().equals("과제") ? BoardCategory.DATA_ROOM : BoardCategory.NOTICE);
    }

    public void setBoardController(BoardController2 boardController2) {
        this.boardController = boardController2;
    }

    @FXML
    private void handleBrowseBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) browseBtn.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            attachmentPath = file.getAbsolutePath();
            attachmentField.setText(file.getName());
        }
    }

    @FXML
    private void handleSubmitBtn(ActionEvent event) throws ParseException {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            showAlert("제목과 내용을 입력하세요.");
            return;
        }
        // TODO :: DB 저장하고 ID값 반환
        // TODO :: User 연동하고 ID값 반환 
        Long BoardId = 2L; 
        Long UserId = 3L;
        
        String dateStr = "2025-07-21";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = sdf.parse(dateStr);
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        
        BoardDto newItem = new BoardDto(0, title, "작성자",sqlDate, selectedType,BoardId, UserId);
        newItem.setContent(content);
        newItem.setAttachmentPath(attachmentPath);
        if (boardController != null) {
            boardController.addNewPost(newItem);
        }
        ((Stage) submitBtn.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("입력 오류");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}