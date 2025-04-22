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
    
    private ServerTCP server;

    @FXML
    private void initialize() {
        stopServerBtn.setDisable(true);
    }

@FXML
private void handleStartServer() {
    startServerBtn.setDisable(true);
    stopServerBtn.setDisable(false);
    log("Starting server...");
    new Thread(() -> {
        ServerTCP.setLogger(this::log);
        try {
            ServerTCP.startServer();
        } catch (IOException e) {
            log("Server error: " + e.getMessage());
        }
    }).start();
}

    @FXML
    private void handleStopServer() {
        startServerBtn.setDisable(false);
        stopServerBtn.setDisable(true);
        log("Stopping server...");
        if (server != null) {
            server.stopServer();
        }
        
    }
   
        public void setServer(ServerTCP server) {
        this.server = server;
        this.server.setLogger(this::log);
    }
        
    public void log(String message) {
        if (message.startsWith("CLIENT_LIST_UPDATE:")) {
            // Handle client list updates separately
            updateClientList(message.substring("CLIENT_LIST_UPDATE:".length()));
        } else {
            // Normal logging
            javafx.application.Platform.runLater(() -> 
                logArea.appendText(message + "\n")
            );
        }
    }
    
    private void updateClientList(String clientNames) {
        javafx.application.Platform.runLater(() -> {
            clientListView.getItems().clear();
            if (!clientNames.isEmpty()) {
                clientListView.getItems().addAll(clientNames.split(","));
            }
        });
    }
}