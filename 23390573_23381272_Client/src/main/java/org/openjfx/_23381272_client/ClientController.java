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

    @FXML public void handleAddLecture() {
        sendRequestToServer("ADD_LECTURE");
        
    }

    @FXML public void handleRemoveLecture() {
        sendRequestToServer("REMOVE_LECTURE");
    }

    @FXML public void handleViewSchedule() {
        sendRequestToServer("VIEW_SCHEDULE");
    }

    @FXML public void handleOther() {
        sendRequestToServer("OTHER");
    }

    @FXML public void handleQuit() {
        new Thread(() -> {
            if (model != null) {
                try {
                    model.sendMessage("QUIT");
                    model.closeConnection();
                } catch (IOException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }

            Platform.runLater(() -> {
                System.out.println("Closing application...");
                System.exit(0);
            });
        }).start();
    }
    
    public void sendRequestToServer(String message) {
        if (model == null) {
            showAlert("Error", "Server connection not established.");
            return;
        }

        System.out.println("📢 Attempting to send request: " + message);
        new Thread(() -> {
            try {
                String response = model.sendMessage(message);
                if (response == null || response.isEmpty()) {
                    response = "No response from server.";
                }
                
                System.out.println("📩 Server Response: " + response);
                String request = response;
                Platform.runLater(() -> handleServerResponse(request));

            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Failed to communicate with server: " + e.getMessage()));
            }
        }).start();
}

    
   private void handleServerResponse(String response) {
        switch (response) {
            case "OPEN_ADD_LECTURE_PAGE":
                openAddLectureForm();
                break;
            case "OPEN_REMOVE_LECTURE_PAGE":
                openRemoveLectureForm();
                break;
            case "OPEN_VIEW_SCHEDULE_PAGE":
                openViewScheduleForm();
                break;
            case "OPEN_OTHER_PAGE":
                openOther();
                break;
            default:
                showAlert("Server Response", response);
                break;
        }
    }


    
    private void openAddLectureForm() {
        FXMLHelper.loadWindow("AddLectureView.fxml", "Add Lecture", 400, 500);
    }
    
    private void openRemoveLectureForm() {
        FXMLHelper.loadWindow("RemoveLectureView.fxml", "Remove Lecture", 400, 500);
    }


    
    private void openViewScheduleForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewScheduleView.fxml"));
            Parent root = loader.load();

            ViewScheduleController viewScheduleController = loader.getController();

            String scheduleData = model.sendMessage("VIEW_SCHEDULE");
            viewScheduleController.populateSchedule(scheduleData);

            Stage stage = new Stage();
            stage.setTitle("View Schedule");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open View Schedule page: " + e.getMessage());
        }
    }

    
    private void openOther() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherView.fxml"));
            Parent root = loader.load();

            OtherController otherController = loader.getController();
            otherController.setClientController(this); // ✅ Pass the client controller

            Stage stage = new Stage();
            stage.setTitle("Other Services");
            stage.setScene(new Scene(root, 300, 250));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Other page: " + e.getMessage());
        }
}

    
    @FXML public void handleOtherRequest(String request) {
        new Thread(() -> {
            try {
                String response = model.sendMessage(request);
                Platform.runLater(() -> showAlert("Server Response", response));
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Failed to communicate with server: " + e.getMessage()));
            }
        }).start();
}


    

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
