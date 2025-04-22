package org.openjfx.ServerGUI;

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
            server = new ServerTCP(this);
            server.startServer();
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

    @FXML
    private void handleEarlyLectures() {
        log("Optimizing timetable for early lectures...");
        if (server != null) {
            server.optimizeForEarlyLectures();
        }
    }

    public void log(String message) {
        javafx.application.Platform.runLater(() -> 
            logArea.appendText(message + "\n")
        );
    }

    public void addClient(String clientInfo) {
        javafx.application.Platform.runLater(() -> 
            clientListView.getItems().add(clientInfo)
        );
    }

    public void removeClient(String clientInfo) {
        javafx.application.Platform.runLater(() -> 
            clientListView.getItems().remove(clientInfo)
        );
    }
}