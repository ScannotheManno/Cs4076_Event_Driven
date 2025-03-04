package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class OtherController {
    @FXML private ComboBox<String> dropdown;
    @FXML private TextField otherTextField;
    @FXML private Button submitButton;

    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        dropdown.getItems().addAll("Add Lecture", "Remove Lecture", "View Schedule", "Other");
        dropdown.setOnAction(e -> {
            boolean isOther = "Other".equals(dropdown.getValue());
            otherTextField.setVisible(isOther);
            submitButton.setDisable(isOther && otherTextField.getText().trim().isEmpty());
        });

        otherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(dropdown.getValue().equals("Other") && newValue.trim().isEmpty());
        });

        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleSubmit() {
        String selectedOption = dropdown.getValue();
        String message = "";

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
                    showAlert("Error", "Please enter a request for 'Other'.");
                    return;
                }
                break;
            default:
                showAlert("Error", "Invalid selection. Please try again.");
                return;
        }

        if (clientController != null) {
            System.out.println("Sending request from Other page: " + message); // Debug log
            clientController.sendRequestToServer(message);
        } else {
            showAlert("Error", "ClientController is not set.");
            return;
        }

        Platform.runLater(() -> {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
