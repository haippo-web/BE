package com.board.controller;

import java.io.File;
import java.text.ParseException;
import java.util.Map;

import com.board.model.Board;
import com.board.model.BoardCategory;
import com.board.model.dto.BoardWriteRequestDto;
import com.board.service.BoardService;
import com.board.service.FileService;
import com.login.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class PostWriteController {
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField attachmentField;
    @FXML private Button browseBtn, submitBtn;

    private BoardController boardController;

    private PostDetailController postDetailController;

    private BoardCategory selectedType = BoardCategory.DATA_ROOM;
    private String attachmentPath = "";
    private final BoardService boardService;
    private File selectedFile;
    private final FileService fileService;
    private String UserName = ""; // 현재 로그인한 사용자 (실제로는 세션에서 가져와야 함)
    private String UserRole = "";
    private Long UserId = null;
    
    private User currentUser;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("[BoardController] 로그인한 사용자: " + user.getName());
    }
    
    public PostWriteController() {
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
        typeComboBox.getItems().addAll("과제", "공지사항");
        typeComboBox.setValue("과제");
        typeComboBox.setOnAction(e -> selectedType = typeComboBox.getValue().equals("과제") ? BoardCategory.DATA_ROOM : BoardCategory.NOTICE);
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
            attachmentPath = selectedFile.getAbsolutePath();
            attachmentField.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSubmitBtn(ActionEvent event) throws ParseException {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String author = UserName; // 실제로는 로그인한 사용자 정보를 가져와야 함
        Long userId = UserId; // 실제로는 로그인한 사용자 ID를 가져와야 함
        
        if (title.isEmpty() || content.isEmpty()) {
            showAlert("질문", "제목과 내용을 입력하세요.");
            return;
        }
        
        try {
            // 1. 게시글과 파일 함께 저장
            BoardWriteRequestDto newPost = new BoardWriteRequestDto(1, title, author, selectedType, content, null);
            Board board = newPost.toEntity(newPost, null, userId);
            Long boardId = boardService.createBoardWithFile(board, selectedFile, userId);
            
            // 3. DB에서 최신 게시글 정보 가져오기
            Board boardEntity = boardService.getBoard(boardId);
            
            if (boardController != null) {
                boardController.addNewPost(boardEntity);
            }
            
            showAlert("성공", "게시글이 성공적으로 작성되었습니다.");
            ((Stage) submitBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "게시글 작성 중 오류가 발생했습니다.");
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