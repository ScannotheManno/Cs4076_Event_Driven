/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.openjfx.client_23390573_23381272;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * 
 * @author lukes
 */

public class ClientGUI_23390573_23381272 extends Application {
    private ClientModel model;
    private ClientView view;
    private ClientController controller;
    
    @Override
    public void start(Stage primaryStage) {
        // Create the view (builds the UI)
        view = new ClientView(primaryStage);
        
        // Create the model (with server details)
        model = new ClientModel("localhost", 5555);
        
        // Initialize the connection on a separate thread.
        new Thread(() -> {
            boolean success = model.initialise();
            if (!success) {
                System.out.println("Failed to connect to server.");
                Platform.exit();
            }
        }).start();
        
        // Create the controller to wire the model and view.
        controller = new ClientController(model, view);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
