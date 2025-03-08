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

    public ClientView(ClientModel model, ClientController clientController) {
        this.model = model;
        this.clientController = clientController;
    }

    public void openAddLectureForm() {
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

    public void openRemoveLectureForm() {
    try {
        
        String scheduleData = model.sendMessage("SEND_LECTURES");
        if (scheduleData.equals("No lectures available.") || scheduleData.trim().isEmpty()) {
            Platform.runLater(() -> showAlert("Info", "No lectures available to remove."));
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveLectureView.fxml"));
        Parent root = loader.load();

        RemoveLectureController removeLectureController = loader.getController();
        removeLectureController.populateLectureDropdown(scheduleData);
        removeLectureController.setModel(model);

        
        
        Stage stage = new Stage();
        stage.setTitle("Remove Lecture");
        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    } catch (IOException e) {
        System.out.println("Failed to open Remove Lecture page: " + e.getMessage());
    }
}


    public void openViewScheduleForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewScheduleView.fxml"));
            Parent root = loader.load();

            ViewScheduleController viewScheduleController = loader.getController();
            String scheduleData = model.sendMessage("SEND_LECTURE_DETAILS");
            viewScheduleController.populateSchedule(scheduleData);
            viewScheduleController.setModel(model);

            Stage stage = new Stage();
            stage.setTitle("View Schedule");
            stage.setScene(new Scene(root, 900, 750));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open View Schedule page: " + e.getMessage());
        }
    }

    public void openOther() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherView.fxml"));
            Parent root = loader.load();

            OtherController otherController = loader.getController();
            otherController.setClientController(clientController);

            Stage stage = new Stage();
            stage.setTitle("Other Services");
            stage.setScene(new Scene(root, 300, 350));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to open Other page: " + e.getMessage());
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
