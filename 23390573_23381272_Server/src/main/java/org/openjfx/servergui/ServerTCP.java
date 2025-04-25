package org.openjfx.servergui;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerTCP {
    private static final int PORT = 5555;
    private static String userType;
    private static LectureController lectureCon;
    private static EarlyLectureController earlyCon;
    private static LoginController loginCon;
    private static ManageStudentController studentCon;
    private static CSVController csvCon;
    private static ServerSocket serverSocket;
    private static Consumer<String> logger;
    private static Map<String, String> loggedInUsers = new ConcurrentHashMap<>();

    public void setLogger(Consumer<String> logger) {
        this.logger = logger;
    }
    
    private static void log(String message) {
        if (logger != null) {
            logger.accept(message);
        }
        System.out.println(message);
    }

    public void startServer() throws IOException {
        lectureCon = new LectureController();
        lectureCon.loadCSVData();
        earlyCon = new EarlyLectureController();
        serverSocket = new ServerSocket(PORT);
        loginCon = new LoginController();
        studentCon = new ManageStudentController();
        csvCon = new CSVController();
        
        studentCon.csvConSetter();
        
        log("Server started on port " + PORT);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new ClientHandler(clientSocket, logger), "t1");
                t.start();
            }
        } catch (IOException e) {
            log("Server stopped: " + e.getMessage());
        }
    }

    public static void processClientMessage(String message, BufferedReader in, PrintWriter out) throws IOException {
        String currentUser = Thread.currentThread().getName();
        switch (message) {
            case "LOGIN":
                out.println("SEND_USER_DETAILS");
                String credentials = in.readLine();
                String[] loginData = credentials.split(":");
                if (loginData.length == 2 && loginCon.authenticate(loginData[0], loginData[1])) {
                    userType = loginCon.getUserType();
                    loggedInUsers.put(loginData[0], userType);
                    if (userType.equals("Student")) {
                        out.println("LOGIN_SUCCESS_STUDENT");
                        log(loginData[0] + " Has successfully logged in");
                    } else {
                        out.println("LOGIN_SUCCESS_ADMIN");
                        log(loginData[0] + " Has successfully logged in");
                    }
                    Thread.currentThread().setName(loginData[0]);
                    updateClientList(); // Call this to update the GUI
                } else {
                    out.println("Invalid username or password.");
                    log("Invalid credentials inputted");
                }
                break;
                
            case "LOG_OUT":
                // Remove user from loggedInUsers map when they log out
                loggedInUsers.remove(currentUser);
                out.println("LOGGING_OUT");
                log(currentUser + " Has Logged out");
                System.out.println("Client logging out...\n");
                updateClientList(); // Call this to update the GUI
                break;

            case "ADD_LECTURE":
                out.println("OPEN_ADD_LECTURE_PAGE");
                System.out.println("Opening the add lecture page...\n");
                log("Opening add lecture page for " + currentUser);
                break;

            case "SUBMIT_LECTURE_STUDENT":
                out.println("SEND_DATA");
                String scheduleData = in.readLine();
                if ("BACK".equals(scheduleData)) {
                    out.println("RETURNING");
                } else {
                    lectureCon.handleAddLectureStudent(scheduleData, out);
                    log(currentUser + " Added " + scheduleData);
                }
                break;
                
            case "SUBMIT_LECTURE_ADMIN":
                out.println("SEND_DATA");
                String scheduleData2 = in.readLine();
                if ("BACK".equals(scheduleData2)) {
                    out.println("RETURNING");
                } else {
                    lectureCon.handleAddLectureAdmin(scheduleData2, out);
                    log(currentUser + " Added " + scheduleData2);
                }
                break;

            case "REMOVE_LECTURE":
                out.println("OPEN_REMOVE_LECTURE_PAGE");
                System.out.println("Opening the remove lecture page...\n");
                log("Opening remove lecture page for " + currentUser);
                break;

            case "SEND_LECTURES_STUDENT":
                lectureCon.sendLectureKeysStudent(out);
                break;

            case "SEND_LECTURES_ADMIN":
                lectureCon.sendLectureKeysAdmin(out);
                break;                

            case "REMOVE_THIS_LECTURE_STUDENT":
                out.println("REQUEST_DATA");
                String lectureToRemove = in.readLine();
                lectureCon.handleRemoveLectureStudent(lectureToRemove, out);
                log(currentUser + " Removed " + lectureToRemove);
                break;
                
            case "REMOVE_THIS_LECTURE_ADMIN":
                out.println("REQUEST_DATA");
                String lectureToRemove2 = in.readLine();
                lectureCon.handleRemoveLectureAdmin(lectureToRemove2, out);
                log(currentUser + " Removed " + lectureToRemove2);
                break;
                
            case "TIMETABLE_POPUP":
                out.println("OPEN_TIMETABLE_POPUP");
                System.out.println("Opening timetable popup page...\n");
                log("Opening timetable popup page for " + currentUser);
                break;

            case "GROUP_TIMETABLE":
                out.println("OPEN_GROUP_TIMETABLE_PAGE");
                System.out.println("Opening the group timetable page...\n");
                log("Opening group timetable page for " + currentUser);
                break;
                
            case "PERSONAL_TIMETABLE":
                out.println("OPEN_PERSONAL_TIMETABLE_PAGE");
                System.out.println("Opening the personal timetable page...\n");
                log("Opening personal timetable page for " + currentUser);
                break;

            case "SEND_LECTURE_DETAILS_GROUP":
                lectureCon.handleSendLectureDetailsGroup(out);
                break;
            
            case "SEND_LECTURE_DETAILS_PERSONAL":
                lectureCon.handleSendLectureDetailsPersonal(out);
                break;
                
            case "EARLY_LECTURE_STUDENT":
                Map <String, String> oldMapStudent = lectureCon.getLectureStoragePersonal();
                oldMapStudent = lectureCon.getUserMap(oldMapStudent);
                Map<String, String> newMapStudent = earlyCon.adjustTimetableParallel(oldMapStudent);
                newMapStudent = lectureCon.rebuildMap(newMapStudent, currentUser);
                lectureCon.setLectureStoragePersonal(newMapStudent);
                out.println("MAKING_LECTURES_EARLIER_STUDENT");
                log(currentUser + " Made their Lectures Earlier");
                break;
                
            case "EARLY_LECTURE_ADMIN":
                Map <String, String> oldMapGroup = lectureCon.getLectureStorageGroup();
                Map<String, String> newMapGroup = earlyCon.adjustTimetableParallel(oldMapGroup);
                lectureCon.setLectureStorageGroup(newMapGroup);
                out.println("MAKING_LECTURES_EARLIER_ADMIN");
                log("Making Module Lectures Earlier");
                break;

            case "MANAGE_STUDENTS":
                out.println("OPENING_MANAGE_STUDENTS_PAGE");
                System.out.println("Opening the manage students page...\n");
                log("Opening manage student page for " + currentUser);
                break;

            case "ADD_STUDENT":
                out.println("SEND_STUDENT_DATA");
                String studentData = in.readLine();
                studentCon.addStudent(studentData, out);
                log("Student: " + studentData + " Added by " + currentUser);
                break;

            case "REMOVE_STUDENT":
                out.println("REMOVING_STUDENT");
                String studentId = in.readLine();
                studentCon.removeStudent(studentId, out);
                log("Student: " + studentId + " removed by " + currentUser);
                break;

            case "GET_STUDENTS":
                studentCon.sendStudentNames(out);
                System.out.println("Sending student data...\n");
                log("Displaying List of Students to " + currentUser);
                break;

            case "OTHER":
                out.println("OPEN_OTHER_PAGE");
                System.out.println("Opening the other page...\n");
                log("Opening the other page for " + currentUser);
                break;

            case "BACK":
                out.println("RETURNING");
                System.out.println("Returning...\n");
                log("Returning...");
                break;

            case "QUIT":
                out.println("GOODBYE");
                System.out.println("Closing connection...\n");
                log("Closing connection with " + currentUser);
                break;

            default:
                out.println("ERROR: Invalid Request: " + message);
                log("ERROR: Invalid Request: " + message);
        }
    }
    
    public void stopServer() {
        try {
        serverSocket.close();
        } catch (IOException e) {
            System.err.println("Unable to close server");
        }
    }
    
    private static void updateClientList() {
        if (logger != null) {
            StringBuilder sb = new StringBuilder("CLIENT_LIST_UPDATE:");
            for (Map.Entry<String, String> entry : loggedInUsers.entrySet()) {
                sb.append(entry.getKey()).append(" (").append(entry.getValue()).append("),");
            }
            if (!loggedInUsers.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
            logger.accept(sb.toString());
        }
    }
    
    public LectureController getLectureController() {
        return lectureCon;
    }
    
    public EarlyLectureController getEarlyLectureController() {
        return earlyCon;
    }
    
    public ManageStudentController getManageStudentController() {
        return studentCon;
    }
}
