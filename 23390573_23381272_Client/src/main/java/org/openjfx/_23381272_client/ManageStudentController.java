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
    @FXML private TextArea studentsListArea;
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

    @FXML
    private void handleActionSelection() {
        String action = studentManagementComboBox.getValue();
        hideAll();

        if ("Add Student".equals(action)) {
            addUserBox.setVisible(true);
            closeButton.setVisible(false);
        } else if ("Remove Student".equals(action)) {
            removeUserBox.setVisible(true);
            closeButton.setVisible(false);
            populateUserDropdown();
        } else if ("List Of Students".equals(action)) {
            studentsListArea.setVisible(true);
            closeButton.setVisible(false);
            fetchAndDisplayStudents();
        }
    }

    private void hideAll() {
        addUserBox.setVisible(false);
        removeUserBox.setVisible(false);
        studentsListArea.setVisible(false);
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
                if (response == null || response.equals("NO_STUDENTS_AVAILABLE")) {
                    studentsListArea.setText("No students found.");
                } else {
                    studentsListArea.setText(response.replace(":", "\n"));
                }
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
