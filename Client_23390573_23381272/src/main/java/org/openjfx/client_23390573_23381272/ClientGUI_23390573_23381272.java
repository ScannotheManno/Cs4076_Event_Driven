/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.openjfx.client_23390573_23381272;

/**
 * 
 * @author Luke
 */

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.net.*;

public class ClientGUI_23390573_23381272 extends Application {
    private ClientModel model;
    private ClientView view;
    private ClientController controller;
    
    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        model = new ClientModel(InetAddress.getLocalHost(), 5555);
        view = new ClientView(primaryStage);
        controller = new ClientController(model, view);
        
        view.setController(controller);

        new Thread(() -> {
            boolean success = model.initialise();
            if (!success) {
                System.out.println("Failed to connect to server.");
                Platform.exit();
            }
        }).start();
        
        
        controller = new ClientController(model, view);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
