package org.openjfx.servergui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableController {
    @FXML private GridPane scheduleGrid;
    @FXML private ImageView logo;
    @FXML private Button backButton;
    @FXML private ComboBox<String> userDropdown;

    private LectureController lectureCon;
    private boolean showGroup = true;
    private String selectedUser;

    public void setLectureController(LectureController lectureCon) {
        this.lectureCon = lectureCon;
    }

    public void setUsers(List<String> allUsers) {
        Platform.runLater(() -> {
            userDropdown.getItems().setAll(allUsers);
            userDropdown.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && newV.equalsIgnoreCase("Admin")) {
                    showGroup = true;
                    selectedUser = null;
                } else {
                    showGroup = false;
                    selectedUser = newV;
                }
                loadSchedule();
            });

            if (!allUsers.isEmpty()) {
                String first = allUsers.get(0);
                userDropdown.getSelectionModel().select(first);
                if (first.equalsIgnoreCase("admin")) {
                    showGroup = true;
                    selectedUser = null;
                } else {
                    showGroup = false;
                    selectedUser = first;
                }
                loadSchedule();
            }
        });
    }

    @FXML
    public void initialize() {
        // Load logo once UI is ready
        Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
        logo.setImage(image);
    }

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        ((VBox) event.getSource()).setOpacity(0.7);
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        ((VBox) event.getSource()).setOpacity(1.0);
    }

    @FXML
    private void handleBackButton() {
        closeWindow();
    }

    private static final String[] timeSlots = {
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00", "18:00"
    };
    private static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /**
     * Builds a semicolon-separated scheduleData string and repopulates the grid.
     */
    private void loadSchedule() {
        Map<String, String> sourceMap;
        if (showGroup) {
            sourceMap = lectureCon.getLectureStorageGroup();
        } else if (selectedUser != null) {
            sourceMap = new HashMap<>();
            String prefix = selectedUser + "_";
            for (Map.Entry<String, String> entry : lectureCon.getLectureStoragePersonal().entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    String key = entry.getKey().substring(prefix.length());
                    String data = entry.getValue().substring(prefix.length());
                    sourceMap.put(key, data);
                }
            }
        } else {
            sourceMap = new HashMap<>();
        }

        String scheduleData = sourceMap.isEmpty()
            ? "NO_LECTURES_SCHEDULED"
            : String.join(";", sourceMap.values());

        populateSchedule(scheduleData);
    }

    public void populateSchedule(String scheduleData) {
        createTimetable();
        if ("NO_LECTURES_SCHEDULED".equals(scheduleData)) return;

        Platform.runLater(() -> {
            for (String lecture : scheduleData.split(";")) {
                try {
                    String[] d = lecture.split("@");
                    if (d.length != 7) continue;
                    String moduleName = d[0], moduleID = d[1], room = d[2],
                           type = d[3], day = d[4], startTime = d[5];
                    int duration = Integer.parseInt(d[6]);
                    int col = getColumnForDay(day), row = getRowForTime(startTime);
                    if (col < 0 || row < 0) continue;

                    StackPane box = new StackPane();
                    Rectangle bg = new Rectangle(120, duration == 2 ? 110 : 55);
                    bg.setFill(Color.WHITE);
                    bg.setStroke(Color.BLACK);

                    VBox content = new VBox(2,
                        new Label(startTime),
                        new Label(moduleID + "_" + type),
                        new Label(moduleName),
                        new Label(room)
                    );
                    content.setAlignment(Pos.CENTER);
                    content.getChildren().forEach(n -> ((Label)n).setFont(Font.font("Arial", 9)));

                    box.getChildren().addAll(bg, content);
                    for (int i = 0; i < duration; i++) {
                        int r = row + 1 + i, c = col + 1;
                        scheduleGrid.getChildren().removeIf(n ->
                            GridPane.getRowIndex(n) == r && GridPane.getColumnIndex(n) == c
                        );
                    }
                    scheduleGrid.add(box, col + 1, row + 1, 1, duration);
                } catch (Exception ignored) {}
            }
        });
    }

    private int getColumnForDay(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) return i;
        }
        return -1;
    }

    private int getRowForTime(String time) {
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equalsIgnoreCase(time)) return i;
        }
        return -1;
    }

    private void createTimetable() {
        Platform.runLater(() -> {
            scheduleGrid.getChildren().clear();
            for (int col = 0; col < days.length; col++) {
                StackPane header = new StackPane();
                Rectangle bg = new Rectangle(120, 30);
                bg.setFill(Color.GREEN);
                bg.setStroke(Color.BLACK);
                Label lbl = new Label(days[col]);
                lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                lbl.setTextFill(Color.WHITE);
                header.getChildren().addAll(bg, lbl);
                scheduleGrid.add(header, col + 1, 0);
            }
            for (int col = 0; col < days.length; col++) {
                for (int row = 0; row < timeSlots.length; row++) {
                    StackPane cell = new StackPane();
                    Rectangle bg = new Rectangle(120, 55);
                    bg.setFill(Color.WHITE);
                    bg.setStroke(Color.BLACK);
                    cell.getChildren().add(bg);
                    scheduleGrid.add(cell, col + 1, row + 1);
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
