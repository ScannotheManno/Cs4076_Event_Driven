package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    public void initialize() {
        timeStartComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
        timeEndComboBox.getItems().addAll("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
        roomsComboBox.getItems().addAll("CSG-001", "CS1-044", "CS1-045", "CS2-044", "CS2-045", "CS3-004a", "CS3-004b", "CS3-005a", "CS3-005b");
        typeComboBox.getItems().addAll("Lec", "Lab", "Tut");

        submitLectureButton.setOnAction(e -> handleSubmitLecture());
    }

    private void handleSubmitLecture() {
        System.out.println("✅ Submit button clicked. Implement logic to send data to server.");
        // Implement sending lecture details to server here
    }
}
