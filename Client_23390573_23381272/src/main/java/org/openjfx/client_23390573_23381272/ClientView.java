/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.client_23390573_23381272;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.time.LocalDate;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;

/**
 *
 * @author lukes
 */

public class ClientView {
    private Stage primaryStage;
    private Button addLectureButton;
    private Button removeLectureButton;
    private Button viewScheduleButton;
    private Button otherButton;
    private Button quitButton;
    
    public ClientView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initUI();
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
    
    // Getters for controller to attach event handlers
    public Button getAddLectureButton() { return addLectureButton; }
    public Button getRemoveLectureButton() { return removeLectureButton; }
    public Button getViewScheduleButton() { return viewScheduleButton; }
    public Button getotherButton() { return otherButton; }
    public Button getQuitButton() { return quitButton; }
    
    // Methods to display additional forms/windows

public void showAddLectureForm() {
    Stage lectureAddStage = new Stage();
    VBox layout = new VBox();
    layout.setSpacing(10);
    layout.setAlignment(Pos.CENTER);
    
    // Date pickers for start and end dates
    Label startDateLabel = new Label("Date:");
    DatePicker startDatePicker = new DatePicker();
    Label timeLabel = new Label("Time:");
    ComboBox<String> timeComboBox = new ComboBox<>();
    timeComboBox.getItems().addAll(
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00", "18:00"
    );
    timeComboBox.setPromptText("Select time");
    
    // Text fields for lecture and course names
    Label lectureNameLabel = new Label("Lecture Name:");
    TextField lectureNameField = new TextField();
    lectureNameField.setPromptText("Enter lecture name");
    lectureNameField.setMaxWidth(200);
    Label courseNameLabel = new Label("Course ID:");
    TextField courseNameField = new TextField();
    courseNameField.setPromptText("Enter course name");
    courseNameField.setMaxWidth(200);
    // Submit button to process the input
    Button submitButton = new Button("Submit");
    submitButton.setOnAction(e -> {
        LocalDate startDate = startDatePicker.getValue();
        String time = timeComboBox.getValue();
        String lectureName = lectureNameField.getText();
        String courseName = courseNameField.getText();
        
        // For now, simply print out the values. In a real application, you'd likely send these details to the server.
        System.out.println("Lecture Name: " + lectureName);
        System.out.println("Course Name: " + courseName);
        System.out.println("Date: " + startDate);
        System.out.println("Time: " + time);
        
        // You might also want to close the window after submission:
        lectureAddStage.close();
    });
    
    // Add all components to the layout
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
        Stage scheduleStage = new Stage();
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);
        scheduleStage.setTitle("Other");
        scheduleStage.setScene(scene);
        scheduleStage.show();
    }
}
