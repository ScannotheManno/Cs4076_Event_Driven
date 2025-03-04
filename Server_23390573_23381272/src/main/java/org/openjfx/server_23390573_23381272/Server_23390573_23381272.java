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
                System.out.println("Unable to connect to client.");
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
}