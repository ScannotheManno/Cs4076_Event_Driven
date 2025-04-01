package org.openjfx._23381272_client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AdminView {
    private ClientModel model;
    private AdminController adminController;

    public AdminView(ClientModel model, AdminController adminController) {
        this.model = model;
        this.adminController = adminController;
    }

    public void openManageStudentPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/_23381272_client/ManageStudentView.fxml"));
            Parent root = loader.load();

            ManageStudentController controller = loader.getController();
            controller.setModel(model);

            Stage stage = new Stage();
            stage.setTitle("Manage Students");
            stage.setScene(new Scene(root, 375, 400));
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to open Manage Users page: " + e.getMessage());
            e.printStackTrace(); // Optional but recommended for detailed errors
        }
    }

    
    public void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();

            ClientModel model = new ClientModel();
            LoginController loginController = loader.getController();
            loginController.setModel(model);

            Stage primaryStage = new Stage();
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root, 420, 600));
            primaryStage.setResizable(false);
            primaryStage.show();
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
