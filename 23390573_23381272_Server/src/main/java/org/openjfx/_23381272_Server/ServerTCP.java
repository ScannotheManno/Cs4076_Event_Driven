package org.openjfx._23381272_Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerTCP {
    private static final int PORT = 5555;
    private static String userType;
    private static final String USER_PASSWORD_CSV_PATH = "CSV_Files/User_Password.csv";

    public static void main(String[] args) {
        System.out.println("Opening port...\n");
        LectureController.loadCSVData();

        if (!isPortAvailable(PORT)) {
            System.out.println("Port " + PORT + " is already in use.");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected");
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static void processClientMessage(String message, BufferedReader in, PrintWriter out) throws IOException {
        LectureController lecCon = new LectureController();
        switch (message) {
            case "LOGIN":
                out.println("SEND_USER_DETAILS");
                String credentials = in.readLine();
                String[] loginData = credentials.split(":");
                if (loginData.length == 2 && authenticate(loginData[0], loginData[1])) {
                    if (userType.equals("Student")) {
                        out.println("LOGIN_SUCCESS_STUDENT");
                        System.out.println(loginData[0] + " Has successfully logged in\n");
                    } else {
                        out.println("LOGIN_SUCCESS_ADMIN");
                        System.out.println(loginData[0] + " Has successfully logged in\n");
                    }
                } else {
                    out.println("Invalid username or password.");
                        System.out.println("Invalid credentials inputted\n");
                }
                break;

            case "ADD_LECTURE":
                out.println("OPEN_ADD_LECTURE_PAGE");
                System.out.println("Opening the add lecture page...\n");
                break;

            case "SUBMIT_LECTURE_STUDENT":
                out.println("SEND_DATA");
                String scheduleData = in.readLine();
                if ("BACK".equals(scheduleData)) {
                    out.println("RETURNING");
                } else {
                    lecCon.handleAddLectureStudent(scheduleData, out);
                }
                break;
                
            case "SUBMIT_LECTURE_ADMIN":
                out.println("SEND_DATA");
                String scheduleData2 = in.readLine();
                if ("BACK".equals(scheduleData2)) {
                    out.println("RETURNING");
                } else {
                    lecCon.handleAddLectureAdmin(scheduleData2, out);
                }
                break;

            case "REMOVE_LECTURE":
                out.println("OPEN_REMOVE_LECTURE_PAGE");
                System.out.println("Opening the remove lecture page...\n");
                break;

            case "SEND_LECTURES_STUDENT":
                lecCon.sendLectureKeysStudent(out);
                break;

            case "SEND_LECTURES_ADMIN":
                lecCon.sendLectureKeysAdmin(out);
                break;                

            case "REMOVE_THIS_LECTURE_STUDENT":
                out.println("REQUEST_DATA");
                String lectureToRemove = in.readLine();
                lecCon.handleRemoveLectureStudent(lectureToRemove, out);
                break;
                
            case "REMOVE_THIS_LECTURE_ADMIN":
                out.println("REQUEST_DATA");
                String lectureToRemove2 = in.readLine();
                lecCon.handleRemoveLectureAdmin(lectureToRemove2, out);
                break;
                
            case "TIMETABLE_POPUP":
                out.println("OPEN_TIMETABLE_POPUP");
                System.out.println("Opening the group Timetable page...\n");
                break;

            case "GROUP_TIMETABLE":
                out.println("OPEN_GROUP_TIMETABLE_PAGE");
                System.out.println("Opening the group Timetable page...\n");
                break;
                
            case "PERSONAL_TIMETABLE":
                out.println("OPEN_PERSONAL_TIMETABLE_PAGE");
                System.out.println("Opening the group Timetable page...\n");
                break;

            case "SEND_LECTURE_DETAILS_GROUP":
                lecCon.handleSendLectureDetailsGroup(out);
                break;
            
            case "SEND_LECTURE_DETAILS_PERSONAL":
                lecCon.handleSendLectureDetailsPersonal(out);
                break;

            case "MANAGE_STUDENTS":
                out.println("OPENING_MANAGE_STUDENTS_PAGE");
                System.out.println("Opening the manage students page...\n");
                break;

            case "ADD_STUDENT":
                out.println("SEND_STUDENT_DATA");
                String studentData = in.readLine();
                addStudent(studentData, out);
                break;

            case "REMOVE_STUDENT":
                out.println("REMOVING_STUDENT");
                String studentId = in.readLine();
                removeStudent(studentId, out);
                break;

            case "GET_STUDENTS":
                sendStudentNames(out);
                System.out.println("Sending student data...\n");
                break;

            case "OTHER":
                out.println("OPEN_OTHER_PAGE");
                System.out.println("Opening the other page...\n");
                break;

            case "BACK":
                out.println("RETURNING");
                System.out.println("Returning...\n");
                break;

            case "LOG_OUT":
                out.println("LOGGING_OUT");
                System.out.println("Client logging out...\n");
                break;

            case "QUIT":
                out.println("GOODBYE");
                System.out.println("Closing connection...\n");
                break;

            default:
                out.println("ERROR: Invalid Request: " + message);
        }
    }

    private static boolean authenticate(String studentId, String password) {
        try {
            List<String[]> csvData = CSVController.readCSV(USER_PASSWORD_CSV_PATH);
            for (String[] userData : csvData) {
                if (userData.length == 3) {
                    if (userData[0].equals(studentId) && userData[1].equals(password)) {
                        userType = userData[2].trim();
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV");
        }
        return false;
    }

    private static void sendStudentNames(PrintWriter out) {
        try {
            List<String[]> data = CSVController.readCSV(USER_PASSWORD_CSV_PATH);
            if (data.isEmpty()) {
                out.println("NO_STUDENTS_AVAILABLE");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String[] row : data) {
                if (row.length == 3) sb.append(row[0].trim()).append(":" );
            }
            out.println(sb.toString());
        } catch (IOException e) {
            out.println("ERROR: Cannot read student list");
        }
    }

    private static void addStudent(String line, PrintWriter out) {
        try {
            String[] parts = line.split(";");
            CSVController.appendLineToCSV(USER_PASSWORD_CSV_PATH, parts);
            out.println(parts[0] + " was added.");
        } catch (IOException e) {
            out.println("ERROR: Could not add student");
        }
    }

    private static void removeStudent(String id, PrintWriter out) {
        try {
            CSVController.removeLineFromCSV(USER_PASSWORD_CSV_PATH, id);
            out.println(id + " Was removed from the database");
        } catch (IOException e) {
            out.println("ERROR: Could not remove student");
        }
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket s = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
