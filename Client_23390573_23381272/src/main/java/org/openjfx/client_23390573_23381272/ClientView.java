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
    Label moduleNameLabel = new Label("Mondule Name:");
    TextField moduleNameField = new TextField();
    moduleNameField.setPromptText("Enter Mondule name");
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
    
    public void showSchedule() {
        Stage scheduleStage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(10); // Horizontal gap between columns
        grid.setVgap(10); // Vertical gap between rows
        grid.setAlignment(Pos.CENTER); // Center the grid on the screen

        // Create column and row labels for the timetable
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};

        for (int col = 0; col < days.length; col++) {
            Label dayLabel = new Label(days[col]);
            dayLabel.setStyle("-fx-font-size: 18px;");
            grid.add(dayLabel, col + 1, 0);
        }

        for (int col = 0; col < days.length; col++) {
            for (int row = 0; row < times.length; row++) {
                Label timeLabel = new Label(times[row]);
                timeLabel.setStyle("-fx-font-size: 16px;");
                grid.add(timeLabel, 0, row + 1);

                // Add timetable entry labels
                Label timetableEntry = new Label("Class");
                timetableEntry.setStyle("-fx-font-size: 16px;");
                grid.add(timetableEntry, col + 1, row + 1);
            }
        }
        Scene scheduleScene = new Scene(grid, 600, 400);
        scheduleStage.setTitle("Timetable");
        scheduleStage.setScene(scheduleScene);
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
