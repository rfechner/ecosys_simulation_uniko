module com.example.ecosystem {
    requires javafx.controls;
    requires javafx.fxml;

    exports de.uniko.ecosystem.control;

    opens de.uniko.ecosystem.control to javafx.fxml;
    exports de.uniko.ecosystem;
    opens de.uniko.ecosystem to javafx.fxml;
}