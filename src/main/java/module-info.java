module gui.hudes_projekt_riziko {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens hudes_projekt_riziko.gui to javafx.fxml;
    exports hudes_projekt_riziko.gui;
}