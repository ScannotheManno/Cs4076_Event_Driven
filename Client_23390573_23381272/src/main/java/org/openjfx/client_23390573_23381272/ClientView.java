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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;

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
        mainMenu();
        
    }
    
    public void setController(ClientController controller) {
        this.controller = controller;
    }
    
   
    public void mainMenu() {
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
        primaryStage.setTitle("Main Menu");
        primaryStage.show();
    }
    
    public Button getAddLectureButton() {
        return addLectureButton;
    }
    
    public Button getRemoveLectureButton() {
        return removeLectureButton;
    }
    
    public Button getViewScheduleButton() {
        return viewScheduleButton;
    }
    
    public Button getotherButton() {
        return otherButton;
    }
    
    public Button getQuitButton() {
        return quitButton;
    }
    

    public void showAddLectureForm() {
        Stage lectureAddStage = new Stage();
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);

        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();

        Label timeStartLabel = new Label("Start Time:");
        ComboBox<String> timeStartComboBox = new ComboBox<>();
        Label timeEndLabel = new Label("End Time:");
        ComboBox<String> timeEndComboBox = new ComboBox<>();
        Label roomsLabel = new Label("Room:");
        ComboBox<String> roomsComboBox = new ComboBox<>();
        Label typeLabel = new Label("Class Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();

        timeStartComboBox.getItems().addAll(
            "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00");
        timeStartComboBox.setPromptText("Select Start Time");

        timeEndComboBox.getItems().addAll(
            "10:00", "11:00", "12:00", "13:00", "14:00",
            "15:00", "16:00", "17:00", "18:00");
        timeEndComboBox.setPromptText("Select End Time");

        roomsComboBox.getItems().addAll(
            "CSG-001", "CS1-044","CS1-045", "CS2-044", "CS2-045",
                "CS3-004a", "CS3-004b", "CS3-005a", "CS3-005b");
        roomsComboBox.setPromptText("Select Room");

        typeComboBox.getItems().addAll("Lec", "Lab", "Tut");
        typeComboBox.setPromptText("Select Class Type");
        Label moduleNameLabel = new Label("Module Name:");
        TextField moduleNameField = new TextField();
        moduleNameField.setPromptText("Enter Module name");
        moduleNameField.setMaxWidth(200);
        Label moduleIDLabel = new Label("Module ID:");
        TextField moduleIDField = new TextField();
        moduleIDField.setPromptText("Enter Module ID");
        moduleIDField.setMaxWidth(200);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String room = roomsComboBox.getValue();
            String type = typeComboBox.getValue();
            String startTime = timeStartComboBox.getValue();
            String endTime = timeEndComboBox.getValue();
            String moduleName = moduleNameField.getText();
            String moduleID = moduleIDField.getText();

            if (moduleName.isEmpty() || moduleID.isEmpty() || room == null || type == null || startDate == null || endDate == null || startTime == null || endTime == null) {
                showAlert("Error", "All fields must be filled out!");
                return;
            }

            controller.addLecture(moduleName, moduleID, room, type, startDate, endDate, startTime, endTime);

            lectureAddStage.close();
        });

        layout.getChildren().addAll(
            moduleNameLabel, moduleNameField, 
            moduleIDLabel, moduleIDField,
            startDateLabel, startDatePicker,
            endDateLabel, endDatePicker,
            roomsLabel, roomsComboBox,
            typeLabel, typeComboBox,
            timeStartLabel, timeStartComboBox,
            timeEndLabel, timeEndComboBox,

            submitButton
        );

        Scene scene = new Scene(layout, 300, 700);
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
    
    public void showSchedule(String schedule) {
    Stage scheduleStage = new Stage();
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setAlignment(Pos.CENTER);

    // ✅ Define time slots (rows)
    String[] timeSlots = {"9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM"};

    // ✅ Define days (columns)
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    // ✅ Add time labels to the first column
    for (int i = 0; i < timeSlots.length; i++) {
        Label timeLabel = new Label(timeSlots[i]);
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(timeLabel, 0, i + 1); // Column 0, Row i+1
    }

    // ✅ Add day labels to the first row
    for (int i = 0; i < days.length; i++) {
        Label dayLabel = new Label(days[i]);
        dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        dayLabel.setTextFill(Color.BLUE);
        grid.add(dayLabel, i + 1, 0); // Column i+1, Row 0
    }

    Scene scene = new Scene(grid, 600, 400);
    scheduleStage.setTitle("Weekly Schedule");
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
        
        String selectedOption = dropdown.getValue();
        String message = null;

        if (selectedOption == null) {
            Platform.runLater(() -> showAlert("Empty Seclection", "No option selected. Please try again."));
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
                    Platform.runLater(() -> showAlert("No Request Made", "No other request entered"));
                    return; 
                }
                message = userRequest;
                break;
            default:
                Platform.runLater(() -> showAlert("Invalid Selection", "Invalid selection. Please try again."));
                return;
        }
        controller.handleOtherRequest(message);

        Platform.runLater(() -> otherStage.close());
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
