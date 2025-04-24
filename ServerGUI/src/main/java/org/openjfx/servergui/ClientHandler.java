package org.openjfx.servergui;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> logger;

    public ClientHandler(Socket socket, Consumer<String> logger) {
        this.socket = socket;
        this.logger = logger;
    }

    private void log(String message) {
        if (logger != null) {
            logger.accept(message);
        }
        System.out.println(message);
    }

    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress();
        log("Client connected from: " + clientAddress);

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String message = in.readLine();
                if (message == null) {
                    log("Client " + clientAddress + " disconnected unexpectedly");
                    break;
                }
                String currentUser = Thread.currentThread().getName();
                log("Received from " + currentUser + ": " + message);
                ServerTCP.processClientMessage(message, in, out);

                if (message.equals("QUIT")) {
                    log("Client " + currentUser + " requested disconnect");
                    break;
                }
            }

        } catch (IOException e) {
            log("Connection error with " + clientAddress + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
                log("Connection closed with " + clientAddress);
            } catch (IOException e) {
                log("Error closing connection with " + clientAddress + ": " + e.getMessage());
            }
        }
    }
}