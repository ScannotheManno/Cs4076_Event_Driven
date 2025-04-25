package org.openjfx.servergui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/openjfx/ServerGUI/ServerGUI.fxml"));
            Scene scene = new Scene(root,800,800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Timetable Server");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}