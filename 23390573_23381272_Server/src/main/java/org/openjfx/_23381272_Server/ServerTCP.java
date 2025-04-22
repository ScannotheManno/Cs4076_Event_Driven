package org.openjfx._23381272_Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerTCP {
    private static final int PORT = 5555;
    private static String userType;
    private static final String USER_PASSWORD_CSV_PATH = "CSV_Files/User_Password.csv";
    private static LectureController lectureCon;
    private static EarlyLectureController earlyCon;
    private static LoginController loginCon;
    private static ManageStudentController studentCon;

    // in ServerTCP.java, main()
    public static void main(String[] args) throws IOException {
        lectureCon = new LectureController();
        lectureCon.loadCSVData();
        earlyCon  = new EarlyLectureController();
        loginCon = new LoginController();
        studentCon = new ManageStudentController();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        }
    }


    public static void processClientMessage(String message, BufferedReader in, PrintWriter out) throws IOException {
        switch (message) {
            case "LOGIN":
                out.println("SEND_USER_DETAILS");
                String credentials = in.readLine();
                String[] loginData = credentials.split(":");
                userType = loginCon.getUserType();
                if (loginData.length == 2 && loginCon.authenticate(loginData[0], loginData[1])) {
                    userType = loginCon.getUserType();
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
                    lectureCon.handleAddLectureStudent(scheduleData, out);
                }
                break;
                
            case "SUBMIT_LECTURE_ADMIN":
                out.println("SEND_DATA");
                String scheduleData2 = in.readLine();
                if ("BACK".equals(scheduleData2)) {
                    out.println("RETURNING");
                } else {
                    lectureCon.handleAddLectureAdmin(scheduleData2, out);
                }
                break;

            case "REMOVE_LECTURE":
                out.println("OPEN_REMOVE_LECTURE_PAGE");
                System.out.println("Opening the remove lecture page...\n");
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
                break;
                
            case "REMOVE_THIS_LECTURE_ADMIN":
                out.println("REQUEST_DATA");
                String lectureToRemove2 = in.readLine();
                lectureCon.handleRemoveLectureAdmin(lectureToRemove2, out);
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
                lectureCon.handleSendLectureDetailsGroup(out);
                break;
            
            case "SEND_LECTURE_DETAILS_PERSONAL":
                lectureCon.handleSendLectureDetailsPersonal(out);
                break;
                
            case "EARLY_LECTURE_STUDENT":
                Map <String, String> oldMapStudent = lectureCon.getLectureStoragePersonal();
                 Map<String, String> newMapStudent = earlyCon.adjustTimetableParallel(oldMapStudent);
                lectureCon.setLectureStoragePersonal(newMapStudent);
                out.println("MAKING_LECTURES_EARLIER_STUDENT");
                break;
                
            case "EARLY_LECTURE_ADMIN":
                Map <String, String> oldMapGroup = lectureCon.getLectureStorageGroup();
                Map<String, String> newMapGroup = earlyCon.adjustTimetableParallel(oldMapGroup);
                lectureCon.setLectureStorageGroup(newMapGroup);
                out.println("MAKING_LECTURES_EARLIER_ADMIN");
                break;

            case "MANAGE_STUDENTS":
                out.println("OPENING_MANAGE_STUDENTS_PAGE");
                System.out.println("Opening the manage students page...\n");
                break;

            case "ADD_STUDENT":
                out.println("SEND_STUDENT_DATA");
                String studentData = in.readLine();
                studentCon.addStudent(studentData, out);
                break;

            case "REMOVE_STUDENT":
                out.println("REMOVING_STUDENT");
                String studentId = in.readLine();
                studentCon.removeStudent(studentId, out);
                break;

            case "GET_STUDENTS":
                studentCon.sendStudentNames(out);
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
}
