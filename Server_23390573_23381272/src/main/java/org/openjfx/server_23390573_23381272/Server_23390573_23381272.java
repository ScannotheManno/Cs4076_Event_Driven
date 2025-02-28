/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.openjfx.server_23390573_23381272;

/**
 * 
 * @author Luke
 */
import java.io.*;
import java.net.*;

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

            while ((message = in.readLine()) != null) {
                System.out.println("Client: " + message);

                try {
                    switch (message) {
                        case "ADD_LECTURE":
                            System.out.println("Opening add lecture page...\n");
                            out.println("OPEN_ADD_LECTURE_PAGE");
                            break;
                        case "REMOVE_LECTURE":
                            System.out.println("opening remove lecture page...\n");
                            out.println("OPEN_REMOVE_LECTURE_PAGE");
                            break;
                        case "VIEW_SCHEDULE":
                            out.println("OPEN_VIEW_SCHEDULE_PAGE");
                            break;
                        case "OTHER":
                            System.out.println("Opening other page...\n");
                            out.println("OPEN_OTHER_PAGE");
                            break;
                        case "QUIT":
                            System.out.println("Closing connection...\n");
                            out.println("GOODBYE");
                            break;
                        default:
                            
                            throw new IncorrectActionException("Invalid request received: " + message + ". The server does not support this request.");
                    }
                } catch (IncorrectActionException e) {
                    System.out.println("Error: " + e.getMessage());
                    out.println("ERROR: " + e.getMessage());
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
