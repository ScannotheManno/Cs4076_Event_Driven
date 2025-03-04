package org.openjfx.server_23390573_23381272;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server_23390573_23381272 {
    private static ServerSocket servSock;
    private static final int PORT = 5555; // Change this if the port is in use
    private static int clientConnections = 0;
    private static final Map<String, String> lectureStorage = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Opening port...\n");

        // Check if the port is available
        if (!isPortAvailable(PORT)) {
            System.out.println("Port " + PORT + " is already in use. Please free the port or use a different one.");
            System.exit(1);
        }

        try {
            servSock = new ServerSocket(PORT);

            // Add a shutdown hook to close the server socket on exit
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
            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Client: " + message);

                try {
                    switch (message) {
                        case "ADD_LECTURE":
                            System.out.println("Opening add lecture page...\n");
                            out.println("OPEN_ADD_LECTURE_PAGE");
                            handleAddLecture(in, out);
                            break;
                        case "REMOVE_LECTURE":
                            System.out.println("Opening remove lecture page...\n");
                            out.println("OPEN_REMOVE_LECTURE_PAGE");
                            handleRemoveLecture(in, out);
                            break;
                        case "VIEW_SCHEDULE":
                            out.println("OPEN_SCHEDULE_PAGE");
                            handleViewSchedule(out);
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

    private static void handleAddLecture(BufferedReader in, PrintWriter out) {
        try {
            String response = in.readLine();
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

                System.out.println("New Lecture Added:");
                System.out.println("Lecture Name: " + moduleName);
                System.out.println("Course Name: " + moduleID);
                System.out.println("Room: " + room);
                System.out.println("Type: " + type);
                System.out.println("Day: " + day);
                System.out.println("Start Time: " + startTime);
                System.out.println("End Time: " + endTime);

                out.println("Lecture Added Successfully!");
            } else {
                out.println("ERROR: Invalid ADD_LECTURE format. Expected format: LectureName, CourseID, Room, Type, Day, StartTime, EndTime");
            }
        } catch (IOException e) {
            System.out.println("Unable to read message");
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
            out.println("No lectures scheduled.");
        } else {
            for (Map.Entry<String, String> entry : lectureStorage.entrySet()) {
                out.println(entry.getValue());
            }
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