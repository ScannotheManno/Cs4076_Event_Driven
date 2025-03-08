package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ViewScheduleController {
    @FXML private GridPane scheduleGrid;
    @FXML private ImageView logo;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        if (logo == null) {
            System.out.println("Error: ImageView 'logo' is null! Check fx:id in FXML.");
        } else {
            Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
            logo.setImage(image);
            System.out.println("Image Loaded Successfully!");
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private static final String[] timeSlots = {
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00", "18:00"};

    public void populateSchedule(String scheduleData) {
        if (scheduleData == null || scheduleData.trim().isEmpty()) {
            System.out.println("Error: Empty or null schedule data received.");
            createTimetable();
            return;
        }

        if (scheduleData.equals("NO_LECTURES_SCHEDULED")) {
            System.out.println("No lectures scheduled.");
            createTimetable();
            return;
        }

        if (scheduleData.equals("No lectures scheduled.")) {
            System.out.println("No lectures available.");
            createTimetable();
            return;
        }

        createTimetable();
        Platform.runLater(() -> {
            String[] lectures = scheduleData.split(";");
            for (String lecture : lectures) {
                try {
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

                        for (int i = moduleID.length() / 2; i < ((moduleName.length() / 2) + (moduleID.length() / 4)); i++) {
                            moduleID = " " + moduleID;
                        }

                        for (int i = room.length() / 2; i < ((moduleName.length() / 2) + (room.length() / 4)); i++) {
                            room = " " + room;
                        }

                        for (int i = startTime.length() / 2; i < ((moduleName.length() / 2) + (startTime.length() / 4)); i++) {
                            startTime = " " + startTime;
                        }

                        System.out.println("📌 Placing: " + moduleName + " at Column: " + col + ", Row: " + row);

                        if (col != -1 && row != -1) {
                            StackPane lectureBox = new StackPane();

                            Rectangle background;
                            if (duration.equals("1")) {
                                background = new Rectangle(100, 40);
                                background.setFill(Color.WHITE);
                                background.setStroke(Color.BLACK);
                            } else {
                                background = new Rectangle(100, 80);
                                background.setFill(Color.WHITE);
                                background.setStroke(Color.BLACK);
                            }
                            Label lectureLabel = new Label(startTime + "\n" + moduleName + "\n" + moduleID + "-" + type + "\n" + room);
                            lectureLabel.setFont(Font.font("Arial", 10));
                            lectureLabel.setTextFill(Color.BLACK);

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
                            int durationInt = Integer.parseInt(duration);
                            scheduleGrid.add(lectureBox, col + 1, row + 1, 1, durationInt);

                        } else {
                            System.out.println("Invalid Position: " + moduleName + " (" + day + " " + startTime + ")");
                        }
                    } else {
                        System.out.println("Invalid Data Format: " + lecture);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing lecture data: " + e.getMessage());
                }
            }
        });
    }

    private int getColumnForDay(String day) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                return i;
            }
        }
        return -1;
    }

    private int getRowForTime(String time) {
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equalsIgnoreCase(time)) {
                return i;
            }
        }
        return -1;
    }

    private void createTimetable() {
        Platform.runLater(() -> {
            scheduleGrid.getChildren().clear();

            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            for (int col = 0; col < days.length; col++) {
                StackPane headerPane = new StackPane();

                Rectangle background = new Rectangle(100, 30);
                background.setFill(Color.GREEN);
                background.setStroke(Color.BLACK);

                Label dayLabel = new Label(days[col]);
                dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                dayLabel.setTextFill(Color.WHITE);

                headerPane.getChildren().addAll(background, dayLabel);

                scheduleGrid.add(headerPane, col + 1, 0);
            }

            String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00",
                    "14:00", "15:00", "16:00", "17:00", "18:00"};

            for (int col = 0; col < days.length; col++) {
                for (int row = 0; row < timeSlots.length; row++) {
                    StackPane blankBox = new StackPane();

                    Rectangle blankBackground = new Rectangle(100, 40);
                    blankBackground.setFill(Color.WHITE);
                    blankBackground.setStroke(Color.BLACK);

                    blankBox.getChildren().add(blankBackground);

                    scheduleGrid.add(blankBox, col + 1, row + 1);
                }
            }
        });
    }
}