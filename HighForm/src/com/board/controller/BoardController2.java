package com.board.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.board.model.Board;
import com.board.model.BoardCategory;
import com.board.model.dto.BoardDto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BoardController2 {
    @FXML private Label pathLabel;
    @FXML private Button noticeBtn, resourceBtn, boardBtn, backBtn, uploadBtn;
    @FXML private TableView<BoardDto> boardTable;
    @FXML private TableColumn<BoardDto, Integer> noColumn;
    @FXML private TableColumn<BoardDto, String> titleColumn;
    @FXML private TableColumn<BoardDto, String> authorColumn;
    @FXML private TableColumn<BoardDto, String> dateColumn;
    @FXML private Pagination pagination;

    private ObservableList<BoardDto> allItems = FXCollections.observableArrayList();
    private ObservableList<BoardDto> currentItems = FXCollections.observableArrayList();
    private BoardCategory currentBoardType = BoardCategory.NOTICE;
    private static final int ROWS_PER_PAGE = 16;
    private static final double ROW_HEIGHT = 32.0;
    private static final double HEADER_HEIGHT = 32.0;
    private static Date sqlDate = null;
    
    @FXML
    public void initialize() throws ParseException {
        // 테이블 높이 고정 설정
        boardTable.setFixedCellSize(ROW_HEIGHT);
        double tableHeight = ROWS_PER_PAGE * ROW_HEIGHT + HEADER_HEIGHT;
        boardTable.setPrefHeight(tableHeight);
        boardTable.setMinHeight(tableHeight);
        boardTable.setMaxHeight(tableHeight);

        // 샘플 데이터
        String dateStr = "2025-07-21";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = sdf.parse(dateStr);
        sqlDate = new java.sql.Date(utilDate.getTime());
        
        allItems.addAll(
                new BoardDto(1, "example.java", "youn.K", sqlDate, BoardCategory.NOTICE,1L, 2L),
                new BoardDto(2, "프로젝트 계획서", "admin", sqlDate, BoardCategory.NOTICE,1L, 2L),
                new BoardDto(3, "과제 제출 안내", "youn.K",sqlDate, BoardCategory.NOTICE,1L, 2L),
                new BoardDto(4, "샘플 코드", "user1", sqlDate, BoardCategory.DATA_ROOM,1L, 2L),
                new BoardDto(5, "시스템 점검 공지", "admin", sqlDate, BoardCategory.DATA_ROOM,1L, 3L),
                new BoardDto(6, "데이터베이스 설계", "user2", sqlDate, BoardCategory.DATA_ROOM,1L, 4L),
                new BoardDto(7, "example.java", "youn.K", sqlDate, BoardCategory.DATA_ROOM,1L, 3L),
                new BoardDto(8, "프로젝트 계획서", "admin", sqlDate, BoardCategory.BOARD,1L, 3L),
                new BoardDto(9, "과제 제출 안내", "youn.K", sqlDate, BoardCategory.BOARD,1L, 3L),
                new BoardDto(10, "샘플 코드", "user1", sqlDate, BoardCategory.BOARD,1L, 4L),
                new BoardDto(11, "시스템 점검 공지", "admin", sqlDate, BoardCategory.BOARD,1L, 5L),
                new BoardDto(12, "데이터베이스 설계", "user2", sqlDate, BoardCategory.BOARD,1L, 5L),
                new BoardDto(13, "example.java", "youn.K", sqlDate,BoardCategory.BOARD,1L, 5L),
                new BoardDto(14, "프로젝트 계획서", "admin", sqlDate, BoardCategory.BOARD,1L, 5L),
                new BoardDto(15, "프로젝트 계획서", "admin", sqlDate, BoardCategory.BOARD,1L, 3L),
                new BoardDto(16, "과제 제출 안내", "youn.K", sqlDate, BoardCategory.BOARD,1L, 3L),
                new BoardDto(17, "샘플 코드", "user1", sqlDate, BoardCategory.BOARD,1L, 4L),
                new BoardDto(18, "시스템 점검 공지", "admin", sqlDate, BoardCategory.BOARD,1L, 5L),
                new BoardDto(19, "데이터베이스 설계", "user2", sqlDate, BoardCategory.BOARD,1L, 5L),
                new BoardDto(20, "example.java", "youn.K", sqlDate,BoardCategory.BOARD,1L, 5L),
                new BoardDto(21, "프로젝트 계획서", "admin", sqlDate, BoardCategory.BOARD,1L, 5L)

        );

        // 컬럼 바인딩
        noColumn.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        
        boardTable.setRowFactory(tv -> {
            TableRow<BoardDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    BoardDto clicked = row.getItem();
					try {
	                    // 상세 페이지 이동 로직 (추후 구현)
	                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/board/BoardDetail.fxml"));
	                  
	                    Parent root;
	                    
	                    // PostDetailController 인스턴스 얻기
	                    PostDetailController detailController = new PostDetailController();
	                    // boardId 전달
		                    detailController.setBoardId(clicked.getBoardId());
	                    
						root = loader.load();
	                    Scene scene = new Scene(root, 1000, 750);
	                    Stage stage = new Stage();
	                    stage.setTitle("게시글 상세보기");
	                    stage.setScene(scene);
	                    stage.setResizable(true);
	                    stage.show();

	                    
	                    
	                    System.out.println("상세 페이지 이동: " + clicked.getTitle() + ", 게시글 ID :"+clicked.getBoardId());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            });
            return row;
        });

        // 초기화 - PageFactory는 한 번만 설정
        pagination.setPageFactory(this::createPage);
        filterByType(BoardCategory.NOTICE);
        updatePagination();
    }

    // 공지사항 게시물 리스트 
    @FXML
    private void handleNoticeBtn(ActionEvent event) {
        currentBoardType = BoardCategory.NOTICE;
        pathLabel.setText("C:\\Board\\Notice");
        filterByType(BoardCategory.NOTICE);
        updatePagination();
    }

    // 자료실 게시물 리스트 
    @FXML
    private void handleResourceBtn(ActionEvent event) {
        currentBoardType = BoardCategory.DATA_ROOM;
        pathLabel.setText("C:\\Board\\DataRoom");
        filterByType(BoardCategory.DATA_ROOM);
        updatePagination();
    }

    // 게시판 게시물 리스트 
    @FXML
    private void handleBoardBtn(ActionEvent event) {
        currentBoardType = BoardCategory.BOARD;
        pathLabel.setText("C:\\Board\\Board");
        filterByType(BoardCategory.BOARD);
        updatePagination();
    }

    @FXML
    private void handleBackBtn(ActionEvent event) {
        // 추후 이전 페이지 이동 구현
        System.out.println("이전 페이지로 이동");
        // TODO :: Main fxml 페이지 연결
    }

    @FXML
    private void handleUploadBtn(ActionEvent event) {
//    	String userRole = "MANAGER";
    	String userRole = "STUDENT";
        try {
        	// 권한이 매니저나 교수일 경우
        	if(userRole.equals("MANAGER") || userRole.equals( "PROFESSOR")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/board/PostWrite2.fxml"));
                Stage stage = new Stage();
                stage.setTitle("공지사항 및 과제 작성");
                stage.setScene(new Scene(loader.load()));
                stage.initModality(Modality.APPLICATION_MODAL);

                
                PostWriteController2 controller = loader.getController();
                controller.setBoardController(this);

                stage.showAndWait();
        	}else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/board/BoardWrite.fxml"));
                Stage stage = new Stage();
                stage.setTitle("게시글 작성");
                stage.setScene(new Scene(loader.load()));
                stage.initModality(Modality.APPLICATION_MODAL);

                BoardWriteController controller = loader.getController();
                controller.setBoardController(this);

                stage.showAndWait();
        	}
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterByType(BoardCategory type) {
        currentItems.clear();
        if (type.equals(BoardCategory.BOARD)) {
            allItems.stream().filter(item -> BoardCategory.BOARD.equals(item.getType()))
            .forEach(currentItems::add);
        } else if (type.equals(BoardCategory.NOTICE)) {
            allItems.stream().filter(item -> BoardCategory.NOTICE.equals(item.getType()))
                    .forEach(currentItems::add);
        } else if (type.equals(BoardCategory.DATA_ROOM)) {
            allItems.stream().filter(item -> BoardCategory.DATA_ROOM.equals(item.getType()))
                    .forEach(currentItems::add);
        }
    }

    // 수정된 createPage 메서드 - TableView 대신 더미 Node 반환
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, currentItems.size());
        ObservableList<BoardDto> pageItems = FXCollections.observableArrayList();

        // 실제 데이터 추가
        if (fromIndex < currentItems.size()) {
            pageItems.addAll(currentItems.subList(fromIndex, toIndex));
        }
        
        // 빈 행으로 채우기 (항상 10개 행이 표시되도록)
        for (int i = pageItems.size(); i < ROWS_PER_PAGE; i++) {
            pageItems.add(new BoardDto(0, "", "", sqlDate,null,null,null));
        }
        
        // TableView에 데이터 설정
        boardTable.setItems(pageItems);
        
        // TableView 높이 재설정 (중요!)
        double tableHeight = ROWS_PER_PAGE * ROW_HEIGHT + HEADER_HEIGHT;
        boardTable.setPrefHeight(tableHeight);
        boardTable.setMinHeight(tableHeight);
        boardTable.setMaxHeight(tableHeight);
        
        // 더미 노드 반환 (Pagination은 이 노드를 표시하지만 실제로는 TableView를 사용)
        return new Region();
    }

    // 수정된 updatePagination 메서드 - PageFactory 재설정 제거
    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) currentItems.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        
        // PageFactory 재설정 제거 - initialize()에서 한 번만 설정
        // 첫 페이지 데이터 로드
        createPage(0);
    }

    // 게시글 작성 후 리스트에 추가 (공지사항 , 과제)
    // TODO :: DB에 저장하는 로직 추가 
    public void addNewPost(BoardDto item) {
        item.setNo(allItems.size() + 1);
        allItems.add(item);
        filterByType(currentBoardType);
        updatePagination();
    }
    
    // 게시글 작성 후 리스트에 추가 ( 자유 게시물 )
    // TODO :: DB에 저장하는 로직 추가 
    // TODO :: DB에 저장 후 생성되는 ID값 반
    // TODO :: User 연동되면 USerId 추가
    public void addNewPost(Board board) {
        int no = allItems.size() + 1;
        Long BoardId = 3L;
        Long userId = 2L;
        BoardDto dto =  new BoardDto(no, board.getTitle(), "test", board.getCreatedAt() , board.getType() ,BoardId,userId);
        allItems.add(dto);
        filterByType(currentBoardType);
        updatePagination();
    }
    
}