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

        new Thread(() -> {
            try {
                String response = model.sendMessage(message);
                if (response == null || response.isEmpty()) {
                response = "No response from server.";
            }

                
                switch (response) {
                    case "OPEN_ADD_LECTURE_PAGE":
                        Platform.runLater(() -> openAddLectureForm());
                        break;
                    case "OPEN_OTHER_PAGE":
                        Platform.runLater(() -> openOther());
                        break;
                    
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


            AddLectureController addLectureController = loader.getController();
            addLectureController.setModel(model);

            Stage stage = new Stage();
            stage.setTitle("Add Lecture");
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Add Lecture page: " + e.getMessage());
        }
    }
    
    private void openViewScheduleForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewScheduleView.fxml"));
            Parent root = loader.load();

            String scheduleData = model.sendMessage("VIEW_SCHEDULE");

            ViewScheduleController viewScheduleController = loader.getController();
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
            otherController.setClientController(this);

            Stage stage = new Stage();
            stage.setTitle("Select a Service");
            stage.setScene(new Scene(root, 300, 250));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Other Request form: " + e.getMessage());
        }
    }
    
    @FXML public void handleOtherRequest(String request) {
        new Thread(() -> {
            try {
                
                switch (request) {
                    case "ADD_LECTURE":
                        sendRequestToServer(request);
                        break;
                    case "REMOVE_LECTURE":
                        sendRequestToServer(request);
                        break;
                    case "VIEW_SCHEDULE":
                        sendRequestToServer(request);
                        break;
                    default:
                        String response = model.sendMessage(request);
                        Platform.runLater(() -> showAlert("Server Response", response));
                        return;
                }
            } catch (IOException e) {
                System.err.println("Error sending request: " + e.getMessage());
            }
        }).start();
    }

    private void openRemoveLectureForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveLectureView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Remove Lecture");
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Remove Lecture page: " + e.getMessage());
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
