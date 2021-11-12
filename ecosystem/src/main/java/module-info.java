module com.example.ecosystem {
    requires javafx.controls;
    requires javafx.fxml;

    exports de.uniko.ecosystem.control;
    exports de.uniko.ecosystem.model.trees;
    exports de.uniko.ecosystem;


    opens de.uniko.ecosystem.control to javafx.fxml;

    opens de.uniko.ecosystem to javafx.fxml;
}