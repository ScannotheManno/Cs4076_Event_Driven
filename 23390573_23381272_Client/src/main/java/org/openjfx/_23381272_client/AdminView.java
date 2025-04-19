package org.openjfx._23381272_client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AdminView {
    private ClientModel model;
    private AdminController adminController;

    public AdminView(ClientModel model, AdminController adminController) {
        this.model = model;
        this.adminController = adminController;
    }

    public void openManageStudentPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/_23381272_client/ManageStudentView.fxml"));
            Parent root = loader.load();

            ManageStudentController controller = loader.getController();
            controller.setModel(model);

            Stage stage = new Stage();
            stage.setTitle("Manage Students");
            stage.setScene(new Scene(root, 375, 400));
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to open Manage Users page: " + e.getMessage());
            e.printStackTrace(); // Optional but recommended for detailed errors
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminAddLectureView.fxml"));
            Parent root = loader.load();

            // Calls addLectureController and sets the model
            AddLectureControllerAdmin addLectureControllerAdmin = loader.getController();
            addLectureControllerAdmin.setModel(model);

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Add Lecture Admin");
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
            String scheduleData = model.sendMessage("SEND_LECTURES_ADMIN");
            // If no lectures are scheduled then alert client an do not open page
            if (scheduleData.equals("NO_LECTURES_AVAILABLE") || scheduleData.trim().isEmpty()) {
                Platform.runLater(() -> showAlert("Info", "No lectures available to remove."));
                return;
            }

            // Loads RemoveLectureView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminRemoveLectureView.fxml"));
            Parent root = loader.load();

            // Calls RemoveLectureController, populates the dropdown box and sets model
            RemoveLectureControllerAdmin removeLectureController = loader.getController();
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
    
     public void openGroupTimetablePage() {
        try {
            // Loads ViewScheduleView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupAdminTimetableView.fxml"));
            Parent root = loader.load();

            // Calls ViewScheduleController
            AdminViewScheduleController viewScheduleController = loader.getController();
            // Requests lecture data from server and populates the schdule
            String scheduleData = model.sendMessage("SEND_LECTURE_DETAILS_GROUP");
            viewScheduleController.populateSchedule(scheduleData);
            // Sets model
            viewScheduleController.setModel(model);
            viewScheduleController.setView(this);
            

            // Creates and shows stage
            Stage stage = new Stage();
            stage.setTitle("Class Timetable");
            stage.setScene(new Scene(root, 900, 750));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open View Schedule page: " + e.getMessage());
        }
    }


    
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
