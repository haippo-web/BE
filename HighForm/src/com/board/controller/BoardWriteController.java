package com.board.controller;

import java.io.File;
import java.util.Map;

import com.board.dao.CommentDao;
import com.board.model.Board;
import com.board.model.BoardCategory;
import com.board.model.Comment;
import com.board.model.dto.BoardWriteRequestDto;
import com.board.service.BoardService;
import com.board.service.FileService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class BoardWriteController {
    
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField filePathField;
    @FXML private CheckBox aiQuestionCheck;
    @FXML private Button browseBtn, submitBtn, closeBtn;
    
    private BoardController boardController;

    private PostDetailController postDetailController;

    private File selectedFile;
    private final BoardService boardService;
    private final FileService fileService;
    private String UserName = ""; // 현재 로그인한 사용자 (실제로는 세션에서 가져와야 함)
    private String UserRole = "";
    private Long UserId = null;
    
    
    public BoardWriteController() {
		this.boardService = BoardService.getInstance();
        this.fileService = FileService.getInstance();
        // 반드시 public, 파라미터 없음
    }
    
    // BoardController 참조 설정
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
    private void handleSubmitBtn(ActionEvent event) {
        // 입력 검증
        if (titleField.getText().trim().isEmpty()) {
            showAlert("경고", "제목을 입력해주세요.");
            return;
        }
        
        if (contentArea.getText().trim().isEmpty()) {
            showAlert("경고", "내용을 입력해주세요.");
            return;
        }
        
        try {
            // 새 게시글 생성
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String author =UserName; // 실제로는 로그인한 사용자 정보를 가져와야 함
            BoardCategory type = BoardCategory.BOARD;
            
            
            // 1. 게시글 먼저 저장
            BoardWriteRequestDto newPost = new BoardWriteRequestDto(1, title, author, type, content, null);
            Board board = newPost.toEntity(newPost, null, UserId);
            Long boardId = boardService.createBoard(board);
            
            // 2. 첨부파일이 있다면 파일 저장
            Long fileId = null;
            if (selectedFile != null) {
                try {
                    fileId = fileService.saveFile(selectedFile, UserId, boardId);
                    System.out.println("파일 저장 완료: " + fileId);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("경고", "파일 업로드 중 오류가 발생했습니다.");
                }
            }
         // 3. 게시글에 파일 ID 업데이트 (필요시)
            if (fileId != null) {
                // board 테이블의 file_id 컬럼을 업데이트하는 로직 추가 가능
            }
            
            // 4. DB에서 최신 게시글 정보 가져오기
            Board boardEntity = boardService.getBoard(boardId);
            
            // 5. 게시글 목록 새로고침
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
    
    @FXML
    private void handleCloseBtn(ActionEvent event) {
        closeWindow();
    }

   
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }
    
    // 폼 초기화 메서드
    public void clearForm() {
        titleField.clear();
        contentArea.clear();
        filePathField.clear();
        aiQuestionCheck.setSelected(false);
        selectedFile = null;
    }
    
    private void saveAiComment(Long boardId, String comment, Long userId) {
        Comment commentObj = Comment.builder()
            .boardId(boardId)
            .content(comment)
            .userId(userId)
            .author("AI")
            .build();
        CommentDao.getInstance().createComment(commentObj);
    }
}