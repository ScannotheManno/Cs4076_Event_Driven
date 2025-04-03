package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class AddLectureController {
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeStartComboBox;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private ComboBox<String> roomsComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField moduleNameField;
    @FXML private TextField moduleIDField;
    @FXML private Button submitLectureButton;
    @FXML private ImageView logo;
    
    private ClientModel model;
    private ClientController controller;

    // Setter Method for ClientModel
    public void setModel(ClientModel model) {
        this.model = model;
    }
    
    // Setter Method for ClientController
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        // Loads logo into UI
        Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
        logo.setImage(image);
        
        
        // Populates the selection boxes on the UI
        dayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        timeStartComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
        durationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, 1));
        roomsComboBox.getItems().addAll("CSG-001", "CS1-044", "CS1-045", "CS2-044", "CS2-045", "CS3-004a", "CS3-004b", "CS3-005a", "CS3-005b");
        typeComboBox.getItems().addAll("Lec", "Lab", "Tut");
        
        
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
    
    // Handles the submit button action. Requests to send data and sends once approved
    @FXML
    private void handleSubmitButton(){
        String response = model.sendMessage("SUBMIT_LECTURE");
        if (response.equals("SEND_DATA")) {
            handleSubmitLecture();
        }
    }

    // Method to get and send lecture data
    private void handleSubmitLecture() {
        // Retrieve values entered by the client
        String lectureName = moduleNameField.getText();
        String courseID = moduleIDField.getText();
        String day = dayComboBox.getValue();
        String startTime = timeStartComboBox.getValue();
        String duration = durationSpinner.getValue().toString();
        String room = roomsComboBox.getValue();
        String type = typeComboBox.getValue();

        // Checks if a field is empty to not allow submittion
        if (lectureName.isEmpty() || courseID.isEmpty() || day == null || startTime == null || duration == null || room == null || type == null) {
            showAlert("Missing Fields", "Please fill in all fields before submitting.");
            return;
        }

        // Formats data for server
        String message = String.format("%s@%s@%s@%s@%s@%s@%s",
                lectureName, courseID, room, type, day, startTime, duration);


        // Sends data
        new Thread(() -> {
            String response = model.sendMessage(message);
            System.out.println("Server Response: " + response + "\n");
            Platform.runLater(() -> showAlert("Server Response", response));
        }).start();

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
    
    // Method to close window after submittion
    private void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) submitLectureButton.getScene().getWindow();
            stage.close();
        });
    }
}