package org.openjfx._23381272_client;

import java.io.*;
import java.net.*;
import javafx.scene.control.Alert;

public class ClientModel {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientModel() {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            int serverPort = 5555;

            System.out.println("Attempting to connect to server...\n");
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connection Established\n");

        } catch (IOException e) {
            showAlert("Error", "Failed to connect to server. Check that server is online.");
            System.err.println("Failed to connect to server. Check that server is online.");
            System.exit(0);
        }
    }

    public String sendMessage(String message) throws IOException {
        if (socket == null || socket.isClosed()) {
            return "Not connected to server.";
        }

        out.println(message);
        String response = in.readLine();
        return response;
    }

    public void closeConnection() throws IOException {
        if (socket != null && !socket.isClosed()) {
             socket.close();
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
