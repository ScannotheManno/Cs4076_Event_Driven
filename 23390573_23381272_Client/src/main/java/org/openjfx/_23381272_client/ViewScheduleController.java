package org.openjfx._23381272_client;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
            
    public void setModel(ClientModel model) {
        this.model = model;
    }

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
        try {
            String response = model.sendMessage("BACK");
            if (response.equals("RETURNING")) {
                closeWindow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        
                        System.out.println("📌 Placing: " + moduleName + " at Column: " + col + ", Row: " + row);

                        if (col != -1 && row != -1) {
                            StackPane lectureBox = new StackPane();

                            Rectangle background;
                            if (duration.equals("1")) {
                                background = new Rectangle(120, 55);
                                background.setFill(Color.WHITE);
                                background.setStroke(Color.BLACK);
                            } else {
                                background = new Rectangle(120, 110);
                                background.setFill(Color.WHITE);
                                background.setStroke(Color.BLACK);
                            }
                            
                            Label timeLabel = new Label(startTime);
                            timeLabel.setFont(Font.font("Arial", 9));
                            timeLabel.setTextFill(Color.BLACK);
                            
                            Label nameLabel = new Label(moduleName);
                            nameLabel.setFont(Font.font("Arial", 9));
                            nameLabel.setTextFill(Color.BLACK);
                            
                            Label idLabel = new Label(moduleID + "_" + type);
                            idLabel.setFont(Font.font("Arial", 9));
                            idLabel.setTextFill(Color.BLACK);
                            
                            Label roomLabel = new Label(room);
                            roomLabel.setFont(Font.font("Arial", 9));
                            roomLabel.setTextFill(Color.BLACK);
                            
                            VBox lectureVBox = new VBox();
                            lectureVBox.setAlignment(Pos.CENTER);
                            lectureVBox.getChildren().addAll(timeLabel,idLabel, nameLabel, roomLabel);
                            lectureBox.getChildren().addAll(background, lectureVBox);

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

                Rectangle background = new Rectangle(120, 30);
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

                    Rectangle blankBackground = new Rectangle(120, 55);
                    blankBackground.setFill(Color.WHITE);
                    blankBackground.setStroke(Color.BLACK);

                    blankBox.getChildren().add(blankBackground);

                    scheduleGrid.add(blankBox, col + 1, row + 1);
                }
            }
        });
    }
    
     private void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }
}