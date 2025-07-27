package com.board.controller;

import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import com.board.model.Board;
import com.board.model.BoardCategory;
import com.board.model.dto.BoardDto;
import com.board.model.dto.BoardWriteRequestDto;
import com.board.service.BoardService;
import com.board.service.FileService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*		[					]
 * 		[	배지원   담당   	]
 * 		[					]
 */

public class PostUpdateController {
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField filePathField;
    @FXML private Button browseBtn, submitBtn;

    private File selectedFile;
    private BoardController boardController;
    private PostDetailController postDetailController;
    private BoardCategory selectedType = BoardCategory.DATA_ROOM;
    private String attachmentPath = "";
    private final BoardService boardService;
    private final FileService fileService;
    private static Long boardId;
    private String UserName = ""; // 현재 로그인한 사용자 (실제로는 세션에서 가져와야 함)
    private String UserRole = "";
    private Long UserId = null;

    
    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
    
    public PostUpdateController() {
		this.boardService = BoardService.getInstance();
        this.fileService = FileService.getInstance();
        // 반드시 public, 파라미터 없음
    }
    
    public void setBoardController(BoardController boardController) {
        this.boardController = boardController;
    }
    
    public void setPostDetailController(PostDetailController postDetailController) {
    	this.postDetailController = postDetailController;
    }
    
    
    
    @FXML
    public void initialize() {
        Map<String, String> userInfo = boardService.getCurrentUserInfo();
        UserName = userInfo.get("name");
        UserRole = userInfo.get("role");
        UserId = Long.valueOf(userInfo.get("id"));
    }

    @FXML
    private void handleBrowseBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");
        
        // 파일 확장자 필터 설정
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("모든 파일", "*.*"),
            new FileChooser.ExtensionFilter("텍스트 파일", "*.txt"),
            new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.gif"),
            new FileChooser.ExtensionFilter("Java 파일", "*.java"),
            new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx")
        );
        
        Stage stage = (Stage) browseBtn.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSubmitBtn(ActionEvent event) throws ParseException {
        String title = titleField.getText() != null ? titleField.getText().trim() : "";
        String content = contentArea.getText() != null ? contentArea.getText().trim() : "";
        
        // TODO :: File DB 연동  
        Long fileId = 3L;
        Long updateFileId = selectedFile != null ? fileId : null;
        
        
        // 수정된 데이터만 담은 Board 객체 생성
        Board updatedBoard = new Board();
        updatedBoard.setBoardId(boardId); // 수정할 게시글 ID
        
        // 입력된 값만 설정 (입력되지 않은 값은 null로 유지)
        if (!title.isEmpty()) {
            updatedBoard.setTitle(title);
        }
        if (!content.isEmpty()) {
            updatedBoard.setContent(content);
        }
        if (fileId != null) {
            updatedBoard.setFileId(updateFileId);
        }
        
        // 변경된 값만 업데이트
        boolean success = boardService.updateBoardWithFile(boardId, updatedBoard, selectedFile);
        
        if (success) {
            showAlert("수정 완료", "게시글이 수정되었습니다.");
            // 창 닫기
            ((Stage) submitBtn.getScene().getWindow()).close();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}