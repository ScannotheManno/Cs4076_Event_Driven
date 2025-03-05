package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;

public class AddLectureController {
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeStartComboBox;
    @FXML private ComboBox<String> timeEndComboBox;
    @FXML private ComboBox<String> roomsComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField moduleNameField;
    @FXML private TextField moduleIDField;
    @FXML private Button submitLectureButton;

    private ClientModel model;
    private ClientController controller;

    public void setModel(ClientModel model) {
        this.model = model;
    }
    
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        dayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        timeStartComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
        timeEndComboBox.getItems().addAll("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
        roomsComboBox.getItems().addAll("CSG-001", "CS1-044", "CS1-045", "CS2-044", "CS2-045", "CS3-004a", "CS3-004b", "CS3-005a", "CS3-005b");
        typeComboBox.getItems().addAll("Lec", "Lab", "Tut");

        submitLectureButton.setOnAction(e -> handleSubmitButton());
        
        Platform.runLater(() -> {
            Stage stage = (Stage) submitLectureButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("❌ Window closed using the X button. No request sent.");
            });
        });
    }
    
    private void handleSubmitButton(){
        try {
            String response = model.sendMessage("SUBMIT_LECTURE");
            if (response.equals("SEND_DATA")) {
                handleSubmitLecture();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSubmitLecture() {
        String lectureName = moduleNameField.getText();
        String courseID = moduleIDField.getText();
        String day = dayComboBox.getValue();
        String startTime = timeStartComboBox.getValue();
        String endTime = timeEndComboBox.getValue();
        String room = roomsComboBox.getValue();
        String type = typeComboBox.getValue();

        if (lectureName.isEmpty() || courseID.isEmpty() || day == null || startTime == null || endTime == null || room == null || type == null) {
            showAlert("Missing Fields", "Please fill in all fields before submitting.");
            return;
        }

        // Format message properly
        String message = String.format("%s,%s,%s,%s,%s,%s,%s",
                lectureName, courseID, room, type, day, startTime, endTime);

        System.out.println("📤 Sending to server: " + message); // Debugging log

        new Thread(() -> {
            try {
                String response = model.sendMessage(message);
                System.out.println("📩 Server Response: " + response); // Debugging log
                Platform.runLater(() -> showAlert("Server Response", response));
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Failed to communicate with server: " + e.getMessage()));
            }
        }).start();

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
            Stage stage = (Stage) submitLectureButton.getScene().getWindow();
            stage.close();
        });
    }
}