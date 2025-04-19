module org.openjfx.servergui {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.openjfx.servergui to javafx.fxml;
    exports org.openjfx.servergui;
}
