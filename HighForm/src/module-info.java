module HighForm {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires lombok;
	requires java.sql;
	
    opens com to javafx.graphics, javafx.fxml;
    opens com.board.controller to javafx.fxml;
    opens com.login.controller to javafx.fxml;
    opens com.manager.controller to javafx.fxml; 
}