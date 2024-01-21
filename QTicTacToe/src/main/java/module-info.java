/**
 * Application module info.
 */
module com.github.massimopavoni.qtictactoe {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.massimopavoni.qtictactoe to javafx.controls, javafx.graphics, javafx.fxml;

    exports com.github.massimopavoni.qtictactoe;
}
