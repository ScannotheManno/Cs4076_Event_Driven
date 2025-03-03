package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientController {
    private ClientModel model;

    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML private void handleAddLecture() {
        sendRequestToServer("ADD_LECTURE");
        
    }

    @FXML private void handleRemoveLecture() {
        sendRequestToServer("REMOVE_LECTURE");
    }

    @FXML private void handleViewSchedule() {
        sendRequestToServer("VIEW_SCHEDULE");
    }

    @FXML private void handleOther() {
        sendRequestToServer("OTHER_REQUEST");
    }

    @FXML private void handleQuit() {
        sendRequestToServer("QUIT");
    }

    private void sendRequestToServer(String message) {
        if (model == null) {
            showAlert("Error", "Server connection not established.");
            return;
        }

        new Thread(() -> {
            try {
                String response = model.sendMessage(message);
                if (response == null || response.isEmpty()) {
                response = "No response from server.";
            }

                
                
                if ("OPEN_ADD_LECTURE_PAGE".equals(response)) {
                Platform.runLater(() -> openAddLectureForm());
                }
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Failed to communicate with server: " + e.getMessage()));
            }
        }).start();
    }
    
    private void openAddLectureForm() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddLectureView.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Add Lecture");
        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    } catch (IOException e) {
        System.out.println("Failed to open Add Lecture page: " + e.getMessage());
    }
}

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
