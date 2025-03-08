package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ClientController {
    @FXML private ImageView logo;
    @FXML private ImageView addLectureImage;
    @FXML private ImageView removeLectureImage;
    @FXML private ImageView viewScheduleImage;
    @FXML private ImageView otherImage;

    private ClientModel model;
    private ClientView view;

    public void setModel(ClientModel model) {
        this.model = model;
        this.view = new ClientView(model, this);
    }

    @FXML
    public void initialize() {
        // Debugging: Print image paths
        System.out.println("Logo Path: " + getClass().getResource("/Images/ul_logo.jpg"));
        System.out.println("Add Lecture Image Path: " + getClass().getResource("/Images/add_lecture_image.jpg"));
        System.out.println("Remove Lecture Image Path: " + getClass().getResource("/Images/remove_lecture_image.jpg"));
        System.out.println("View Schedule Image Path: " + getClass().getResource("/Images/view_schedule_image.jpg"));
        System.out.println("Other Image Path: " + getClass().getResource("/Images/other_image.jpg"));

        // Load images
        try {
            Image logoImage = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
            logo.setImage(logoImage);

            Image addLectureImg = new Image(getClass().getResource("/Images/add_lecture_image.jpg").toExternalForm());
            addLectureImage.setImage(addLectureImg);

            Image removeLectureImg = new Image(getClass().getResource("/Images/remove_lecture_image.jpg").toExternalForm());
            removeLectureImage.setImage(removeLectureImg);

            Image viewScheduleImg = new Image(getClass().getResource("/Images/view_schedule_image.jpg").toExternalForm());
            viewScheduleImage.setImage(viewScheduleImg);

            Image otherImg = new Image(getClass().getResource("/Images/other_image.jpg").toExternalForm());
            otherImage.setImage(otherImg);
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddLecture() {
        sendRequestToServer("ADD_LECTURE");
    }

    @FXML
    public void handleRemoveLecture() {
        sendRequestToServer("REMOVE_LECTURE");
    }

    @FXML
    public void handleViewSchedule() {
        sendRequestToServer("VIEW_SCHEDULE");
    }

    @FXML
    public void handleOther() {
        sendRequestToServer("OTHER");
    }

    @FXML
    public void handleQuit() {
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

        System.out.println("Attempting to send request: " + message);
        new Thread(() -> {
            try {
                String response = model.sendMessage(message);
                if (response == null || response.isEmpty()) {
                    response = "No response from server.";
                }
                System.out.println("Message Sent: " + message);
                System.out.println("Server Response: " + response + "\n");

                switch (response) {
                    case "OPEN_ADD_LECTURE_PAGE":
                        Platform.runLater(() -> view.openAddLectureForm());
                        break;
                    case "OPEN_REMOVE_LECTURE_PAGE":
                        Platform.runLater(() -> view.openRemoveLectureForm());
                        break;
                    case "OPEN_VIEW_SCHEDULE_PAGE":
                        Platform.runLater(() -> view.openViewScheduleForm());
                        break;
                    case "OPEN_OTHER_PAGE":
                        Platform.runLater(() -> view.openOther());
                        break;
                    default:
                        String request = response;
                        Platform.runLater(() -> showAlert("Error", request));
                }
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