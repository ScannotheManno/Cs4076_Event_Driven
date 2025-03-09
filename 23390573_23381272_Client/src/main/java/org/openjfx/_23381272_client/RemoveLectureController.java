package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class RemoveLectureController {
    @FXML private ComboBox<String> lectureComboBox;
    @FXML private Button removeLectureButton;
    @FXML private ImageView logo;
    
    private ClientModel model;
    
    // Setter Method for ClientModel
    public void setModel(ClientModel model) {
        this.model = model;
    }
    
    @FXML
    public void initialize() {
        // Loads logo into UI
        Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
        logo.setImage(image);
        System.out.println("Image Loaded Successfully!");
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
    
    
    // Populates the selection box with all lecture information stored in server
    public void populateLectureDropdown(String scheduleData) {
        
        // Double checks there is no lecture data
        if (scheduleData.equals("NO_LECTURES_ARE_AVAILABLE")) {
            showAlert("Info", "No lectures have been scheduled.");
            return;
        }
        
        // Saves data to array
        String[] lectureList = scheduleData.split(";");
        
        // Add data to selection box
        lectureComboBox.getItems().addAll(lectureList);
    }

    // Handles removing lectures
    @FXML
    private void handleRemoveLecture() {
        // Finds the selected lecture from the combobox
        String selectedLecture = lectureComboBox.getValue();
        // Shows alert if nothing is selected
        if (selectedLecture == null || selectedLecture.isEmpty()) {
            showAlert("Error", "Please select a lecture to remove.");
            return;
        }

        // Sends a request to the server to remove the lecture
        String response = model.sendMessage("REMOVE_THIS_LECTURE");
        if (response.equals("REQUEST_DATA")) {
            String lectureToRemove = model.sendMessage(selectedLecture); // Sends lecture data to remove
            Platform.runLater(() -> showAlert("Server Response", lectureToRemove));
        } else {
            Platform.runLater(() -> showAlert("Error", "Unexpected server response: " + response));
        }
        
        closeWindow();
    }

    // Method to show alerts to client
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Method to close window after submission
    private void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) removeLectureButton.getScene().getWindow();
            stage.close();
        });
    }
}
