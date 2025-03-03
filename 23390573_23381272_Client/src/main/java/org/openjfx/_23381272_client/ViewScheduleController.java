package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ViewScheduleController {
    @FXML private GridPane scheduleGrid;

    private static final String[] timeSlots = {"9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM",
                                               "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM"};
    private static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    @FXML
    public void initialize() {
        setupScheduleGrid();
    }

    private void setupScheduleGrid() {
        for (int i = 0; i < timeSlots.length; i++) {
            Label timeLabel = new Label(timeSlots[i]);
            timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            scheduleGrid.add(timeLabel, 0, i + 1);
        }

        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            dayLabel.setTextFill(Color.BLUE);
            scheduleGrid.add(dayLabel, i + 1, 0);
        }
    }

    public void populateSchedule(String scheduleData) {
        String[] lectures = scheduleData.split(";");
        
        for (String lecture : lectures) {
            String[] details = lecture.split("\\|");
            if (details.length == 4) {
                String moduleName = details[0];
                String day = details[1];
                String startTime = details[2];

                int col = getColumnForDay(day);
                int row = getRowForTime(startTime);

                if (col != -1 && row != -1) {
                    Label lectureLabel = new Label(moduleName);
                    lectureLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                    scheduleGrid.add(lectureLabel, col, row);
                }
            }
        }
    }

    private int getColumnForDay(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                return i + 1;
            }
        }
        return -1;
    }

    private int getRowForTime(String time) {
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equalsIgnoreCase(time)) {
                return i + 1;
            }
        }
        return -1;
    }
}
