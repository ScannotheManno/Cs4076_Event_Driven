/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.client_23390573_23381272;

import javafx.application.Platform;
import java.io.IOException;

/**
 *
 * @author lukes
 */

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
}
