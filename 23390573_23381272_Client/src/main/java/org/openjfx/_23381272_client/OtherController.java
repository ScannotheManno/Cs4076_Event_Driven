package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class OtherController {
    @FXML private ComboBox<String> dropdown;
    @FXML private TextField otherTextField;
    @FXML private Button submitButton;

    private ClientController clientController; // Reference to main controller

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        dropdown.getItems().addAll("Add Lecture", "Remove Lecture", "View Schedule", "Other");
        dropdown.setOnAction(e -> otherTextField.setVisible("Other".equals(dropdown.getValue())));

        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleSubmit() {
        String selectedOption = dropdown.getValue();
        String message;

        if (selectedOption == null) {
            showAlert("Error", "No option selected. Please select an action.");
            return;
        }

        switch (selectedOption) {
            case "Add Lecture":
                message = "ADD_LECTURE";
                break;
            case "Remove Lecture":
                message = "REMOVE_LECTURE";
                break;
            case "View Schedule":
                message = "VIEW_SCHEDULE";
                break;
            case "Other":
                message = otherTextField.getText().trim();
                if (message.isEmpty()) {
                    showAlert("Error", "No request entered for 'Other'. Please enter a request.");
                    return;
                }
                break;
            default:
                showAlert("Error", "Invalid selection. Please try again.");
                return;
        }

        if (clientController != null) {
            clientController.handleOtherRequest(message);
        } else {
            showAlert("Error", "ClientController is not set.");
        }

        // Close the window after submission
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
