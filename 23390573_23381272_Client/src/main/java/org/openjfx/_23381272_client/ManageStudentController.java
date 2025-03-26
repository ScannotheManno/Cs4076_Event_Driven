package org.openjfx._23381272_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ManageStudentController {

    @FXML private ImageView logo;
    @FXML private ComboBox<String> actionComboBox;
    @FXML private ComboBox<String> removeUserComboBox;
    @FXML private TextField addUserField;
    @FXML private TextArea studentsListArea;
    @FXML private HBox addUserBox;
    @FXML private HBox removeUserBox;

    private ClientModel model;

    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        loadLogo();
        actionComboBox.setOnAction(e -> handleActionSelection());
    }

    private void loadLogo() {
        try {
            Image logoImage = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
            logo.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }

    private void handleActionSelection() {
        String action = actionComboBox.getValue();
        hideAll();

        if ("Add Student".equals(action)) {
            addUserBox.setVisible(true);
        } else if ("Remove Student".equals(action)) {
            removeUserBox.setVisible(true);
            populateUserDropdown();
        } else if ("View Students".equals(action)) {
            studentsListArea.setVisible(true);
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
            String response = model.sendMessage("GET_USERS");
            Platform.runLater(() -> {
                removeUserComboBox.getItems().clear();
                if (response == null || response.equals("NO_USERS_AVAILABLE")) {
                    showAlert("Info", "No students available.");
                } else {
                    removeUserComboBox.getItems().addAll(response.split(";"));
                }
            });
        }).start();
    }

    private void fetchAndDisplayStudents() {
        new Thread(() -> {
            String response = model.sendMessage("GET_USERS");
            Platform.runLater(() -> {
                if (response == null || response.equals("NO_USERS_AVAILABLE")) {
                    studentsListArea.setText("No students found.");
                } else {
                    studentsListArea.setText(response.replace(";", "\n"));
                }
            });
        }).start();
    }

    @FXML
    private void handleAddUser() {
        String username = addUserField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Error", "Please enter a username.");
            return;
        }

        new Thread(() -> {
            String response = model.sendMessage("ADD_USER:" + username);
            Platform.runLater(() -> {
                showAlert("Server Response", response);
                addUserField.clear();
            });
        }).start();
    }

    @FXML
    private void handleRemoveUser() {
        String selectedUser = removeUserComboBox.getValue();
        if (selectedUser == null || selectedUser.isEmpty()) {
            showAlert("Error", "Please select a user to remove.");
            return;
        }

        new Thread(() -> {
            String response = model.sendMessage("REMOVE_USER:" + selectedUser);
            Platform.runLater(() -> {
                showAlert("Server Response", response);
                populateUserDropdown(); // Refresh after removal
            });
        }).start();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) actionComboBox.getScene().getWindow();
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
