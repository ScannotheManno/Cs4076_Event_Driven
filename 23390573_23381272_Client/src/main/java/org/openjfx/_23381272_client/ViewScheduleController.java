package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ViewScheduleController {
    @FXML private GridPane scheduleGrid;

    // Define the time slots in the correct order
    private static final String[] timeSlots = {
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00"};

    public void populateSchedule(String scheduleData) {
        if (scheduleData.equals("No lectures scheduled.")) {
            System.out.println("No lectures available.");
            return;
        }

        createTimetable();
        Platform.runLater(() -> {
            
            String[] lectures = scheduleData.split(";");
            for (String lecture : lectures) {
                String[] details = lecture.split(",");
                if (details.length == 7) {
                    String moduleName = details[0];
                    String moduleID = details[1];
                    String room = details[2];
                    String type = details[3];
                    String day = details[4];
                    String startTime = details[5];
                    String duration = details[6];
                    
                    int col = getColumnForDay(day);
                    int row = getRowForTime(startTime);
                    
                    
                    for (int i = moduleID.length()/2; i < ((moduleName.length() / 2) + (moduleID.length() / 4)); i++) {
                        moduleID = " " + moduleID;
                    }
                    
                    
                    for (int i = room.length()/2; i < ((moduleName.length() / 2) + (room.length() / 4)); i++) {
                        room = " " + room;
                    }
                    
                    for (int i = startTime.length()/2; i < ((moduleName.length() / 2) + (startTime.length() / 4)); i++) {
                        startTime = " " + startTime;
                    }

                    System.out.println("📌 Placing: " + moduleName + " at Column: " + col + ", Row: " + row);

                    if (col != -1 && row != -1) {
                        // Create a stack to hold the black box and text
                        StackPane lectureBox = new StackPane();

                        Rectangle background;
                        if (duration.equals("1")) {
                            background = new Rectangle(200, 40);
                            background.setFill(Color.WHITE);
                            background.setStroke(Color.BLACK);
                        } else {
                            background = new Rectangle(200, 80);
                            background.setFill(Color.WHITE);
                            background.setStroke(Color.BLACK);
                            
                        }
                        // Create label for lecture name
                        Label lectureLabel = new Label(startTime + "\n" + moduleName + "\n" + moduleID + "-" + type + "\n" + room );
                        lectureLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        lectureLabel.setTextFill(Color.BLACK);

                        // Add background and text to the stack
                        lectureBox.getChildren().addAll(background, lectureLabel);
                        
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
                        int durationInt = Integer.parseInt(duration); // Convert duration to an integer
                        scheduleGrid.add(lectureBox, col + 1, row + 1, 1, durationInt); // Span rows properly

                    } else {
                        System.out.println("❌ Invalid Position: " + moduleName + " (" + day + " " + startTime + ")");
                    }
                } else {
                    System.out.println("❌ Invalid Data Format: " + lecture);
                }
            }
        });
    }


    private int getColumnForDay(String day) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                return i; // ✅ Returns column index starting from 0
            }
        }
        return -1; // ❌ Error case
    }

    private int getRowForTime(String time) {
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equalsIgnoreCase(time)) {
                return i; // ✅ Ensures 9 AM at the top and 6 PM at the bottom
            }
        }
        return -1; // ❌ Error case
    }
    
    private void createTimetable() {
        Platform.runLater(() -> {
            scheduleGrid.getChildren().clear(); // ✅ Clears everything before adding elements

            // Add day headers at the top (Monday - Friday)
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            for (int col = 0; col < days.length; col++) {
                StackPane headerPane = new StackPane();

                // Create black background rectangle for day headers
                Rectangle background = new Rectangle(200, 40);
                background.setFill(Color.GREEN);
                background.setStroke(Color.BLACK);

                // Create label for the day name
                Label dayLabel = new Label(days[col]);
                dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                dayLabel.setTextFill(Color.WHITE);

                // Stack the label on top of the rectangle
                headerPane.getChildren().addAll(background, dayLabel);

                // Add to the GridPane at row 0 (header row)
                scheduleGrid.add(headerPane, col + 1, 0);
            }

            // Define all time slots
            String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00",
                                  "14:00", "15:00", "16:00", "17:00"};

            // Fill the entire timetable with blank boxes
            for (int col = 0; col < days.length; col++) { // Loop through days
                for (int row = 0; row < timeSlots.length; row++) { // Loop through times
                    StackPane blankBox = new StackPane();

                    // Create a light gray background rectangle for empty slots
                    Rectangle blankBackground = new Rectangle(200, 40);
                    blankBackground.setFill(Color.WHITE);
                    blankBackground.setStroke(Color.BLACK);

                    blankBox.getChildren().add(blankBackground);

                    // Add blank box to grid at (col + 1, row + 1) since headers are in row 0
                    scheduleGrid.add(blankBox, col + 1, row + 1);
                }
            }
        });
    }
}