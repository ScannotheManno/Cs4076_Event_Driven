package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OtherController {
    @FXML private ComboBox<String> dropdown;
    @FXML private TextField otherTextField;
    @FXML private Button submitButton;
    @FXML private ImageView logo;

    
    private ClientController clientController;

    // Setter method for ClientController
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        // Loads logo into UI
        Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
        logo.setImage(image);
        
        // Adds items to drop down box
        dropdown.getItems().addAll("Add Lecture", "Remove Lecture", "View Schedule", "Other");
        // Handles other selection
        dropdown.setOnAction(e -> {
            boolean isOther = "Other".equals(dropdown.getValue());
            // If other is selected then show textbox and disable submitButton
            otherTextField.setVisible(isOther);
            submitButton.setDisable(isOther && otherTextField.getText().trim().isEmpty());
        });
        // Adds listener to text field to enable/disable the text box based on input
        otherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(dropdown.getValue().equals("Other") && newValue.trim().isEmpty());
        });
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

    // Handles submitting request
    @FXML
    private void handleSubmit() {
        // Gets the selected option
        String selectedOption = dropdown.getValue();
        String message = "";

        // If nothing is selected alert and prompt client to select
        if (selectedOption == null) {
            showAlert("Error", "No option selected. Please select an action.");
            return;
        }

        // Message based on selected option
        switch (selectedOption) {
            case "Add Lecture":
                message = "ADD_LECTURE"; // Request to open add lecture page
                break;
            case "Remove Lecture":
                message = "REMOVE_LECTURE"; // Request to open remove lecture page
                break;
            case "View Schedule":
                message = "VIEW_SCHEDULE"; // Request to open the schedule
                break;
            case "Other": // Handles other requests
                message = otherTextField.getText().trim();
                // If message is empty then alert client
                if (message.isEmpty()) {
                    showAlert("Error", "Please enter a request for 'Other'.");
                    return;
                }
                break;
            default: // Default response if issues arise
                showAlert("Error", "Invalid selection. Please try again.");
                return;
        }

        // Handles page opening via ClientController
        if (clientController != null) {
            clientController.sendRequestToServer(message);
        } else {
            Platform.runLater(() -> showAlert("Error", "ClientController is not working."));
            return;
        }

        // Closes page when submit is successful
        Platform.runLater(() -> {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        });
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
