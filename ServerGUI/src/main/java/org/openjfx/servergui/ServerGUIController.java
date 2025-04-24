package org.openjfx.servergui;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ServerGUIController {
    @FXML private TextArea logArea;
    @FXML private ListView<String> clientListView;
    @FXML private Button startServerBtn;
    @FXML private Button stopServerBtn;
    @FXML private Button timetableBtn;
    
    private ServerTCP server;

    @FXML
    private void initialize() {
        stopServerBtn.setDisable(true);
        timetableBtn.setDisable(true);
        server = new ServerTCP();
    }

    @FXML
    private void handleStartServer() {
        startServerBtn.setDisable(true);
        stopServerBtn.setDisable(false);
        timetableBtn.setDisable(false);
        log("Starting server...");
        new Thread(() -> {
            server.setLogger(this::log);
            try {
                server.startServer();
            } catch (IOException e) {
                log("Server error: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    private void handleStopServer() {
        startServerBtn.setDisable(false);
        stopServerBtn.setDisable(true);
        timetableBtn.setDisable(true);
        log("Stopping server...");
        if (server != null) {
            server.stopServer();
        }
        
    }
    
    @FXML
    private void handleShowTimetable() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/openjfx/servergui/TimeTableView.fxml"));
            Parent root = loader.load();

            TimetableController tc = loader.getController();
            tc.setLectureController(server.getLectureController());
            
            List<String> allUsers = server.getManageStudentController().getAllUserIds();
            tc.setUsers(allUsers);

            Stage stage = new Stage();
            stage.setTitle("Timetable");
            stage.setScene(new Scene(root, 900, 825));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
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