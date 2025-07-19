module HighForm {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires java.sql;
	requires lombok;
	requires javafx.graphics;
	
    opens com to javafx.graphics, javafx.fxml;
    opens com.board.controller to javafx.fxml;
    opens com.login.controller to javafx.fxml;
    opens com.mypage.controller to javafx.fxml;

}