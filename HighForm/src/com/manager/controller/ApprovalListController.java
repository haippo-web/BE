package com.manager.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.manager.model.Approval;
import com.manager.service.ApprovalListService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ApprovalListController {
	// 네비게이션 컨트롤들
	@FXML
	private Button backButton;
	@FXML
	private Button forwardButton;
	@FXML
	private Button refreshButton;
	@FXML
	private Button homeButton;
	@FXML
	private TextField pathField;

	// 테이블과 컬럼들
	@FXML
	private TableView<Approval> approvalTable;
	@FXML
	private TableColumn<Approval, String> approvalIdColumn; // Integer → String으로 변경
	@FXML
	private TableColumn<Approval, String> typeColumn;
	@FXML
	private TableColumn<Approval, String> titleColumn;
	@FXML
	private TableColumn<Approval, String> requesterColumn;
	@FXML
	private TableColumn<Approval, String> attachmentColumn;
	@FXML
	private TableColumn<Approval, String> startDateColumn;
	@FXML
	private TableColumn<Approval, String> endDateColumn;
	@FXML
	private TableColumn<Approval, String> requestDateColumn;
	@FXML
	private TableColumn<Approval, String> resultColumn;

	// 상세 정보 필드들
	@FXML
	private TextField approvalNoField; // 결재번호
	@FXML
	private TextField categoryField; // 결재 종류 -연차
	@FXML
	private TextField affiliationField; // 소속
	@FXML
	private TextField aplicantField; // 신청인
	@FXML
	private TextField titleDetailField; // 제목
	@FXML
	private TextArea contentArea; // 내용
	@FXML
	private TextField proofFileField; // 증명서
	@FXML
	private TextField startDateField; // 시작일
	@FXML
	private TextField endDateField; // 종료일
	@FXML
	private TextField requestDateField; // 신청일

	// 버튼들
	@FXML
	private Button rejectButton;
	@FXML
	private Button approveButton;

	private ApprovalListService approvalService;
	private ObservableList<Approval> approvalList;

	@FXML
	private void initialize() {
		// setupTableColumns();
		// loadSampleData();
		// setupEventHandlers();

		// 서비스 초기화
		approvalService = new ApprovalListService();
		approvalList = FXCollections.observableArrayList();

		// 테이블 컬럼 설정
		// 결재ID
		approvalIdColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null && approval.getApprovalId() != 0) { // int -> String 형변환
				return new SimpleStringProperty(String.valueOf(approval.getApprovalId()));
			}
			return new SimpleStringProperty("");
		});
		// 결재 종류
		typeColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String text = "휴가";
				return new SimpleStringProperty(text != null ? text : "");
			}
			return new SimpleStringProperty("");
		});
		// 제목
		titleColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String reason = approval.getReason();
				return new SimpleStringProperty(reason != null ? reason : "");
			}
			return new SimpleStringProperty("");
		});
		// 신청자
		requesterColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String request = approval.getUserName();
				return new SimpleStringProperty(request != null ? request : "");
			}
			return new SimpleStringProperty("");
		});
		// 첨부파일
		attachmentColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String attachment = approval.getProof_file();
				return new SimpleStringProperty(attachment != null ? attachment : "");
			}
			return new SimpleStringProperty("");
		});
		// 시작일
		startDateColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String startDate = approval.getStart_date();
				return new SimpleStringProperty(startDate != null ? startDate : "");
			}
			return new SimpleStringProperty("");
		});
		// 종료일
		endDateColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String endDate = approval.getEnd_date();
				return new SimpleStringProperty(endDate != null ? endDate : "");
			}
			return new SimpleStringProperty("");
		});
		// 요청일
		requestDateColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String requestDate = approval.getRequested_at();
				return new SimpleStringProperty(requestDate != null ? requestDate : "");
			}
			return new SimpleStringProperty("");
		});
		// 승인 결과
		resultColumn.setCellValueFactory(cellData -> {
			Approval approval = cellData.getValue();
			if (approval != null) {
				String status = approval.getStatus();
				return new SimpleStringProperty(status != null ? status : "");
			}
			return new SimpleStringProperty("");
		});

		// 컬럼폭
		resultColumn.setPrefWidth(100);

		// 컬럼 크기 조정 정책 설정
		approvalTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		// 테이블에 데이터 바인딩
		approvalTable.setItems(approvalList);

		// 초기 데이터 로드
		loadCourseData();

		// 테이블 선택 이벤트 핸들러
		approvalTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				populateFields(newValue);
			}
		});

		System.out.println("CourseManagementController 초기화 완료");
	}

	// 데이터 불러오기
	private void loadCourseData() {
		try {
			approvalList.clear();
			List<Approval> approvals = approvalService.getAllApprovals();

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss][.S]");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			for (Approval approval : approvals) {
				// 날짜 필드들이 String 형태라면 → 날짜만 추출해서 다시 저장
				try {
					if (approval.getRequested_at() != null) {
						String formattedRequestedAt = LocalDate.parse(approval.getRequested_at(), inputFormatter)
								.format(outputFormatter);
						approval.setRequested_at(formattedRequestedAt);
					}
					if (approval.getStart_date() != null) {
						String formattedStartDate = LocalDate.parse(approval.getStart_date(), inputFormatter)
								.format(outputFormatter);
						approval.setStart_date(formattedStartDate);
					}
					if (approval.getEnd_date() != null) {
						String formattedEndDate = LocalDate.parse(approval.getEnd_date(), inputFormatter)
								.format(outputFormatter);
						approval.setEnd_date(formattedEndDate);
					}
				} catch (Exception dateEx) {
					System.err.println("날짜 파싱 오류: " + dateEx.getMessage());
				}

				// 디버깅 로그
				System.out.println("로드된 결재: " + approval.getApprovalId() + " | " + approval.getReason());
			}

			approvalList.addAll(approvals);
			approvalTable.refresh();
			System.out.println("결재 데이터 로드 완료: " + approvalList.size() + "건");

		} catch (Exception e) {
			System.err.println("데이터 로드 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 선택한 데이터를 (하단) 필드에 채우기
	private void populateFields(Approval approval) {
		approvalNoField.setText(String.valueOf(approval.getApprovalId()));
		categoryField.setText("휴가");
		affiliationField.setText(approval.getUserAffiliation()); // 소속
		aplicantField.setText(approval.getUserName()); // 신청자
		titleDetailField.setText(approval.getReason()); // 제목
		contentArea.setText(approval.getReason()); // 내용
		proofFileField.setText(approval.getProof_file());
		startDateField.setText(approval.getStart_date());
		endDateField.setText(approval.getEnd_date());
		requestDateField.setText(approval.getRequested_at());
	}

	private void clearFields() {
		approvalNoField.clear();
		categoryField.clear();
		affiliationField.clear();
		aplicantField.clear();
		proofFileField.clear();
		startDateField.clear();
		endDateField.clear();
		requestDateField.clear();
		approvalTable.getSelectionModel().clearSelection();
	}

	// 결재 승인
	@FXML
	private void handleApproval() {
		Approval selectedApproval = approvalTable.getSelectionModel().getSelectedItem();

		if (selectedApproval == null) {
			showAlert("선택 오류", "승인할 결재를 선택하세요.");
			return;
		}

		// 1. 수정 전 확인 다이얼로그
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("승인 확인");
		confirmAlert.setHeaderText("선택한 결재를 승인하시겠습니까?");
		confirmAlert.setContentText(selectedApproval.getReason());

		// 2. 확인/취소 버튼 대기
		Optional<ButtonType> result = confirmAlert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			// 3. 사용자가 '확인'을 누르면 수정 진행
			boolean success = approvalService.updateApproval(selectedApproval);

			if (success) {
				selectedApproval.setStatus("approve");

				approvalService.updateApproval(selectedApproval);

				Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "결재가 승인되었습니다.");
				successAlert.show();
			} else {
				Alert errorAlert = new Alert(Alert.AlertType.ERROR, "결재 승인 중 오류가 발생했습니다.");
				errorAlert.show();
			}
			loadCourseData();
			clearFields();
		} else {
			System.out.println("결재 승인이 취소되었습니다.");
		}
	}

	// 결재 거절
	@FXML
	private void handleRejection() {
		Approval selectedApproval = approvalTable.getSelectionModel().getSelectedItem();

		if (selectedApproval == null) {
			showAlert("선택 오류", "반려할 결재를 선택하세요.");
			return;
		}

		// 1. 수정 전 확인 다이얼로그
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("반려 확인");
		confirmAlert.setHeaderText("선택한 결재를 반려하시겠습니까?");
		confirmAlert.setContentText(selectedApproval.getReason());

		// 2. 확인/취소 버튼 대기
		Optional<ButtonType> result = confirmAlert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			// 3. 사용자가 '확인'을 누르면 수정 진행
			boolean success = approvalService.updateApproval(selectedApproval);

			if (success) {
				selectedApproval.setStatus("reject");

				approvalService.updateApproval(selectedApproval);

				Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "결재가 반려되었습니다.");
				successAlert.show();
			} else {
				Alert errorAlert = new Alert(Alert.AlertType.ERROR, "결재 반려 중 오류가 발생했습니다.");
				errorAlert.show();
			}
			loadCourseData();
			clearFields();
		} else {
			System.out.println("결재 반려가 취소되었습니다.");
		}
	}

	// 경고창 출력 유틸
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	private void handleBackBtn(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/manager/menuSelect.fxml"));
			Parent root = loader.load();

			// 현재 이벤트가 발생한 노드에서 Stage 가져오기
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			// 해당 Stage의 Scene을 바꿈
			stage.setScene(new Scene(root));
			stage.setTitle("Management System"); // 타이틀 설정 (선택)
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
			showAlert("오류", "이전 화면으로 돌아가는 데 실패했습니다.");
		}
	}

}
