package org.openjfx._23381272_Server;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

public class LectureController {
    private static Map<String, String> lectureStorageGroup = new HashMap<>();
    private static final Map<String, String> lectureStoragePersonal = new HashMap<>();
    private static ArrayList<String> timetableSpacesGroup = new ArrayList<>();
    private static final ArrayList<String> timetableSpacesPersonal = new ArrayList<>();
    private static ArrayList<String> studentAvailibilityGroup = new ArrayList<>();
    private static final ArrayList<String> studentAvailibilityPersonal = new ArrayList<>();
    private static final String GROUPTIMETABLE_CSV_PATH = "CSV_Files/GroupTimetable.csv";
    private static final String LECTURECHECKS_CSV_PATH = "CSV_Files/LectureChecks.csv";


    public static void loadCSVData() {
        try {
            List<String[]> csvData = new ArrayList<>();
            lectureStorageGroup = CSVController.csvToMap(GROUPTIMETABLE_CSV_PATH);
            csvData = CSVController.readCSV(LECTURECHECKS_CSV_PATH);
            for (String[] row : csvData) {
                timetableSpacesGroup.add(row[0]);
                studentAvailibilityGroup.add(row[1]);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV files");
        }
    }

    public static void handleAddLectureStudent(String lectureData, PrintWriter out) {
        String[] module = lectureData.split("@");
        if (module.length == 7) {
            String key = module[0] + "_" + module[3] + "_" + module[2] + "_" + module[4] + "_" + module[5];
            String detail = lectureData;
            String slot = module[2] + "_" + module[4] + "_" + module[5];
            String studentSlot = module[4] + "_" + module[5];
            
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
            out.println("ERROR: Invalid format");
        }
    }
    
    public static void handleAddLectureAdmin(String lectureData, PrintWriter out) {
        String[] module = lectureData.split("@");
        if (module.length == 7) {
            String key = module[0] + "_" + module[3] + "_" + module[2] + "_" + module[4] + "_" + module[5];
            String detail = lectureData;
            String slot = module[2] + "_" + module[4] + "_" + module[5];
            String studentSlot = module[4] + "_" + module[5];
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

                String[] csvLectureChecks = {slot, studentSlot};
                try {
                CSVController.appendLineToCSV(LECTURECHECKS_CSV_PATH, csvLectureChecks);
                } catch (IOException e) {

                }

                if ("2".equals(module[6])) {
                    int time = Integer.parseInt(module[5].split(":" )[0]) + 1;
                    String extraTime = String.format("%02d:00", time);
                    String slot2 = module[2] + "_" + module[4] + "_" + extraTime;
                    String studentSlot2 = module[4] + "_" + extraTime;
                    timetableSpacesGroup.add(slot2);
                    studentAvailibilityGroup.add(studentSlot2);
                    String[] csvLectureChecks2 = {slot2, studentSlot2};
                try {
                CSVController.appendLineToCSV(LECTURECHECKS_CSV_PATH, csvLectureChecks2);
                } catch (IOException e) {

                }
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
        } else {
            
        } 
    }

    public static void handleRemoveLectureStudent(String key, PrintWriter out) {
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
    }
    
    public static void handleRemoveLectureAdmin(String key, PrintWriter out) {
        if (lectureStorageGroup.containsKey(key)) {
            String[] details = lectureStorageGroup.get(key).split("@");
            String slot = details[2] + "_" + details[4] + "_" + details[5];
            String studentSlot = details[4] + "_" + details[5];

            try {
                CSVController.removeLineFromCSV(LECTURECHECKS_CSV_PATH, slot);
                timetableSpacesGroup.remove(slot);
                studentAvailibilityGroup.remove(studentSlot);
            } catch (IOException e) {
                System.out.println("Could not remove from csv");
            }
            if ("2".equals(details[6])) {
                int time = Integer.parseInt(details[5].split(":" )[0]) + 1;
                String extraTime = String.format("%02d:00", time);
                String slot2 = details[2] + "_" + details[4] + "_" + extraTime;
                String studentSlot2 = details[4] + "_" + extraTime;
                try {
                    CSVController.removeLineFromCSV(LECTURECHECKS_CSV_PATH, slot2);
                    timetableSpacesGroup.remove(slot2);
                    studentAvailibilityGroup.remove(studentSlot2);
                } catch (IOException e) {
                    System.out.println("Could not remove from csv");
                }
            }
            
            try {
                CSVController.removeLineFromCSV(GROUPTIMETABLE_CSV_PATH, key);
                lectureStorageGroup.remove(key);
                out.println("Lecture Removed Successfully!");
            } catch (IOException e) {
            System.out.println("Could not remove from csv");
            }
        } else {
            out.println("ERROR: Lecture not found.");
        }
        
    }

    public void sendLectureKeysStudent(PrintWriter out) {
        if (lectureStoragePersonal.isEmpty()) {
            out.println("NO_LECTURES_AVAILABLE");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String key : lectureStoragePersonal.keySet()) {
            sb.append(key).append(";");
        }
        out.println(sb.toString());
    }
    
    public void sendLectureKeysAdmin(PrintWriter out) {
        if (lectureStorageGroup.isEmpty()) {
            out.println("NO_LECTURES_AVAILABLE");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String key : lectureStorageGroup.keySet()) {
            sb.append(key).append(";");
        }
        out.println(sb.toString());
    }

    public void handleSendLectureDetailsGroup(PrintWriter out) {
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

    public void handleSendLectureDetailsPersonal(PrintWriter out) {
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
