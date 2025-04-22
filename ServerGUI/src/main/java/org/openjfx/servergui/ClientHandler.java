package org.openjfx.servergui;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String message = in.readLine();
                if (message == null) break;

                System.out.println("Received from client: " + message);
                ServerTCP.processClientMessage(message, in, out);

                if (message.equals("QUIT")) break;
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Client connection closed.\n");
            } catch (IOException e) {
                System.out.println("Unable to close connection.");
            }
        }
    }
}