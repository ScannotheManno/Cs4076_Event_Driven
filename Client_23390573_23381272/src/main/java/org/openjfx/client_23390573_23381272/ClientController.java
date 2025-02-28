/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.client_23390573_23381272;

/**
 * 
 * @author Luke
 */

import javafx.application.Platform;
import java.io.IOException;
import javafx.scene.control.Alert;

public class ClientController {
    private ClientModel model;
    private ClientView view;
    
    public ClientController(ClientModel model, ClientView view) {
        this.model = model;
        this.view = view;
        attachEventHandlers();
    }
    
    private void attachEventHandlers() {
        // When buttons are clicked, call the corresponding handler methods.
        view.getAddLectureButton().setOnAction(e -> handleAddLecture());
        view.getRemoveLectureButton().setOnAction(e -> handleRemoveLecture());
        view.getViewScheduleButton().setOnAction(e -> handleViewSchedule());
        view.getotherButton().setOnAction(e -> handleOther());
        view.getQuitButton().setOnAction(e -> {
            handleQuit();
            Platform.exit();
        });
    }
    
    public void handleAddLecture() {
        new Thread(() -> {
            try {
                String response = model.sendMessage("ADD_LECTURE");
                if ("OPEN_ADD_LECTURE_PAGE".equals(response)) {
                    Platform.runLater(() -> view.showAddLectureForm());
                } else {
                    System.out.println("Unexpected response: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void handleRemoveLecture() {
        new Thread(() -> {
            try {
                String response = model.sendMessage("REMOVE_LECTURE");
                if ("OPEN_REMOVE_LECTURE_PAGE".equals(response)) {
                    Platform.runLater(() -> view.showRemoveLectureForm());
                } else {
                    System.out.println("Unexpected response: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void handleViewSchedule() {
        new Thread(() -> {
            try {
                String response = model.sendMessage("VIEW_SCHEDULE");
                if ("OPEN_VIEW_SCHEDULE_PAGE".equals(response)) {
                    Platform.runLater(() -> view.showSchedule());
                } else {
                    System.out.println("Unexpected response: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void handleOther() {
        new Thread(() -> {
            try {
                String response = model.sendMessage("OTHER");
                if ("OPEN_OTHER_PAGE".equals(response)) {
                    Platform.runLater(() -> view.otherButton());
                } else {
                    System.out.println("Unexpected response: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void handleQuit() {
        new Thread(() -> {
            try {
                String response = model.sendMessage("QUIT");
                if ("GOODBYE".equals(response)) {
                    model.closeConnection();
                } else {
                    System.out.println("Unexpected response: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
    }
    
    public void handleOtherRequest(String request) {
        new Thread(()-> {
            try {
                String response = " ";
                if (request.equals("ADD_LECTURE")) {
                    handleAddLecture();
                } else if (request.equals("REMOVE_LECTURE")) {
                    handleRemoveLecture();
                } else if (request.equals("VIEW_SCHEDULE")) {
                    handleViewSchedule();
                } else {
                    response = model.sendMessage(request);
                     if (response.startsWith("ERROR:")) {
                        String errorMessage = response.substring(7).trim(); // Remove "ERROR:" prefix
                        Platform.runLater(() -> showAlert("Invalid Action", errorMessage));
                    } 
                }
                
                
            } catch(IOException e) {
                System.err.println(e);
            }
        }).start();
    }
    
    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}
