package org.openjfx.servergui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ServerGUIController {
    @FXML private TextArea logArea;
    @FXML private ListView<String> clientListView;
    @FXML private Button startServerBtn;
    @FXML private Button stopServerBtn;
    @FXML private Button earlyLecturesBtn;
    
    private ServerTCP server;

    @FXML
    private void initialize() {
        stopServerBtn.setDisable(true);
        earlyLecturesBtn.setDisable(true);
    }

    @FXML
    private void handleStartServer() {
        startServerBtn.setDisable(true);
        stopServerBtn.setDisable(false);
        earlyLecturesBtn.setDisable(false);
        log("Starting server...");
        new Thread(() -> {
            server = new ServerTCP();
            try {
            server.startServer();
            } catch (IOException e) {
                
            }
        }).start();
    }

    @FXML
    private void handleStopServer() {
        startServerBtn.setDisable(false);
        stopServerBtn.setDisable(true);
        earlyLecturesBtn.setDisable(true);
        log("Stopping server...");
        if (server != null) {
            server.stopServer();
        }
        
    }

    public void log(String message) {
        javafx.application.Platform.runLater(() -> 
            logArea.appendText(message + "\n")
        );
    }
}