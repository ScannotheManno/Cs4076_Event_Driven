package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminController {

    @FXML private ImageView logo;
    @FXML private ImageView manageUsersImage;
    @FXML private ImageView addLectureImage;
    @FXML private ImageView removeLectureImage;
    @FXML private ImageView viewScheduleImage;
    @FXML private Button logoutButton;

    private ClientModel model;
    private AdminView adminView;

    // Setter Method for ClientModel
    public void setModel(ClientModel model) {
        this.model = model;
        this.adminView = new AdminView(model, this);
    }

    @FXML
    public void initialize() {
        try {
            Image logoImage = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
            logo.setImage(logoImage);

            Image manageUsersImg = new Image(getClass().getResource("/Images/people_logo.jpg").toExternalForm());
            manageUsersImage.setImage(manageUsersImg);

            Image addLectureImg = new Image(getClass().getResource("/Images/add_lecture_image.jpg").toExternalForm());
            addLectureImage.setImage(addLectureImg);

            Image removeLectureImg = new Image(getClass().getResource("/Images/remove_lecture_image.jpg").toExternalForm());
            removeLectureImage.setImage(removeLectureImg);

            Image viewScheduleImg = new Image(getClass().getResource("/Images/schedule_button.jpg").toExternalForm());
            viewScheduleImage.setImage(viewScheduleImg);

        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    // Highlighting effect methods
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

    // Button handlers for admin functionalities
    @FXML
    public void handleManageStudent() {
        sendRequestToServer("MANAGE_STUDENTS");
    }
    
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
        sendRequestToServer("GROUP_TIMETABLE"); //Sends VIEW_SCHEDULE
    }

    @FXML
    public void handleLogout() {
        sendRequestToServer("LOG_OUT");
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();
    }

    // Handles quit button similarly to ClientController
    @FXML
    public void handleQuit() {
        new Thread(() -> {
            if (model != null) {
                try {
                    model.sendMessage("QUIT");
                    model.closeConnection();
                } catch (Exception e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
            Platform.runLater(() -> {
                System.out.println("Closing admin panel...");
                System.exit(0);
            });
        }).start();
    }

    // Sends message to server, similar structure to ClientController
    public void sendRequestToServer(String message) {
        if (model == null) {
            showAlert("Error", "Server connection not established.");
            return;
        }

        new Thread(() -> {
            String response = model.sendMessage(message);
            if (response == null || response.isEmpty()) {
                response = "No response from server.";
            }

            System.out.println("Message Sent: " + message);
            System.out.println("Server Response: " + response + "\n");

            switch (response) {
                case "OPENING_MANAGE_STUDENTS_PAGE":
                    Platform.runLater(() -> adminView.openManageStudentPage());
                    break;
                case "OPEN_ADD_LECTURE_PAGE":
                    Platform.runLater(() -> adminView.openAddLecturePage()); // Opens the add lecture page
                    break;
                case "OPEN_REMOVE_LECTURE_PAGE":
                    Platform.runLater(() -> adminView.openRemoveLecturePage()); // Opens the remove lecture page
                    break;
                case "OPEN_GROUP_TIMETABLE_PAGE":
                    Platform.runLater(() -> adminView.openGroupTimetablePage());
                    break;
                case "LOGGING_OUT":
                    Platform.runLater(() -> adminView.openLoginView());
                    break;
                default:
                    String errorResponse = response;
                    Platform.runLater(() -> showAlert("Error", errorResponse));
            }
        }).start();
    }

    // Method to show alerts to admin
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}