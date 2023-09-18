module net.dialingspoon.terrace {
    requires javafx.controls;
    requires javafx.fxml;


    opens net.dialingspoon.terrace to javafx.fxml;
    exports net.dialingspoon.terrace;
}