package com.mypage.controller;

import com.mypage.dao.AssignmentSubmitDAO;
import com.mypage.dao.AssignmentSubmitDAO.AssignmentOption;
import com.mypage.domain.AssignmentSubmit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class AssignmentController {

    @FXML private Button submitBtn;
    @FXML private Button listBtn;
    @FXML private VBox assignmentListBox;
    @FXML private HBox paginationBox;

    @FXML private ComboBox<AssignmentOption> assignmentCombo;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField fileField;
    @FXML private Button browseBtn;
    @FXML private Button submitBtn_form;

    private int currentPage = 1;
    private int pageSize = 10;
    private long loginUserId = 1L;

    @FXML
    public void initialize() {
        if (submitBtn != null) submitBtn.setOnAction(e -> openSubmitForm());
        if (listBtn != null) listBtn.setOnAction(e -> loadAssignmentList(1));
        if (assignmentListBox != null) loadAssignmentList(currentPage);
    }

    private void loadAssignmentList(int page) {
        try {
            if (assignmentListBox.getChildren().size() > 1)
                assignmentListBox.getChildren().remove(1, assignmentListBox.getChildren().size());

            int offset = (page - 1) * pageSize;
            List<AssignmentSubmit> submitList = AssignmentSubmitDAO.getInstance().getSubmitList(loginUserId, offset, pageSize);

            int no = offset + 1;
            for (AssignmentSubmit submit : submitList) {
                HBox row = new HBox();
                row.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0; -fx-alignment: center;");

                Label noLabel = createTableCell(String.valueOf(no++), 62);
                Label curriculumLabel = createTableCell(submit.getCurriculumName(), 142);
                Label titleLabel = createTableCell(submit.getTitle(), 200);
                Label contentLabel = createTableCell(submit.getContent(), 380);
                Label dateLabel = createTableCell(
                        submit.getSubmittedAt() != null ? submit.getSubmittedAt().toString().replace('T', ' ') : "", 168);

                // 순서 중요! fxml 헤더와 반드시 동일
                row.getChildren().addAll(noLabel, curriculumLabel, titleLabel, contentLabel, dateLabel);
                assignmentListBox.getChildren().add(row);
            }

            showPagination(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Label createTableCell(String text, double width) {
        Label label = new Label(text == null ? "" : text);
        label.setPrefWidth(width);
        label.setStyle("-fx-font-size: 16px; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 0 1 0 0;");
        return label;
    }

    private void showPagination(int selectedPage) {
        paginationBox.getChildren().clear();
        int totalCount = 0;
        try {
            totalCount = AssignmentSubmitDAO.getInstance().getSubmitCount(loginUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        for (int i = 1; i <= pageCount; i++) {
            Button pageBtn = new Button(String.valueOf(i));
            pageBtn.setStyle("-fx-font-size: 16px;");
            if (i == selectedPage) {
                pageBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #00007b; -fx-text-fill: white;");
            }
            int pageNum = i;
            pageBtn.setOnAction(e -> loadAssignmentList(pageNum));
            paginationBox.getChildren().add(pageBtn);
        }
    }

    private void openSubmitForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mypage/AssignmentSubmit.fxml"));
            Parent root = loader.load();
            ComboBox<AssignmentOption> combo = (ComboBox<AssignmentOption>) root.lookup("#assignmentCombo");
            TextField titleField = (TextField) root.lookup("#titleField");
            TextArea contentArea = (TextArea) root.lookup("#contentArea");
            TextField fileField = (TextField) root.lookup("#fileField");
            Button browseBtn = (Button) root.lookup("#browseBtn");
            Button submitBtn_form = (Button) root.lookup("#submitBtn");

            if (combo != null) {
                List<AssignmentOption> availableAssignments =
                        AssignmentSubmitDAO.getInstance().getAvailableAssignmentsForUser(loginUserId);
                combo.getItems().setAll(availableAssignments);
                combo.setPromptText("과제 선택");
            }

            if (browseBtn != null && fileField != null) {
                browseBtn.setOnAction(e -> {
                    FileChooser fc = new FileChooser();
                    java.io.File f = fc.showOpenDialog(null);
                    if (f != null) fileField.setText(f.getAbsolutePath());
                });
            }

            if (submitBtn_form != null) {
                submitBtn_form.setOnAction(e -> {
                    AssignmentOption selected = combo.getValue();
                    String title = titleField.getText();
                    String content = contentArea.getText();
                    if (selected == null) {
                        new Alert(Alert.AlertType.WARNING, "과제를 선택하세요.").showAndWait();
                        return;
                    }
                    if (title == null || title.isEmpty()) {
                        new Alert(Alert.AlertType.WARNING, "제목을 입력하세요.").showAndWait();
                        return;
                    }
                    AssignmentSubmit submitData = new AssignmentSubmit();
                    submitData.setUserId(loginUserId);
                    submitData.setAssignmentId(selected.getId());
                    submitData.setTitle(title);
                    submitData.setContent(content);
                    submitData.setSubmittedAt(java.time.LocalDateTime.now());
                    try {
                        AssignmentSubmitDAO.getInstance().insert(submitData);
                        new Alert(Alert.AlertType.INFORMATION, "제출 완료!").showAndWait();
                        submitBtn_form.getScene().getWindow().hide();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "제출 실패: " + ex.getMessage()).showAndWait();
                    }
                });
            }

            Stage stage = new Stage();
            stage.setTitle("과제 제출");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            loadAssignmentList(currentPage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
