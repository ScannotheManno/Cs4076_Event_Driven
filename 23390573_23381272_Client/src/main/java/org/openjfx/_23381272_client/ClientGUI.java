package org.openjfx._23381272_client;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("org/openjfx/_23381272_client/ClientView.fxml"));
            Parent root = loader.load();
            
            ClientController controller = loader.getController();
            ClientModel model = new ClientModel();
            controller.setModel(model);
            
            primaryStage.setTitle("Lecture Management System");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void ClientGUI(String[] args) {
        launch(args);
    }
}