package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class OtherController {
    @FXML private ComboBox<String> dropdown;
    @FXML private TextField otherTextField;
    @FXML private Button submitButton;
    @FXML private ImageView logo;

    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        {
            Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
            logo.setImage(image);
        }
        
        //adds items to drop down box
        dropdown.getItems().addAll("Add Lecture", "Remove Lecture", "View Schedule", "Other");
        //listener to see whats added to the box
        dropdown.setOnAction(e -> {
            boolean isOther = "Other".equals(dropdown.getValue());
            otherTextField.setVisible(isOther);
            //shows text field if other is selected
            submitButton.setDisable(isOther && otherTextField.getText().trim().isEmpty());
        });
        //adds listener to text field to enable/disable the text box based on input
        otherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(dropdown.getValue().equals("Other") && newValue.trim().isEmpty());
        });

        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleSubmit() {
        String selectedOption = dropdown.getValue();
        String message = "";

        //if nothing is selected
        if (selectedOption == null) {
            showAlert("Error", "No option selected. Please select an action.");
            return;
        }

        //message based on selected option
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

        //message to the ClientController
        if (clientController != null) {
            System.out.println("Sending request from Other page: " + message); // Debug log
            clientController.sendRequestToServer(message);
        } else {
            Platform.runLater(() -> showAlert("Error", "ClientController is not set."));
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
