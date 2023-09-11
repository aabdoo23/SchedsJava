module com.example.scheds {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.scheds to javafx.fxml;
    exports com.example.scheds;
}