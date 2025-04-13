package org.openjfx._23381272_Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerTCP {
    private static final int PORT = 5555;
    private static Map<String, String> lectureStorageGroup = new HashMap<>();
    private static final Map<String, String> lectureStoragePersonal = new HashMap<>();
    private static final ArrayList<String> timetableSpacesGroup = new ArrayList<>();
    private static final ArrayList<String> timetableSpacesPersonal = new ArrayList<>();
    private static final ArrayList<String> studentAvailibilityGroup = new ArrayList<>();
    private static final ArrayList<String> studentAvailibilityPersonal = new ArrayList<>();
    private static String studentID;
    private static String userType;
    private static final String USER_PASSWORD_CSV_PATH = "CSV_Files/User_Password.csv";
    private static final String GROUPTIMETABLE_CSV_PATH = "CSV_Files/GroupTimetable.csv";


    public static void main(String[] args) {
        System.out.println("Opening port...\n");

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
        switch (message) {
            case "LOGIN":
                out.println("SEND_USER_DETAILS");
                String credentials = in.readLine();
                String[] loginData = credentials.split(":");
                if (loginData.length == 2 && authenticate(loginData[0], loginData[1])) {
                    if (userType.equals("Student")) {
                        out.println("LOGIN_SUCCESS_STUDENT");
                        System.out.println(loginData[0] + " Has successfully logged in\n");
                        lectureStorageGroup.clear();
                        lectureStorageGroup = CSVController.csvToMap(GROUPTIMETABLE_CSV_PATH);
                    } else {
                        out.println("LOGIN_SUCCESS_ADMIN");
                        System.out.println(loginData[0] + " Has successfully logged in\n");
                        lectureStorageGroup.clear();
                        lectureStorageGroup = CSVController.csvToMap(GROUPTIMETABLE_CSV_PATH);
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

            case "SUBMIT_LECTURE":
                out.println("SEND_DATA");
                String scheduleData = in.readLine();
                if ("BACK".equals(scheduleData)) {
                    out.println("RETURNING");
                } else {
                    handleAddLecture(scheduleData, out);
                }
                break;

            case "REMOVE_LECTURE":
                out.println("OPEN_REMOVE_LECTURE_PAGE");
                System.out.println("Opening the remove lecture page...\n");
                break;

            case "SEND_LECTURES":
                sendLectureKeys(out);
                break;

            case "REMOVE_THIS_LECTURE":
                out.println("REQUEST_DATA");
                String lectureToRemove = in.readLine();
                handleRemoveLecture(lectureToRemove, out);
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
                handleSendLectureDetailsGroup(out);
                break;
            
            case "SEND_LECTURE_DETAILS_PERSONAL":
                handleSendLectureDetailsPersonal(out);
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
                    if (userData[0].trim().equals(studentId) && userData[1].trim().equals(password)) {
                        studentID = userData[0].trim();
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

    private static void handleAddLecture(String lectureData, PrintWriter out) {
        String[] module = lectureData.split("@");
        if (module.length == 7) {
            String key = module[0] + "_" + module[3] + "_" + module[2] + "_" + module[4] + "_" + module[5];
            String detail = lectureData;
            String slot = module[2] + "_" + module[4] + "_" + module[5];
            String studentSlot = module[4] + "_" + module[5];
            if (userType.equals("Student")) {
                if (lectureStoragePersonal.containsKey(key)) {
                    out.println("Error: Lecture already exists.");
                } else if (timetableSpacesPersonal.contains(slot)) {
                    out.println("Error: Room unavailable at this time.");
                } else if (studentAvailibilityPersonal.contains(studentSlot)) {
                    out.println("Error: Student unavailable at this time.");
                } else {
                    lectureStoragePersonal.put(key, detail);
                    timetableSpacesPersonal.add(slot);
                    studentAvailibilityPersonal.add(studentSlot);

                    if ("2".equals(module[6])) {
                        int time = Integer.parseInt(module[5].split(":" )[0]) + 1;
                        String extraTime = String.format("%02d:00", time);
                        timetableSpacesPersonal.add(module[2] + "_" + module[4] + "_" + extraTime);
                        studentAvailibilityPersonal.add(module[4] + "_" + extraTime);
                    }
                    out.println("Lecture Added Successfully!");
                }
            } else {
                if (lectureStorageGroup.containsKey(key)) {
                    out.println("Error: Lecture already exists.");
                } else if (timetableSpacesGroup.contains(slot)) {
                    out.println("Error: Room unavailable at this time.");
                } else if (studentAvailibilityGroup.contains(studentSlot)) {
                    out.println("Error: Student unavailable at this time.");
                } else {
                    lectureStorageGroup.put(key, detail);
                    timetableSpacesGroup.add(slot);
                    studentAvailibilityGroup.add(studentSlot);

                    if ("2".equals(module[6])) {
                        int time = Integer.parseInt(module[5].split(":" )[0]) + 1;
                        String extraTime = String.format("%02d:00", time);
                        timetableSpacesGroup.add(module[2] + "_" + module[4] + "_" + extraTime);
                        studentAvailibilityGroup.add(module[4] + "_" + extraTime);
                    }
                    out.println("Lecture Added Successfully!");
                    CSVController.clearCSV(GROUPTIMETABLE_CSV_PATH);
                    
                    for (var entry : lectureStorageGroup.entrySet()) {
                        try {
                            String csvKey = entry.getKey();
                            String csvValue = entry.getValue();
                            String[] row = {csvKey, csvValue};
                            CSVController.appendLineToCSV(GROUPTIMETABLE_CSV_PATH, row);
                            System.out.println("bug test" + row);
                        } catch (IOException e) {
                            System.out.println("Could not save to csv");
                        }    
                    }
                }
            }
        } else {
            out.println("ERROR: Invalid format");
        }
    }

    private static void handleRemoveLecture(String key, PrintWriter out) {
        if (userType.equals("Student")) {
            if (lectureStoragePersonal.containsKey(key)) {
                String[] details = lectureStoragePersonal.get(key).split("@");
                String slot = details[2] + "_" + details[4] + "_" + details[5];
                String studentSlot = details[4] + "_" + details[5];

                timetableSpacesPersonal.remove(slot);
                studentAvailibilityPersonal.remove(studentSlot);
                if ("2".equals(details[6])) {
                    int time = Integer.parseInt(details[5].split(":" )[0]) + 1;
                    String extraTime = String.format("%02d:00", time);
                    timetableSpacesPersonal.remove(details[2] + "_" + details[4] + "_" + extraTime);
                    studentAvailibilityPersonal.remove(details[4] + "_" + extraTime);
                }

                lectureStoragePersonal.remove(key);
                out.println("Lecture Removed Successfully!");
            } else {
                out.println("ERROR: Lecture not found.");
            }
        } else if (userType.equals("Admin")){
                if (lectureStorageGroup.containsKey(key)) {
                    String[] details = lectureStorageGroup.get(key).split("@");
                    String slot = details[2] + "_" + details[4] + "_" + details[5];
                    String studentSlot = details[4] + "_" + details[5];

                    timetableSpacesGroup.remove(slot);
                    studentAvailibilityGroup.remove(studentSlot);
                    if ("2".equals(details[6])) {
                        int time = Integer.parseInt(details[5].split(":" )[0]) + 1;
                        String extraTime = String.format("%02d:00", time);
                        timetableSpacesGroup.remove(details[2] + "_" + details[4] + "_" + extraTime);
                        studentAvailibilityGroup.remove(details[4] + "_" + extraTime);
                    }
                }
                
            try {
                CSVController.removeLineFromCSV(GROUPTIMETABLE_CSV_PATH, key);
                System.out.println("Could not remove lecture from csv.");
                lectureStorageGroup.remove(key);
                out.println("Lecture Removed Successfully!");
            } catch (IOException e) {
            System.out.println("Could not remove from csv");
            }
            
            } else {
                out.println("ERROR: Lecture not found.");
            }
        }
    


    private static void handleSendLectureDetailsGroup(PrintWriter out) {
        if (lectureStorageGroup.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String val : lectureStorageGroup.values()) {
                sb.append(val).append(";");
            }
            out.println(sb.toString());
            System.out.println("GROUP TEST");
        }
    }
    
    private static void handleSendLectureDetailsPersonal(PrintWriter out) {
        if (lectureStoragePersonal.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String val : lectureStoragePersonal.values()) {
                sb.append(val).append(";");
            }
            out.println(sb.toString());
            System.out.println("PERSONAL TEST");
        }
    }

    private static void sendLectureKeys(PrintWriter out) {
        if (userType.equals("Student")) {
            if (lectureStoragePersonal.isEmpty()) {
                out.println("NO_LECTURES_AVAILABLE");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String key : lectureStoragePersonal.keySet()) {
                    sb.append(key).append(";");
                }
                out.println(sb.toString());
            }
        } else {
            if (lectureStorageGroup.isEmpty()) {
                out.println("NO_LECTURES_AVAILABLE");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String key : lectureStorageGroup.keySet()) {
                    sb.append(key).append(";");
                }
                out.println(sb.toString());
            }
        }
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
    
    private static void populateArrayLists(String path, ArrayList list, int pos) {
        try {
            List<String[]> csvData = CSVController.readCSV(USER_PASSWORD_CSV_PATH);
            for (String[] data : csvData) {
                list.add(data[pos]);
            }
    
        } catch (IOException e) {
            System.out.println("Error reading csv.");
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
