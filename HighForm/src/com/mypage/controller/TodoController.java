package com.mypage.controller;

import com.mypage.Model.TodoItem;
import com.mypage.dao.TodoDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

public class TodoController {

    // ── FXML 바인딩 ────────────────────────────────────────
    @FXML private ImageView calendarBg;
    @FXML private GridPane  calendarGrid;
    @FXML private VBox      todoPanel;

    @FXML private Button    btnPrev;      // 툴바의  ◀ Prev
    @FXML private Button    btnNext;      // 툴바의  Next ▶
    @FXML private Label     lblMonth;     // 툴바 중앙의 "July 2025"

    // ── 상태 & DAO ─────────────────────────────────────────
    private final TodoDao dao = new TodoDao();
    private YearMonth currentYM = YearMonth.now();

    /* ---------- initialize ---------- */
    @FXML public void initialize() {
        calendarBg.setImage(new Image("/img/win98_calendar.png"));
        updateMonthLabel();
        drawCalendar();
    }

    /* ---------- 메뉴/툴바 ---------- */
    @FXML private void onExit() {
        ((Stage) calendarBg.getScene().getWindow()).close();
    }

    @FXML private void onPrevMonth() {
        currentYM = currentYM.minusMonths(1);
        updateMonthLabel();
        drawCalendar();
    }

    @FXML private void onNextMonth() {
        currentYM = currentYM.plusMonths(1);
        updateMonthLabel();
        drawCalendar();
    }

    @FXML private void onAddSchedule() {
        // 오늘 날짜를 기본 선택으로 모달 호출
        openTodoDialog(LocalDate.now());
    }

    /* ---------- 달력 ---------- */
    private void updateMonthLabel() {
        lblMonth.setText(
                currentYM.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + currentYM.getYear());
    }

    private void drawCalendar() {
        calendarGrid.getChildren().clear();
        // 현재 월의 첫 번째 Sunday 계산
        LocalDate firstSunday = currentYM.atDay(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        for (int cell = 0; cell < 42; cell++) {
            LocalDate date = firstSunday.plusDays(cell);

            Button dayBtn = new Button(String.valueOf(date.getDayOfMonth()));
            dayBtn.setPrefSize(40, 28);

            styleDayButton(dayBtn, date);
            dayBtn.setOnAction(e -> openTodoDialog(date));

            calendarGrid.add(dayBtn, cell % 7, cell / 7);
        }
    }

    private void styleDayButton(Button btn, LocalDate date) {
        String style = "";

        // 오늘 날짜 강조 (진한 파란 바탕)
        if (date.equals(LocalDate.now())) {
            style += "-fx-background-color:#000080; -fx-text-fill:white;";
        }

        // 다른 달의 날짜는 흐리게
        if (!date.getMonth().equals(currentYM.getMonth())) {
            style += "-fx-opacity:0.4;";
        }

        // 일정 존재 시 밑줄
        if (!dao.findByDate(date).isEmpty()) {
            style += "-fx-underline:true;";
        }

        btn.setStyle(style);
    }

    /* ---------- 모달 ---------- */
    private void openTodoDialog(LocalDate date) {
        try {
            // FXML 경로는 resources/view/mypage/TodoDialog.fxml 가정
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mypage/TodoDialog.fxml"));
            DialogPane pane  = loader.load();

            TodoDialogController c = loader.getController();
            c.init(date, dao);

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.initModality(Modality.WINDOW_MODAL);
            dlg.initOwner(calendarGrid.getScene().getWindow());
            dlg.setTitle("Schedule - " + date);

            dlg.showAndWait();

            // 모달 종료 후 화면 갱신
            refreshTodoPanel(date);
            drawCalendar();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* ---------- 우측 일정 패널 ---------- */
    private void refreshTodoPanel(LocalDate date) {
        todoPanel.getChildren().clear();

        List<TodoItem> list = dao.findByDate(date);
        list.forEach(item -> {
            CheckBox cb = new CheckBox(item.getTitle());
            cb.setSelected(item.isDone());
            cb.setOnAction(e -> {
                item.setDone(cb.isSelected());
                dao.update(item);
            });
            todoPanel.getChildren().add(cb);
        });
    }
}
