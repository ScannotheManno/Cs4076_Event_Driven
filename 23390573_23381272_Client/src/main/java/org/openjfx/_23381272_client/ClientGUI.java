package org.openjfx._23381272_client;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    // loads Main Menu
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();

            ClientModel model = new ClientModel();
            LoginController loginController = loader.getController();
            loginController.setModel(model);

            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root, 420, 600));
            primaryStage.setResizable(false);
            primaryStage.show();
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}