package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class AddLectureController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> timeStartComboBox;
    @FXML private ComboBox<String> timeEndComboBox;
    @FXML private ComboBox<String> roomsComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField moduleNameField;
    @FXML private TextField moduleIDField;
    @FXML private Button submitLectureButton;

    private ClientModel model; // Reference to ClientModel

    // Method to set ClientModel
    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        timeStartComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
        timeEndComboBox.getItems().addAll("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
        roomsComboBox.getItems().addAll("CSG-001", "CS1-044", "CS1-045", "CS2-044", "CS2-045", "CS3-004a", "CS3-004b", "CS3-005a", "CS3-005b");
        typeComboBox.getItems().addAll("Lec", "Lab", "Tut");

        submitLectureButton.setOnAction(e -> handleSubmitLecture());
    }

    private void handleSubmitLecture() {
        if (model == null) {
            showAlert("Error", "Server connection not established.");
            return;
        }

        String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "";
        String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "";
        String timeStart = timeStartComboBox.getValue();
        String timeEnd = timeEndComboBox.getValue();
        String room = roomsComboBox.getValue();
        String type = typeComboBox.getValue();
        String moduleName = moduleNameField.getText().trim();
        String moduleID = moduleIDField.getText().trim();

        if (startDate.isEmpty() || endDate.isEmpty() || timeStart == null || timeEnd == null ||
            room == null || type == null || moduleName.isEmpty() || moduleID.isEmpty()) {
            showAlert("Missing Fields", "Please fill in all fields before submitting.");
            return;
        }

        String lectureData = String.format("ADD_LECTURE,%s,%s,%s,%s,%s,%s,%s,%s",
                moduleName, moduleID, startDate, endDate, timeStart, timeEnd, room, type);

        new Thread(() -> {
            try {
                String response = model.sendMessage(lectureData);
                showAlert("Server Response", response);
            } catch (IOException e) {
                showAlert("Error", "Failed to communicate with server: " + e.getMessage());
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
