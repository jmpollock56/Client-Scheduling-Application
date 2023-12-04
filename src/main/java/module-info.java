module com.example.c195_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.c195_1 to javafx.fxml;
    exports com.example.c195_1;
    exports model;
    opens model to javafx.fxml;


}