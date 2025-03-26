module org.openjfx._23381272_client {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.openjfx._23381272_client to javafx.fxml;
    exports org.openjfx._23381272_client;
}
