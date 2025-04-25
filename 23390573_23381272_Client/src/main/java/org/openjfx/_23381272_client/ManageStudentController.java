package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageStudentController {

    @FXML private ImageView logo;
    @FXML private ComboBox<String> studentManagementComboBox;
    @FXML private VBox addUserBox;
    @FXML private TextField addUserField;
    @FXML private TextField addPasswordField;
    @FXML private VBox removeUserBox;
    @FXML private ComboBox<String> removeUserComboBox;
    @FXML private ListView studentsListView;
    @FXML private Button closeButton;

    private ClientModel model;

    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm()));        
        studentManagementComboBox.getItems().addAll("Add Student", "Remove Student", "List Of Students");
    }

    private void hideAll() {
        addUserBox.setVisible(false);
        addUserBox.setManaged(false);

        removeUserBox.setVisible(false);
        removeUserBox.setManaged(false);

        studentsListView.setVisible(false);
        studentsListView.setManaged(false);
    }

    @FXML
    private void handleActionSelection() {
        hideAll();

        String action = studentManagementComboBox.getValue();
        
        if (action.equals("Add Student")) {
            addUserBox.setVisible(true);
            addUserBox.toFront();
            closeButton.setVisible(false);
        } else if (action.equals("Remove Student")) {
            removeUserBox.setVisible(true);
            removeUserBox.toFront();
            closeButton.setVisible(false);
            populateUserDropdown();
        } else if (action.equals("List Of Students")) {
            studentsListView.setVisible(true);
            studentsListView.toFront();
            closeButton.setVisible(true);
            fetchAndDisplayStudents();
        }
    }

    private void populateUserDropdown() {
        new Thread(() -> {
            String response = model.sendMessage("GET_STUDENTS");
            Platform.runLater(() -> {
                removeUserComboBox.getItems().clear();
                if (response == null || response.equals("NO_STUDENTS_AVAILABLE")) {
                    showAlert("Info", "No students available.");
                } else {
                    removeUserComboBox.getItems().addAll(response.split(":"));
                }
            });
        }).start();
    }

    private void fetchAndDisplayStudents() {
        new Thread(() -> {
            String response = model.sendMessage("GET_STUDENTS");
            Platform.runLater(() -> {
                 studentsListView.getItems().clear();
                if (response == null || response.equals("NO_STUDENTS_AVAILABLE")) {
                    // optional: show placeholder item
                    studentsListView.getItems().add("No students found.");
                } else {
                    String[] students = response.split(":");
                    studentsListView.getItems().addAll(students);
                }
                
                 int count = studentsListView.getItems().size();
                double cellHeight = studentsListView.getFixedCellSize();
                studentsListView.setPrefHeight(count * cellHeight + 2);
                studentsListView.setVisible(true);
            });
        }).start();
    }

    @FXML
    private void handleAddUser() {
        String response = model.sendMessage("ADD_STUDENT");
        String username = addUserField.getText().trim();
        String password = addPasswordField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Error", "Please enter a username.");
            return;
        }

        new Thread(() -> {
            if (response.equals("SEND_STUDENT_DATA")) {
                String request = model.sendMessage(username + ";" + password + ";Student");
                Platform.runLater(() -> {
                    showAlert("Server Response", request);
                    });
            }
        }).start();
        handleClose();
    }
    

    @FXML
    private void handleRemoveUser() {
        String response = model.sendMessage("REMOVE_STUDENT");
        String selectedUser = removeUserComboBox.getValue();
        if (selectedUser == null || selectedUser.isEmpty()) {
            showAlert("Error", "Please select a user to remove.");
            return;
        }

        new Thread(() -> {
            if (response.equals("REMOVING_STUDENT")) {
                String message = model.sendMessage(selectedUser);
                Platform.runLater(() -> { showAlert("Server Response", message);
                });
            }
        }).start();
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) studentManagementComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
