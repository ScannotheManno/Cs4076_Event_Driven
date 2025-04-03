package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ViewScheduleController {
    @FXML private GridPane scheduleGrid;
    @FXML private ImageView logo;
    @FXML private Button backButton;
    
    private ClientModel model;
    
    // Setter Method for ClientModel
    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        // Loads logo into UI
        Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
        logo.setImage(image);
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

    // Handles the back button
    @FXML
    private void handleBackButton() {
        // Sends BACK to server. If the response is RETURNING then close window
        String response = model.sendMessage("BACK");
        if (response.equals("RETURNING")) {
            closeWindow();
        }
    }

    // initialises the timeSlots for plotting
    private static final String[] timeSlots = {
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00", "18:00"};
    
    private static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    // Handles populating the timeable
    public void populateSchedule(String scheduleData) {

        // Checks if there are lectures saved to the server. If server says NO_LECTURES_SCHEDULED then create blank timetable
        if (scheduleData.equals("NO_LECTURES_SCHEDULED")) {
            System.out.println("No lectures scheduled.\n");
            createTimetable();
            return;
        }

        createTimetable();
        Platform.runLater(() -> {
            // Store data in an array
            String[] lectures = scheduleData.split(";");
            for (String lecture : lectures) {
                try {
                    // Store individual lectures in an array
                    String[] details = lecture.split("@");
                    
                    // If the correct amount of data is sent then continue
                    if (details.length == 7) {
                        String moduleName = details[0];
                        String moduleID = details[1];
                        String room = details[2];
                        String type = details[3];
                        String day = details[4];
                        String startTime = details[5];
                        String duration = details[6];

                        // Variables for positioning on the timetable
                        int col = getColumnForDay(day);
                        int row = getRowForTime(startTime);
                        
                        System.out.println("📌 Placing: " + moduleName + " at Column: " + col + ", Row: " + row);

                        // Ensures correct positioning
                        if (col != -1 && row != -1) {
                            // Create StackPane for the timetable slots
                            StackPane lectureBox = new StackPane();

                            // Rectangle to create the boxes around the lecture info
                            Rectangle background;
                            
                            // Handles size of slot depending on duration of class
                            if (duration.equals("1")) {
                                background = new Rectangle(120, 55);
                                background.setFill(Color.WHITE);
                                background.setStroke(Color.BLACK);
                            } else {
                                background = new Rectangle(120, 110);
                                background.setFill(Color.WHITE);
                                background.setStroke(Color.BLACK);
                            }
                            
                            // Label for start time of the class
                            Label timeLabel = new Label(startTime);
                            timeLabel.setFont(Font.font("Arial", 9));
                            timeLabel.setTextFill(Color.BLACK);
                            
                            // label for the modules name
                            Label nameLabel = new Label(moduleName);
                            nameLabel.setFont(Font.font("Arial", 9));
                            nameLabel.setTextFill(Color.BLACK);
                            
                            // Label for the module id code anf the type of class
                            Label idLabel = new Label(moduleID + "_" + type);
                            idLabel.setFont(Font.font("Arial", 9));
                            idLabel.setTextFill(Color.BLACK);
                            
                            // Label for the room the lecture is in
                            Label roomLabel = new Label(room);
                            roomLabel.setFont(Font.font("Arial", 9));
                            roomLabel.setTextFill(Color.BLACK);
                            
                            // VBox to stack the info vertically
                            VBox lectureVBox = new VBox();
                            lectureVBox.setAlignment(Pos.CENTER);
                            
                            // Add lecture info labels to VBox
                            lectureVBox.getChildren().addAll(timeLabel,idLabel, nameLabel, roomLabel);
                            
                            // Add rectangle and VBox to StackPane
                            lectureBox.getChildren().addAll(background, lectureVBox);

                            // Handles removing blank slots in the timetable depending on duration (1 slot for 1 hour, 2 slots for 2 hours)
                            if (duration.equals("1")) {
                                scheduleGrid.getChildren().removeIf(node ->
                                        GridPane.getColumnIndex(node) == col + 1 &&
                                                GridPane.getRowIndex(node) == row + 1
                                );
                            } else {
                                for (int i = 0; i < 2; i++) {
                                    int removeRow = row + 1 + i;
                                    int removeCol = col + 1;

                                    scheduleGrid.getChildren().removeIf(node ->
                                            GridPane.getColumnIndex(node) == removeCol &&
                                                    GridPane.getRowIndex(node) == removeRow
                                    );
                                }
                            }
                            
                            // Adds lecture to timetable
                            int durationInt = Integer.parseInt(duration);
                            scheduleGrid.add(lectureBox, col + 1, row + 1, 1, durationInt);

                        } else {
                            System.out.println("Invalid Position: " + moduleName + " (" + day + " " + startTime + ")\n");
                        }
                    } else {
                        System.out.println("Invalid Data Format: " + lecture + "\n");
                    }
                } catch (Exception e) {
                    System.out.println("Error processing lecture data: " + e.getMessage());
                }
            }
        });
    }

    // Helper method to base columns on the days of the week
    private int getColumnForDay(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                return i;
            }
        }
        return -1;
    }

    // Helper method to base rows on start times
    private int getRowForTime(String time) {
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equalsIgnoreCase(time)) {
                return i;
            }
        }
        return -1;
    }

    // Creates base blank timetable
    private void createTimetable() {
        Platform.runLater(() -> {
            // Clears the previous schedule data to recreate blank timetable
            scheduleGrid.getChildren().clear();

            // Loop to create time row for days
            for (int col = 0; col < days.length; col++) {
                // StackPane for rectangle and day label
                StackPane headerPane = new StackPane();

                // Create Rectangle
                Rectangle background = new Rectangle(120, 30);
                background.setFill(Color.GREEN);
                background.setStroke(Color.BLACK);

                // Get the correct day
                Label dayLabel = new Label(days[col]);
                dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                dayLabel.setTextFill(Color.WHITE);

                // Add to StackPane
                headerPane.getChildren().addAll(background, dayLabel);

                // Place on the timetable grid
                scheduleGrid.add(headerPane, col + 1, 0);
            }


            // Loop for creating blank timeslots on timetable
            for (int col = 0; col < days.length; col++) {
                for (int row = 0; row < timeSlots.length; row++) {
                    // StackPane for blank timeslots
                    StackPane blankBox = new StackPane();

                    // Creates rectangle for timeslot
                    Rectangle blankBackground = new Rectangle(120, 55);
                    blankBackground.setFill(Color.WHITE);
                    blankBackground.setStroke(Color.BLACK);

                    // Adds blank timeslot to StackPane
                    blankBox.getChildren().add(blankBackground);

                    // Place on the timetable grid
                    scheduleGrid.add(blankBox, col + 1, row + 1);
                }
            }
        });
    }
    
    // Method to close window when back button is hit
     private void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }
}