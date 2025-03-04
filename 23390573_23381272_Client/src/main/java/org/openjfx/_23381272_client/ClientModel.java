package org.openjfx._23381272_client;

import java.io.*;
import java.net.*;

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

        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public String sendMessage(String message) throws IOException {
        if (socket == null || socket.isClosed()) {
            return "Not connected to server.";
        }

        System.out.println("Sending message to server: " + message);
        out.println(message);
        String response = in.readLine();
        System.out.println("Server Response: " + response + "\n");
        return response;
    }

    public void closeConnection() throws IOException {
        if (socket != null && !socket.isClosed()) {
             socket.close();
        }   

    }
}
