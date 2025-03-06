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
        "14:00", "15:00", "16:00", "17:00", "18:00"
    };

    
    @FXML
    public void initialize() {
        scheduleGrid.getChildren().clear(); // Clear any old data

        // Add headers for days (Monday - Friday)
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int col = 0; col < days.length; col++) {
            Label dayLabel = new Label(days[col]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            dayLabel.setTextFill(Color.BLUE); // Make it stand out

            // Add the day label to the first row (rowIndex = 0), one per column
            scheduleGrid.add(dayLabel, col + 1, 0);
        }
    }

    
    public void populateSchedule(String scheduleData) {
        if (scheduleData.equals("No lectures scheduled.")) {
            System.out.println("No lectures available.");
            return;
        }

        Platform.runLater(() -> {
            scheduleGrid.getChildren().clear(); // ✅ Clear previous entries

            String[] lectures = scheduleData.split(";");
            for (String lecture : lectures) {
                String[] details = lecture.split(",");
                if (details.length == 7) {
                    String moduleName = details[0];
                    String day = details[4];
                    String startTime = details[5];

                    int col = getColumnForDay(day);
                    int row = getRowForTime(startTime);

                    System.out.println("📌 Placing: " + moduleName + " at Column: " + col + ", Row: " + row);

                    if (col != -1 && row != -1) {
                        // Create a stack to hold the black box and text
                        StackPane lectureBox = new StackPane();

                        // Create black background rectangle
                        Rectangle background = new Rectangle(100, 40); // Adjust width & height if needed
                        background.setFill(Color.WHITE);
                        background.setStroke(Color.GRAY);

                        // Create label for lecture name
                        Label lectureLabel = new Label(moduleName);
                        lectureLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        lectureLabel.setTextFill(Color.BLACK);

                        // Add background and text to the stack
                        lectureBox.getChildren().addAll(background, lectureLabel);

                        // Add stack to grid
                        scheduleGrid.add(lectureBox, col, row);
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
}
