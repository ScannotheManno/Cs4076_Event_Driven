package org.openjfx._23381272_client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ClientView {
    private ClientModel model;
    private ClientController clientController;

    // Setter method for ClientView and ClientModel
    public ClientView(ClientModel model, ClientController clientController) {
        this.model = model;
        this.clientController = clientController;
    }
    
    public void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();

            ClientModel model = new ClientModel();
            LoginController loginController = loader.getController();
            loginController.setModel(model);

            Stage primaryStage = new Stage();
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root, 420, 600));
            primaryStage.setResizable(false);
            primaryStage.show();
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles opening the add lecture page
    public void openAddLecturePage() {
        try {
            // Loads AddViewLecture.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddLectureView.fxml"));
            Parent root = loader.load();

            // Calls addLectureController and sets the model
            AddLectureController addLectureController = loader.getController();
            addLectureController.setModel(model);

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Add Lecture");
            stage.setScene(new Scene(root, 400, 520));
            
            // If x is hit to close seen ensures no errors with server expecting extra messages
            stage.setOnCloseRequest(e -> {model.sendMessage("BACK");});
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Add Lecture page: " + e.getMessage());
        }
    }
    
    // Handles opening the remove lecture page
    public void openRemoveLecturePage() {
        try {
            // Request lecture data fron server
            String scheduleData = model.sendMessage("SEND_LECTURES");
            // If no lectures are scheduled then alert client an do not open page
            if (scheduleData.equals("NO_LECTURES_AVAILABLE") || scheduleData.trim().isEmpty()) {
                Platform.runLater(() -> showAlert("Info", "No lectures available to remove."));
                return;
            }

            // Loads RemoveLectureView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveLectureView.fxml"));
            Parent root = loader.load();

            // Calls RemoveLectureController, populates the dropdown box and sets model
            RemoveLectureController removeLectureController = loader.getController();
            removeLectureController.populateLectureDropdown(scheduleData);
            removeLectureController.setModel(model);

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Remove Lecture");
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Remove Lecture page: " + e.getMessage());
        }
    }
    
    public void openTimeTablePopUpPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/_23381272_client/TimetablePopUpView.fxml"));
            Parent root = loader.load();

            TimeTablePopUpController controller = loader.getController();
            controller.setModel(model);
            controller.setView(this);

            Stage stage = new Stage();
            stage.setTitle("Choose a Timetable");
            stage.setScene(new Scene(root, 200, 250));
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to open Manage Users page: " + e.getMessage());
            e.printStackTrace(); // Optional but recommended for detailed errors
        }
    }

    // Handles opening the schedule page
    public void openGroupTimetablePage() {
        try {
            // Loads ViewScheduleView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupTimetableView.fxml"));
            Parent root = loader.load();

            // Calls ViewScheduleController
            ViewScheduleController viewScheduleController = loader.getController();
            // Requests lecture data from server and populates the schdule
            String scheduleData = model.sendMessage("SEND_LECTURE_DETAILS_GROUP");
            viewScheduleController.populateSchedule(scheduleData);
            // Sets model
            viewScheduleController.setModel(model);

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Class Timetable");
            stage.setScene(new Scene(root, 900, 750));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open View Schedule page: " + e.getMessage());
        }
    }
    
    public void openPersonalTimetablePage() {
        try {
            // Loads ViewScheduleView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonalTimeTableView.fxml"));
            Parent root = loader.load();

            // Calls ViewScheduleController
            ViewScheduleController viewScheduleController = loader.getController();
            // Requests lecture data from server and populates the schdule
            String scheduleData = model.sendMessage("SEND_LECTURE_DETAILS_PERSONAL");
            viewScheduleController.populateSchedule(scheduleData);
            // Sets model
            viewScheduleController.setModel(model);

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Personal Timetable");
            stage.setScene(new Scene(root, 900, 750));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open View Schedule page: " + e.getMessage());
        }
    }

    // Handles opening the other page
    public void openOtherPage() {
        try {
            // Loads OtherView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherView.fxml"));
            Parent root = loader.load();

            // Calls OtherController and sets clientController
            OtherController otherController = loader.getController();
            otherController.setClientController(clientController);

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Other Services");
            stage.setScene(new Scene(root, 300, 350));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Other page: " + e.getMessage());
        }
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
