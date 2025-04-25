package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ClientController {
    @FXML private ImageView logo;
    @FXML private ImageView addLectureImage;
    @FXML private ImageView removeLectureImage;
    @FXML private ImageView viewScheduleImage;
    @FXML private ImageView otherImage;
    @FXML private Button logoutButton;

    private ClientModel model;
    private ClientView view;

    public void setModel(ClientModel model) {
        this.model = model;
        this.view = new ClientView(model, this);
    }

    @FXML
    public void initialize() {

        // Loading images onto the UI
        try {
            Image logoImage = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
            logo.setImage(logoImage);

            Image addLectureImg = new Image(getClass().getResource("/Images/add_lecture_image.jpg").toExternalForm());
            addLectureImage.setImage(addLectureImg);

            Image removeLectureImg = new Image(getClass().getResource("/Images/remove_lecture_image.jpg").toExternalForm());
            removeLectureImage.setImage(removeLectureImg);

            Image viewScheduleImg = new Image(getClass().getResource("/Images/schedule_button.jpg").toExternalForm());
            viewScheduleImage.setImage(viewScheduleImg);

            Image otherImg = new Image(getClass().getResource("/Images/other_button.jpg").toExternalForm());
            otherImage.setImage(otherImg);
        } catch (NullPointerException e) {
            //if an image fails to load
            System.err.println("Error loading image: " + e.getMessage());
        }
    }
    
    // Highlighting the box effect
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        vbox.setOpacity(0.7);
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        vbox.setOpacity(1.0);
    }


    // Handles which button is clicked / message is sent
    @FXML
    public void handleAddLecture() {
        sendRequestToServer("ADD_LECTURE"); // Sends ADD_LECTURE
    }

    @FXML
    public void handleRemoveLecture() {
        sendRequestToServer("REMOVE_LECTURE"); // Sends REMOVE_LECTURE
    }

    @FXML
    public void handleViewSchedule() {
        sendRequestToServer("TIMETABLE_POPUP"); //Sends VIEW_SCHEDULE
    }

    @FXML
    public void handleOther() {
        sendRequestToServer("OTHER"); // Sends OTHER
    }

    @FXML
    public void handleLogout() {
        sendRequestToServer("LOG_OUT");
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();
    }
    // Handles the quit button
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

    // Sends message to server with provided message and handles a response
    public void sendRequestToServer(String message) {
        // Checks if the client is connected to server
        if (model == null) {
            showAlert("Error", "Server connection not established.");
            return;
        }

        new Thread(() -> {
            String response = model.sendMessage(message); // Sends message
            if (response == null || response.isEmpty()) {
                response = "No response from server.";
            }
            System.out.println("Message Sent: " + message);
            System.out.println("Server Response: " + response + "\n");
            switch (response) { //Switch case the check the server response
                case "OPEN_ADD_LECTURE_PAGE":
                    Platform.runLater(() -> view.openAddLecturePage()); // Opens the add lecture page
                    break;
                case "OPEN_REMOVE_LECTURE_PAGE":
                    Platform.runLater(() -> view.openRemoveLecturePage()); // Opens the remove lecture page
                    break;
                case "OPEN_TIMETABLE_POPUP":
                    Platform.runLater(() -> view.openTimeTablePopUpPage()); // Opens the timetable
                    break;
                case "OPEN_PERSONAL_TIMETABLE":
                    Platform.runLater(()-> view.openPersonalTimetablePage());
                    break;
                case "OPEN_OTHER_PAGE":
                    Platform.runLater(() -> view.openOtherPage()); // Opens the other page
                    break;
                case "LOGGING_OUT":
                    Platform.runLater(() -> view.openLoginView());
                    
                    break;
                default: // If message is not recognised by server an exception is thrown and is displayed by client
                    String request = response;
                    Platform.runLater(() -> showAlert("Error", request));
            }
        }).start();
    }

    // Method to show alerts to client
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}