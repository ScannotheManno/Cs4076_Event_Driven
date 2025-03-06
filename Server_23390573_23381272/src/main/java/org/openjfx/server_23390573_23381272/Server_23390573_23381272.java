package org.openjfx.server_23390573_23381272;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server_23390573_23381272 {
    private static ServerSocket servSock;
    private static final int PORT = 5555;
    private static int clientConnections = 0;
    private static final Map<String, String> lectureStorage = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Opening port...\n");

        if (!isPortAvailable(PORT)) {
            System.out.println("Port " + PORT + " is already in use. Please free the port or use a different one.");
            System.exit(1);
        }

        try {
            servSock = new ServerSocket(PORT);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (servSock != null && !servSock.isClosed()) {
                        servSock.close();
                        System.out.println("Server socket closed.");
                    }
                } catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }));

            while (true) {
                try {
                    Socket link = servSock.accept();
                    clientConnections++;
                    System.out.println("Client Connected (" + clientConnections + ")");
                    new Thread(() -> handleClient(link)).start();
                } catch (IOException e) {
                    System.out.println("Unable to connect to client.");
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to attach to port: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void handleClient(Socket link) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);

            while (true) {
                String message = in.readLine();
                if (message == null) break;

                System.out.println("Received from client: " + message);

                try {
                    switch (message) {
                        case "ADD_LECTURE":
                            System.out.println("Opening Add Lecture page...\n");
                            out.println("OPEN_ADD_LECTURE_PAGE");
                            
                            break;

                        case "SUBMIT_LECTURE":
                            System.out.println("Waiting for data...\n");
                            out.println("SEND_DATA");
                            String lectureData = in.readLine();
                            handleAddLecture(lectureData, out);
                            break;
                            
                        case "REMOVE_LECTURE":
                            System.out.println("Opening Remove Lecture page...\n");
                            out.println("OPEN_REMOVE_LECTURE_PAGE");
                            break;

                        case "VIEW_SCHEDULE":
                            System.out.println("Opening Schedule...\n");
                            out.println("OPEN_VIEW_SCHEDULE_PAGE");
                            break;
                            
                        case "SEND_LECTURE_DETAILS":
                            System.out.println("Sending lecture data...\n");
                            handleViewSchedule(out);
                            break;

                        case "OTHER":
                            System.out.println("Opening Other Services...");
                            out.println("OPEN_OTHER_PAGE");
                            break;

                        case "QUIT":
                            System.out.println("Client disconnected...");
                            out.println("GOODBYE");
                            return;
                        default:
                            throw new IncorrectActionException("Invalid request received: " + message);
                    }
                } catch (IncorrectActionException e) {
                    System.out.println("Error: " + e.getMessage());
                    out.println("ERROR: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            try {
                link.close();
                System.out.println("Client connection closed.");
            } catch (IOException e) {
                System.out.println("Unable to close connection.");
            }
        }
    }

    private static void handleAddLecture(String lectureData, PrintWriter out) {
        
        String response = lectureData;
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Empty message received. Ignoring.");
            return;
        }

        String[] module = response.split(",");
        if (module.length == 7) {
            String moduleName = module[0];
            String moduleID = module[1];
            String room = module[2];
            String type = module[3];
            String day = module[4];
            String startTime = module[5];
            String endTime = module[6];

            String lectureKey = moduleName + "_" + room + "_" + type + "_" + day + "_" + startTime;
            String lectureDetails = moduleName + "," + moduleID + "," + room + "," + type + "," + day + "," + startTime + "," + endTime;

            lectureStorage.put(lectureKey, lectureDetails);

            System.out.println("New Lecture Added: " + lectureDetails);
            out.println("Lecture Added Successfully!");
        } else {

            System.out.println("ERROR: Invalid ADD_LECTURE format. Message: " + response);
            out.println("ERROR: Invalid ADD_LECTURE format. Expected format: LectureName, CourseID, Room, Type, Day, StartTime, EndTime");
        }
    }
        
    


    private static void handleRemoveLecture(BufferedReader in, PrintWriter out) {
        try {
            String lectureKey = in.readLine();
            if (lectureStorage.containsKey(lectureKey)) {
                lectureStorage.remove(lectureKey);
                out.println("Lecture Removed Successfully!");
            } else {
                out.println("ERROR: Lecture not found.");
            }
        } catch (IOException e) {
            System.out.println("Unable to read message");
        }
    }

    private static void handleViewSchedule(PrintWriter out) {
        if (lectureStorage.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
        } else {
            StringBuilder scheduleData = new StringBuilder();
            for (Map.Entry<String, String> entry : lectureStorage.entrySet()) {
                scheduleData.append(entry.getValue()).append(";"); // Separate lectures by ;
            }
            out.println(scheduleData.toString());
        }
}


    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}