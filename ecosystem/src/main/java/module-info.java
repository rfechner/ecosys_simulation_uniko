module com.example.ecosystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.uniko.ecosystem to javafx.fxml;
    exports de.uniko.ecosystem;
}