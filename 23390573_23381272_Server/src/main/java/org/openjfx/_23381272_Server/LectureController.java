package org.openjfx._23381272_Server;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

public class LectureController {
    private static final Map<String, String> lectureStorageGroup = new HashMap<>();
    private static final Map<String, String> lectureStoragePersonal = new HashMap<>();
    private static final ArrayList<String> timetableSpacesGroup = new ArrayList<>();
    private static final ArrayList<String> timetableSpacesPersonal = new ArrayList<>();
    private static final ArrayList<String> studentAvailibilityGroup = new ArrayList<>();
    private static final ArrayList<String> studentAvailibilityPersonal = new ArrayList<>();
    private static final String GROUPTIMETABLE_CSV_PATH = "CSV_Files/GroupTimetable.csv";

    public static Map<String, String> getLectureStorageGroup() {
        return lectureStorageGroup;
    }

    public static Map<String, String> getLectureStoragePersonal() {
        return lectureStoragePersonal;
    }

    public static void handleAddLecture(String userType, String lectureData, PrintWriter out) {
        String[] module = lectureData.split("@");
        if (module.length != 7) {
            out.println("ERROR: Invalid format");
            return;
        }

        String key = module[0] + "_" + module[3] + "_" + module[2] + "_" + module[4] + "_" + module[5];
        String detail = lectureData;
        String slot = module[2] + "_" + module[4] + "_" + module[5];
        String studentSlot = module[4] + "_" + module[5];

        boolean isStudent = userType.equals("Student");
        Map<String, String> lectureStorage = isStudent ? lectureStoragePersonal : lectureStorageGroup;
        ArrayList<String> timetableSpaces = isStudent ? timetableSpacesPersonal : timetableSpacesGroup;
        ArrayList<String> studentAvailability = isStudent ? studentAvailibilityPersonal : studentAvailibilityGroup;

        if (lectureStorage.containsKey(key)) {
            out.println("Error: Lecture already exists.");
        } else if (timetableSpaces.contains(slot)) {
            out.println("Error: Room unavailable at this time.");
        } else if (studentAvailability.contains(studentSlot)) {
            out.println("Error: Student unavailable at this time.");
        } else {
            lectureStorage.put(key, detail);
            timetableSpaces.add(slot);
            studentAvailability.add(studentSlot);

            if ("2".equals(module[6])) {
                int time = Integer.parseInt(module[5].split(":")[0]) + 1;
                String extraTime = String.format("%02d:00", time);
                timetableSpaces.add(module[2] + "_" + module[4] + "_" + extraTime);
                studentAvailability.add(module[4] + "_" + extraTime);
            }

            out.println("Lecture Added Successfully!");

            if (!isStudent) {
                try {
                    CSVController.clearCSV(GROUPTIMETABLE_CSV_PATH);
                    for (var entry : lectureStorage.entrySet()) {
                        String[] row = {entry.getKey(), entry.getValue()};
                        CSVController.appendLineToCSV(GROUPTIMETABLE_CSV_PATH, row);
                    }
                } catch (IOException e) {
                    System.out.println("Could not save to CSV");
                }
            }
        }
    }

    public static void handleRemoveLecture(String userType, String key, PrintWriter out) {
        boolean isStudent = userType.equals("Student");
        Map<String, String> lectureStorage = isStudent ? lectureStoragePersonal : lectureStorageGroup;
        ArrayList<String> timetableSpaces = isStudent ? timetableSpacesPersonal : timetableSpacesGroup;
        ArrayList<String> studentAvailability = isStudent ? studentAvailibilityPersonal : studentAvailibilityGroup;

        if (!lectureStorage.containsKey(key)) {
            out.println("ERROR: Lecture not found.");
            return;
        }

        String[] details = lectureStorage.get(key).split("@");
        String slot = details[2] + "_" + details[4] + "_" + details[5];
        String studentSlot = details[4] + "_" + details[5];

        timetableSpaces.remove(slot);
        studentAvailability.remove(studentSlot);

        if ("2".equals(details[6])) {
            int time = Integer.parseInt(details[5].split(":")[0]) + 1;
            String extraTime = String.format("%02d:00", time);
            timetableSpaces.remove(details[2] + "_" + details[4] + "_" + extraTime);
            studentAvailability.remove(details[4] + "_" + extraTime);
        }

        lectureStorage.remove(key);

        if (!isStudent) {
            try {
                CSVController.removeLineFromCSV(GROUPTIMETABLE_CSV_PATH, key);
            } catch (IOException e) {
                System.out.println("Could not remove lecture from CSV");
            }
        }

        out.println("Lecture Removed Successfully!");
    }

    public static void sendLectureKeys(String userType, PrintWriter out) {
        Map<String, String> lectureStorage = userType.equals("Student") ? lectureStoragePersonal : lectureStorageGroup;
        if (lectureStorage.isEmpty()) {
            out.println("NO_LECTURES_AVAILABLE");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String key : lectureStorage.keySet()) {
            sb.append(key).append(";");
        }
        out.println(sb.toString());
    }

    public static void handleSendLectureDetailsGroup(PrintWriter out) {
        if (lectureStorageGroup.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String val : lectureStorageGroup.values()) {
            sb.append(val).append(";");
        }
        out.println(sb.toString());
    }

    public static void handleSendLectureDetailsPersonal(PrintWriter out) {
        if (lectureStoragePersonal.isEmpty()) {
            out.println("NO_LECTURES_SCHEDULED");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String val : lectureStoragePersonal.values()) {
            sb.append(val).append(";");
        }
        out.println(sb.toString());
    }
}
