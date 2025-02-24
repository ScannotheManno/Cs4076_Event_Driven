/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.openjfx.server_23390573_23381272;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server_23390573_23381272 {
    private static ServerSocket servSock;
    private static final int PORT = 5555;
    private static int clientConnections = 0;

    public static void main(String[] args) {
        System.out.println("Opening port...\n");

        try {
            servSock = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Unable to attach to port");
            System.exit(1);
        }

        // Continuously accept new client connections
        while (true) {
            try {
                Socket link = servSock.accept();
                clientConnections++;
                System.out.println("Client Connected (" + clientConnections + ")");
                new Thread(() -> handleClient(link)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleClient(Socket link) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            String message;

            // Loop to continuously read messages from the client
            while ((message = in.readLine()) != null) {
                System.out.println("Client: " + message);

                if ("ADD_LECTURE".equals(message)) {
                    System.out.println("Opening lecture entry page...\n");
                    out.println("OPEN_ADD_LECTURE_PAGE");
                } else if (message.equals("REMOVE_LECTURE")) {
                    System.out.println("Opening lecture removal page...\n");
                    out.println("OPEN_REMOVE_LECTURE_PAGE");
                } else if (message.equals("VIEW_SCHEDULE")) {
                    System.out.println("Opening Schedule page...\n");
                    out.println("OPEN_VIEW_SCHEDULE_PAGE");
                } else if (message.equals("OTHER")) {
                    System.out.println("Opening other page...\n");
                    out.println("OPEN_OTHER_PAGE");
                } else if ("QUIT".equals(message)) {
                    System.out.println("Closing Connection...\n");
                    out.println("GOODBYE");
                } else {
                    out.println("UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                link.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect");
            }
        }
    }
}
