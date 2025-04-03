package org.openjfx._23381272_client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;


public class TimeTablePopUpController {
    
    private ClientModel model;
    private ClientController controller;
    private ClientView view;
    
    @FXML private Button groupTimeTable;
    @FXML private Button personalTimeTable;

    // Setter Method for ClientModel
    public void setModel(ClientModel model) {
        this.model = model;
    }
    
    // Setter Method for ClientController
    public void setController(ClientController controller) {
        this.controller = controller;
    }
    
    public void setView(ClientView view) {
        this.view = view;
    }
    
    @FXML
    private void handlePersonalTimetable(){
        String response = model.sendMessage("PERSONAL_TIMETABLE");
        if (response.equals("OPEN_PERSONAL_TIMETABLE_PAGE")) {
            view.openPersonalTimetablePage();
        }
    }
    
    @FXML
    private void handleGroupTimetable(){
        String response = model.sendMessage("GROUP_TIMETABLE");
        if (response.equals("OPEN_GROUP_TIMETABLE_PAGE")) {
            view.openGroupTimetablePage();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
