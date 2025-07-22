module HighForm {
    /* ───── 필요한 모듈 ───── */
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires lombok;

    /* ───── 런처에서 접근해야 하는 패키지 ───── */
    exports com.mypage;                        

    /* JavaFX 런처가 리플렉션으로 new 할 때 접근 허용 */
    opens com.mypage to javafx.graphics;      

    /* ───── FXML 컨트롤러용 opens ───── */
    opens com.board.controller  to javafx.fxml;
    opens com.login.controller  to javafx.fxml;
    opens com.mypage.controller to javafx.fxml;

}
