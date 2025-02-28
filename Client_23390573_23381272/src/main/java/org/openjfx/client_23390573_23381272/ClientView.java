/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.client_23390573_23381272;

/**
 * 
 * @author Luke
 */

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

public class ClientView {
    private Stage primaryStage;
    private Button addLectureButton;
    private Button removeLectureButton;
    private Button viewScheduleButton;
    private Button otherButton;
    private Button quitButton;
    
    private ClientController controller;
    
    public ClientView(ClientController controller) {
        this.controller = controller;
    }
    
    public ClientView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initUI();
    }
    
    public void setController(ClientController controller) {
        this.controller = controller;
    }
    
    private void initUI() {
        VBox root = new VBox();
        root.setSpacing(15);
        root.setAlignment(Pos.CENTER);
        
        addLectureButton = new Button("Add a Lecture");
        removeLectureButton = new Button("Remove a Lecture");
        viewScheduleButton = new Button("View Schedule");
        otherButton = new Button("Other");
        quitButton = new Button("Quit");
        
        addLectureButton.setPrefSize(120, 50);
        removeLectureButton.setPrefSize(120, 50);
        viewScheduleButton.setPrefSize(120, 50);
        otherButton.setPrefSize(120, 50);
        quitButton.setPrefSize(120, 50);
        
        root.getChildren().addAll(addLectureButton, removeLectureButton, viewScheduleButton, otherButton, quitButton);
        Scene scene = new Scene(root, 600, 400);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Client");
        primaryStage.show();
    }
    
    public Button getAddLectureButton() { return addLectureButton; }
    public Button getRemoveLectureButton() { return removeLectureButton; }
    public Button getViewScheduleButton() { return viewScheduleButton; }
    public Button getotherButton() { return otherButton; }
    public Button getQuitButton() { return quitButton; }
    

public void showAddLectureForm() {
    Stage lectureAddStage = new Stage();
    VBox layout = new VBox();
    layout.setSpacing(10);
    layout.setAlignment(Pos.CENTER);
    
    Label startDateLabel = new Label("Date:");
    DatePicker startDatePicker = new DatePicker();
    Label timeLabel = new Label("Time:");
    ComboBox<String> timeComboBox = new ComboBox<>();
    timeComboBox.getItems().addAll(
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00", "18:00"
    );
    timeComboBox.setPromptText("Select time");
    
    Label lectureNameLabel = new Label("Lecture Name:");
    TextField lectureNameField = new TextField();
    lectureNameField.setPromptText("Enter lecture name");
    lectureNameField.setMaxWidth(200);
    Label courseNameLabel = new Label("Course ID:");
    TextField courseNameField = new TextField();
    courseNameField.setPromptText("Enter course name");
    courseNameField.setMaxWidth(200);

    Button submitButton = new Button("Submit");
    submitButton.setOnAction(e -> {
        LocalDate startDate = startDatePicker.getValue();
        String time = timeComboBox.getValue();
        String lectureName = lectureNameField.getText();
        String courseName = courseNameField.getText();
        
        System.out.println("Lecture Name: " + lectureName);
        System.out.println("Course Name: " + courseName);
        System.out.println("Date: " + startDate);
        System.out.println("Time: " + time);
        
        lectureAddStage.close();
    });
    
    layout.getChildren().addAll(
        startDateLabel, startDatePicker, 
        timeLabel, timeComboBox, 
        lectureNameLabel, lectureNameField, 
        courseNameLabel, courseNameField,
        submitButton
    );
    
    Scene scene = new Scene(layout, 300, 400);
    lectureAddStage.setTitle("Enter Lecture Details To Add");
    lectureAddStage.setScene(scene);
    lectureAddStage.show();
}

    
    public void showRemoveLectureForm() {
        Stage lectureRemoveStage = new Stage();
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);
        lectureRemoveStage.setTitle("Enter Lecture Details To Remove");
        lectureRemoveStage.setScene(scene);
        lectureRemoveStage.show();
    }
    
    public void showSchedule() {
        Stage scheduleStage = new Stage();
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);
        scheduleStage.setTitle("Timetable");
        scheduleStage.setScene(scene);
        scheduleStage.show();
    }
    



    public void otherButton() {
    Stage otherStage = new Stage();
    VBox layout = new VBox();
    layout.setSpacing(10);
    layout.setAlignment(Pos.CENTER);
    Label otherLabel = new Label("Make a Request:");

    ComboBox<String> dropdown = new ComboBox<>();
    dropdown.getItems().addAll("Add Lecture", "Remove Lecture", "View Schedule", "Other");
    dropdown.setPromptText("Choose a request:");

    TextField otherTextField = new TextField();
    otherTextField.setPromptText("Enter your request");
    otherTextField.setMaxWidth(200);
    otherTextField.setVisible(false);
    dropdown.setOnAction(e -> {
        if ("Other".equals(dropdown.getValue())) {
            otherTextField.setVisible(true);
        } else {
            otherTextField.setVisible(false);
        }
    });
    
    Button submitButton = new Button("Submit");
    submitButton.setOnAction(e -> {
        if (controller == null) {
            System.out.println("Error: Controller is NULL!");
            return;
        }

        String selectedOption = dropdown.getValue();
        String message = null;

        if (selectedOption == null) {
            System.out.println("No option selected.");
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
                
                String userRequest = otherTextField.getText().trim();
                if (userRequest.isEmpty()) {
                    Platform.runLater(() -> showAlert("No Request Made", "No other request entered."));
                    System.out.println("No custom request entered.");
                    return; 
                }
                message = userRequest;
                break;
            default:
                System.out.println("Invalid selection.");
                return;
        }
        controller.handleOtherRequest(message);

        javafx.application.Platform.runLater(() -> otherStage.close());
    });

    layout.getChildren().addAll(otherLabel, dropdown, otherTextField, submitButton);

    Scene scene = new Scene(layout, 300, 250);
    otherStage.setTitle("Select a Service");
    otherStage.setScene(scene);
    otherStage.show();
}

private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

}
