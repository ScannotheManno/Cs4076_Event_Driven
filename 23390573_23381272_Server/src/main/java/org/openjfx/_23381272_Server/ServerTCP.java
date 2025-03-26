package org.openjfx._23381272_Server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerTCP {
    private static ServerSocket servSock;
    private static final int PORT = 5555;
    private static int clientConnections = 0;
    private static final Map<String, String> lectureStorage = new HashMap<>();
    private static final ArrayList<String> timetableSpaces = new ArrayList<>();
    private static final ArrayList<String> studentAvailibility = new ArrayList<>();
    public static void main(String[] args) {
        System.out.println("Opening port...\n");

        // Checks if port is not available or server is already running. If not then close server
        if (!isPortAvailable(PORT)) {
            System.out.println("Port " + PORT + " is already in use. Please free the port or use a different one.");
            System.exit(1);
        }

        try {
            servSock = new ServerSocket(PORT);

            // Make connection with client
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
            // Reads message from client and if none is sent break the loop
            while (true) {
                String message = in.readLine();
                if (message == null) break;

                System.out.println("Received from client: " + message);
                
                // Finds client message and opens appropriate menu or completes an action
                try {
                    switch (message) {
                        case "LOGIN":
                            out.println("SEND_USER_DETAILS");
                            String credentials = in.readLine();
                            String[] loginData = credentials.split(":");
                            if (loginData.length == 2 && authenticate(loginData[0], loginData[1]) == true) {
                                out.println("LOGIN_SUCCESS");
                                System.out.println("User " + loginData[0] + " logged in successfully.");
                            } else {
                                out.println("Invalid username or password.");
                                System.out.println("Failed login attempt for user: " + loginData[0]);
                            }
                            break;
                            
                        case "ADD_LECTURE": // If ADD_LECTURE is recieved then respond with OPEN_ADD_LECTURE_PAGE(opens the add lecture page)
                            System.out.println("Opening Add Lecture page...\n");
                            out.println("OPEN_ADD_LECTURE_PAGE");
                            break;

                        case "SUBMIT_LECTURE": // If SUBMIT_LECTURE os recieved then respond with SEND_DATA(requests new lecture data from add lecture)
                            System.out.println("Waiting for data...\n");
                            out.println("SEND_DATA");
                            String scheduleData = in.readLine();
                            if (scheduleData.equals("BACK")) {
                                System.out.println("Recieved from client: BACK\nReturning to main menu...\n");
                                out.println("RETURNING");
                            } else {
                                handleAddLecture(scheduleData, out);
                            }
                            break;
                            
                        case "REMOVE_LECTURE": // If REMOVE_LECTURE is recieved then resoind with OPEN_REMOVE_LECTURE_PAGE(opens the remove lecture page)
                            System.out.println("Opening Remove Lecture page...\n");
                            out.println("OPEN_REMOVE_LECTURE_PAGE");
                            break;
                            
                        case "SEND_LECTURES": // If SEND_LECTURES is recieved then send all the lecture keys from lectureStorage Hashmap.
                            System.out.println("Sending lecture data...\n");
                            sendLectureKeys(out);
                            break;
                            
                        case "REMOVE_THIS_LECTURE": // If REMOVE_THIS_LECTURE is recieved then respond with REQUEST_DATA(requests lecture data to remove from hashmap)
                            System.out.println("Waiting for data...");
                            out.println("REQUEST_DATA");
                            String lectureData = in.readLine();
                            handleRemoveLecture(lectureData, out);
                            System.out.println("Removing " + lectureData + "...\n");
                            break;

                        case "VIEW_SCHEDULE": // If VIEW_SCHEDULE is recieved the respond with OPEN_VIEW_SCHEDULE_PAGE(opens the timetable page)
                            System.out.println("Opening Schedule...\n");
                            out.println("OPEN_VIEW_SCHEDULE_PAGE");
                            break;
                            
                        case "SEND_LECTURE_DETAILS": // If SEND_LECTURE_DETAILS is recieved then send all lecture details to client
                            System.out.println("Sending lecture data...\n");
                            handleSendLectureDetails(out);
                            break;

                        case "OTHER": // If OTHER is recieved then respond with OPEN_OTHER_PAGE(opens the other page)
                            System.out.println("Opening other page...\n");
                            out.println("OPEN_OTHER_PAGE");
                            break;
                            
                        case "BACK": // If BACK is recieved then respond with RETURNING(for timetable to return to close window)
                            System.out.println("Closing timetable...\n");
                            out.println("RETURNING");
                            break;
                            
                        case "LOG_OUT":
                            System.out.println("User is logging out...\n");
                            out.println("LOGGING_OUT");

                        case "QUIT": // If QUIT is recieved then respond with GOODBYE(closes connection with client)
                            System.out.println("Client disconnected...\n");
                            out.println("GOODBYE");
                            return;
                        default: // If request is not available in the server then throw IncorrectActionException
                            throw new IncorrectActionException();
                    }
                } catch (IncorrectActionException e) { // catch this exception and send message to server
                    System.out.println("Error: " + e.getMessage() + message + "\n");
                    out.println("ERROR: " + e.getMessage() + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            // Closes the connection
            try {
                link.close();
                System.out.println("Client connection closed.\n");
            } catch (IOException e) {
                System.out.println("Unable to close connection.");
            }
        }
    }

    // Handles adding and saving a lecture inputted from client
    private static void handleAddLecture(String lectureData, PrintWriter out) {
        
        // if response is empty then return
        String response = lectureData;
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Empty message received. Ignoring.");
            return;
        }

        // Breaks up data into an array
        String[] module = response.split(",");
        // If all data is accounted for then continue
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
            
            
            
            if (lectureStorage.containsKey(lectureKey)) { // Check if the Lecture is already in the HashMap
                System.out.println("Error: Lecture already exists. \n");
                out.println("Error: Lecture already exists.");
            } else if (timetableSpaces.contains(fillTimetable)) { // Check if the room is available at a specified time
                System.out.println("Error: This room is already in use at this time. \n");
                out.println("Error: This room is already in use at the time specified.");
            } else if (studentAvailibility.contains(availibility)) { // Check if student is available at a specified time (seperate from room)
                System.out.println("Student unavailable at this time. \n");
                out.println("Error: Student already has a class at this time.");
            } else { // If the above is available add lecture
                lectureStorage.put(lectureKey, lectureDetails);
                timetableSpaces.add(fillTimetable);
                studentAvailibility.add(availibility);
                if (duration.equals("2")) { // Handles if the lecture is 2 hours long
                    int time = Integer.parseInt(startTime.split(":")[0]);
                    time += 1;
                    String extraTime = String.format("%02d:00", time);
                    String extraSpace = room + "_" + day + "_" + extraTime;
                    String extraAvail = day + "_" + extraTime; 
                    timetableSpaces.add(extraSpace); 
                    studentAvailibility.add(extraAvail);
                }
                
                System.out.println("New Lecture Added: " + lectureDetails + "\n");
                out.println("Lecture Added Successfully!");
            }
        } else {
            // If all the data is not present or the data is formatted incorrectly then print appropriate message and send to server
            System.out.println("ERROR: Invalid ADD_LECTURE format. Message: " + response);
            out.println("ERROR: Invalid ADD_LECTURE format. Expected format: LectureName, CourseID, Room, Type, Day, StartTime, duration");
        }
    }
        
    

    // Handles the removing of lectures from the Hashmap and freeing up of the timetable slots
    private static void handleRemoveLecture(String lectureData, PrintWriter out) {
        // Checks if the lecture is in the Hashmap
        if (lectureStorage.containsKey(lectureData)) {
            String[] lectureDetails = lectureStorage.get(lectureData).split(",");
            String room = lectureDetails[3];
            String day = lectureDetails[4];
            String startTime = lectureDetails[5];
            String duration = lectureDetails[6];
            
            String availData = day + "_" + startTime;
            String spaceData = room + "_" + day + "_" + startTime;
            
            // Checks if the timetable slot and the student availability is in the appropriate ArrayList
            if (timetableSpaces.contains(spaceData) && studentAvailibility.contains(availData)) {    
                // Checks the duration of the class to handle accordingly
                if (duration.equals("2")) { // Runs if duration is two hours
                    int time = Integer.parseInt(startTime.split(":")[0]);
                    time += 1;
                    String extraTime = String.format("%02d:00", time);
                    String extraSpace = room + "_" + day + "_" + extraTime;
                    String extraAvail = day + "_" + extraTime;
                    
                    // Removes the lecture, timetable blocker and student availibility blocker
                    timetableSpaces.remove(spaceData);
                    timetableSpaces.remove(extraSpace);
                    studentAvailibility.remove(availData);
                    studentAvailibility.remove(extraAvail);
                    lectureStorage.remove(lectureData);
                    System.out.println("Removing two hour lecture...\n");
                } else { // Runs if duration is one hour
                    // Removes the lecture, timetable blocker and student availibility blocker
                    timetableSpaces.remove(spaceData);
                    studentAvailibility.remove(availData);
                    lectureStorage.remove(lectureData);
                    System.out.println("Removing one hour lecture...\n");
                }
            }
            out.println("Lecture Removed Successfully!");
        } else { // If lecture is not found then alert client
            out.println("ERROR: Lecture not found.");
        }   
    }
    
    // Sends lecture details to client to display on timetable
    private static void handleSendLectureDetails(PrintWriter out) {
        // If no lectures are scheduled then alert client
        if (lectureStorage.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
        } else { // Send data to client
            StringBuilder scheduleData = new StringBuilder();
            for (Map.Entry<String, String> entry : lectureStorage.entrySet()) {
                scheduleData.append(entry.getValue()).append(";");
            }
            out.println(scheduleData.toString());
        }
    }
    
    // Sends the lecture keys from hashmap to client for removing the lecture
    private static void sendLectureKeys(PrintWriter out) {
        if (lectureStorage.isEmpty()) {
            out.println("NO_LECTURES_AVAILABLE");
        } else {
            StringBuilder keys = new StringBuilder();
            for (String key : lectureStorage.keySet()) {
                keys.append(key).append(";");
            }
            out.println(keys.toString());
        }
    }
    
    private static boolean authenticate(String studentId, String password) {
        InputStream input = ServerTCP.class.getResourceAsStream("/CSV_Files/User_Password.csv");

        if (input == null) {
            System.err.println("ERROR: CSV file not found! Check the file path.");
            return false;
        } else {
            System.out.println("CSV file found! Reading data...");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            br.readLine(); // Skip header line if it exists

            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");

                if (userData.length == 2) {
                    String csvStudentId = userData[0].trim();
                    String csvPassword = userData[1].trim();

                    if (csvStudentId.equals(studentId) && csvPassword.equals(password)) {
                        return true; // Successful login
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }

        return false; // Login failed
    }


    // Checks if the port is available
    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}