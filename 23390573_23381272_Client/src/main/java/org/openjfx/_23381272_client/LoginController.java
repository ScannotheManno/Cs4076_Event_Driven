package org.openjfx._23381272_client;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ImageView logo;

    private ClientModel model;

    public void setModel(ClientModel model) {
        this.model = model;
    }
    
    @FXML
    public void initialize() {
        // Loads logo into UI
        Image image = new Image(getClass().getResource("/Images/ul_logo.jpg").toExternalForm());
        logo.setImage(image);
    }
    
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

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Enter both username and password.");
            return;
        }

        // Communicate with server
        new Thread(() -> {
            String response = model.sendMessage("LOGIN");
            if (response.equals("SEND_USER_DETAILS")) {  // Server ready to receive credentials
                String authResponse = model.sendMessage(username + ":" + password);

                if (authResponse.equals("LOGIN_SUCCESS_STUDENT")) {
                    Platform.runLater(() -> openMainMenuStudent());
                } else if (authResponse.equals("LOGIN_SUCCESS_ADMIN")) {
                    Platform.runLater(() -> openMainMenuAdmin());
                } else {
                    Platform.runLater(() -> showAlert("Login Failed", "Incorrect username/password."));
                }
            } else {
                Platform.runLater(() -> showAlert("Login Failed", "Unexpected server response."));
            }
        }).start();
    }
    
    @FXML
    private void handleQuit() {
        String message = model.sendMessage("QUIT");
        if (message.equals("GOODBYE")) {
            try {
                model.closeConnection();
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Unable to Disconnect");
            }
        }
    }

    private void openMainMenuStudent() {
        try {
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentMainMenuView.fxml"));
            Parent root = loader.load();

            ClientController controller = loader.getController();
            controller.setModel(model);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Student Lecture Management System");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load main menu: " + e.getMessage());
        }
    }
    
    private void openMainMenuAdmin() {
        try {
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMainMenuView.fxml"));
            Parent root = loader.load();

            AdminController controller = loader.getController();
            controller.setModel(model);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Admin Lecture Management System");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load main menu: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
