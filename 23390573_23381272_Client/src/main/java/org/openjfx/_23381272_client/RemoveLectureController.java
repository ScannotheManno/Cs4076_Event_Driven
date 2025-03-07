package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;

public class RemoveLectureController {
    @FXML private ComboBox<String> lectureComboBox;
    @FXML private Button removeLectureButton;
    
    private ClientModel model;
    
    public void setModel(ClientModel model) {
        this.model = model;
    }
    
    
    
    public void populateLectureDropdown(String scheduleData) {
        if (scheduleData.equals("No lectures available.")) {
            showAlert("Info", "No lectures available to remove.");
            return;
        }
        String[] lectureList = scheduleData.split(";");
        lectureComboBox.getItems().addAll(lectureList);
    }

    @FXML
    private void handleRemoveLecture() {
        String selectedLecture = lectureComboBox.getValue();
        if (selectedLecture == null || selectedLecture.isEmpty()) {
            showAlert("Error", "Please select a lecture to remove.");
            return;
        }

        try {
            String response = model.sendMessage("REMOVE_THIS_LECTURE");
            if (response.equals("SEND_DATA")) {
                String lectureToRemove = model.sendMessage(selectedLecture);
                Platform.runLater(() -> showAlert("Server Response", lectureToRemove));
            } else {
                Platform.runLater(() -> showAlert("Error", "Unexpected server response: " + response));
            }
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Error", "Failed to communicate with server: " + e.getMessage()));
        }
        
        closeWindow();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) removeLectureButton.getScene().getWindow();
            stage.close();
        });
    }
}
