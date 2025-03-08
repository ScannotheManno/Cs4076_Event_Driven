package org.openjfx.server_23390573_23381272;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server_23390573_23381272 {
    private static ServerSocket servSock;
    private static final int PORT = 5555;
    private static int clientConnections = 0;
    private static final Map<String, String> lectureStorage = new HashMap<>();
    private static final ArrayList<String> timetableSpaces = new ArrayList<>();
    private static final ArrayList<String> studentAvailibility = new ArrayList<>();
    public static void main(String[] args) {
        System.out.println("Opening port...\n");

        if (!isPortAvailable(PORT)) {
            System.out.println("Port " + PORT + " is already in use. Please free the port or use a different one.");
            System.exit(1);
        }

        try {
            servSock = new ServerSocket(PORT);

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
            //reads message from client and if non is sent breaks the loop
            while (true) {
                String message = in.readLine();
                if (message == null) break;

                System.out.println("Received from client: " + message);
                
                //finds client message and opens appropriate menu or completes an action
                try {
                    switch (message) {
                        case "ADD_LECTURE":
                            System.out.println("Opening Add Lecture page...\n");
                            out.println("OPEN_ADD_LECTURE_PAGE");
                            
                            break;

                        case "SUBMIT_LECTURE":
                            System.out.println("Waiting for data...\n");
                            out.println("SEND_DATA");
                            String scheduleData = in.readLine();
                            handleAddLecture(scheduleData, out);
                            break;
                            
                        case "REMOVE_LECTURE":
                            System.out.println("Opening Remove Lecture page...\n");
                            out.println("OPEN_REMOVE_LECTURE_PAGE");
                            break;
                            
                        case "SEND_LECTURES":
                            System.out.println("Sending lecture data...\n");
                            sendLectureKeys(out);
                            break;
                            
                        case "REMOVE_THIS_LECTURE":
                            System.out.println("Waiting for data...");
                            out.println("SEND_DATA");
                            String lectureData = in.readLine();
                            handleRemoveLecture(lectureData, out);
                            System.out.println("Removing " + lectureData + "...\n");
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
                            System.out.println("Opening Other Services...\n");
                            out.println("OPEN_OTHER_PAGE");
                            break;

                        case "QUIT":
                            System.out.println("Client disconnected...\n");
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
                System.out.println("Client connection closed.\n");
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
            String duration = module[6];
            
            //checks availablity and if slot/room is free adds the lecture
            String availibility = day + "_" + startTime;
            String fillTimetable = room + "_" + day + "_" + startTime;
            String lectureKey = moduleName + "_" + type + "_" + room + "_" + day + "_" + startTime;
            String lectureDetails = moduleName + "," + moduleID + "," + type + "," + room + "," + day + "," + startTime + "," + duration;
            
            
            
            if (lectureStorage.containsKey(lectureKey)) {
                System.out.println("Error: Lecture already exists");
                out.println("Error: Lecture already exists.");
            } else if (timetableSpaces.contains(fillTimetable)) {
                System.out.println("Error: This room is already in use at this time.");
                out.println("Error: This room is already in use at the time specified.");
            } else if (studentAvailibility.contains(availibility)) {
                System.out.println("Student unavailable at this time.");
                out.println("Error: Student already has a class at this time.");
            } else {
                //if the above is available add lecture
                lectureStorage.put(lectureKey, lectureDetails);
                timetableSpaces.add(fillTimetable);
                studentAvailibility.add(availibility);
                if (duration.equals("2")) {
                    //handles if the lecture is 2 hours long
                    int time = Integer.parseInt(startTime.split(":")[0]);
                    time += 1;
                    String extraTime = String.format("%02d:00", time);
                    String extraSpace = room + "_" + day + "_" + extraTime;
                    String extraAvail = day + "_" + extraTime; 
                    timetableSpaces.add(extraSpace); 
                    studentAvailibility.add(extraAvail);
                }
                System.out.println("New Lecture Added: " + lectureDetails);
                out.println("Lecture Added Successfully!");
            }
        } else {

            System.out.println("ERROR: Invalid ADD_LECTURE format. Message: " + response);
            out.println("ERROR: Invalid ADD_LECTURE format. Expected format: LectureName, CourseID, Room, Type, Day, StartTime, duration");
        }
    }
        
    


    private static void handleRemoveLecture(String lectureData, PrintWriter out) {
        if (lectureStorage.containsKey(lectureData)) {
            String[] lectureDetails = lectureStorage.get(lectureData).split(",");
            String room = lectureDetails[3];
            String day = lectureDetails[4];
            String startTime = lectureDetails[5];
            String duration = lectureDetails[6];
            
            //removes the lecture from the timetable and availablility
            String availData = day + "_" + startTime;
            String spaceData = room + "_" + day + "_" + startTime;
                        
            if (timetableSpaces.contains(spaceData) && studentAvailibility.contains(availData)) {                
                if (duration.equals("2")) {
                    int time = Integer.parseInt(startTime.split(":")[0]);
                    time += 1;
                    String extraTime = String.format("%02d:00", time);
                    String extraSpace = room + "_" + day + "_" + extraTime;
                    String extraAvail = day + "_" + extraTime;
                    timetableSpaces.remove(spaceData);
                    timetableSpaces.remove(extraSpace);
                    studentAvailibility.remove(availData);
                    studentAvailibility.remove(extraAvail);
                    lectureStorage.remove(lectureData);
                    System.out.println("Removing two hour lecture");
                } else {
                    timetableSpaces.remove(spaceData);
                    studentAvailibility.remove(availData);
                    lectureStorage.remove(lectureData);
                    System.out.println("Removing one hour lecture");
                }
            }
            out.println("Lecture Removed Successfully!");
        } else {
            out.println("ERROR: Lecture not found.");
        }   
    }
    
    private static void handleViewSchedule(PrintWriter out) {
        if (lectureStorage.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
        } else {
            StringBuilder scheduleData = new StringBuilder();
            for (Map.Entry<String, String> entry : lectureStorage.entrySet()) {
                scheduleData.append(entry.getValue()).append(";");
            }
            out.println(scheduleData.toString());
        }
    }
    
    private static void sendLectureKeys(PrintWriter out) {
        if (lectureStorage.isEmpty()) {
            out.println("No lectures available.");
        } else {
            StringBuilder keys = new StringBuilder();
            for (String key : lectureStorage.keySet()) {
                keys.append(key).append(";");
            }
            out.println(keys.toString());
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