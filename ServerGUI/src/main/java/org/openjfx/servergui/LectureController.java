package org.openjfx.servergui;

import java.io.*;
import java.util.*;

public class LectureController {
    private Map<String, String> lectureStorageGroup = new HashMap<>();
    private Map<String, String> lectureStoragePersonal = new HashMap<>();
    private ArrayList<String> timetableSpacesGroup = new ArrayList<>();
    private ArrayList<String> timetableSpacesPersonal = new ArrayList<>();
    private ArrayList<String> studentAvailibilityGroup = new ArrayList<>();
    private ArrayList<String> studentAvailibilityPersonal = new ArrayList<>();
    private String GROUPTIMETABLE_CSV_PATH = "CSV_Files/GroupTimetable.csv";
    private String LECTURECHECKS_CSV_PATH = "CSV_Files/LectureChecks.csv";
    


    public void loadCSVData() {
        try {
            List<String[]> csvData;
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
    
    public Map<String, String> getLectureStorageGroup() {
        return lectureStorageGroup;
    }
    
    public Map<String, String> getLectureStoragePersonal() {
        return lectureStoragePersonal;
    }
    
    public void setLectureStorageGroup(Map<String, String> map) {
        this.lectureStorageGroup = map;
        rebuildGroupSlots();
        rewriteToCSV(GROUPTIMETABLE_CSV_PATH, LECTURECHECKS_CSV_PATH, lectureStorageGroup, timetableSpacesGroup, studentAvailibilityGroup);
    }
    
    public void setLectureStoragePersonal(Map<String, String> map) {
        this.lectureStoragePersonal = map;
        rebuildPersonalSlots();
    }

    public void handleAddLectureStudent(String lectureData, PrintWriter out) {
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
    
    public void handleAddLectureAdmin(String lectureData, PrintWriter out) {
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
                    } catch (IOException e) {
                        System.out.println("Could not save to csv");
                    }    
                }
            }
        } else {
            
        } 
    }

    public void handleRemoveLectureStudent(String key, PrintWriter out) {
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
    
    public void handleRemoveLectureAdmin(String key, PrintWriter out) {
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
    
    private void rebuildGroupSlots() {
        timetableSpacesGroup.clear();
        studentAvailibilityGroup.clear();
        for (String detail : lectureStorageGroup.values()) {
            String[] m = detail.split("@");
            String slot = m[2] + "_" + m[4] + "_" + m[5];
            timetableSpacesGroup.add(slot);
            studentAvailibilityGroup.add(m[4] + "_" + m[5]);

            if ("2".equals(m[6])) {
                int h = Integer.parseInt(m[5].split(":")[0]) + 1;
                String extra = String.format("%02d:00", h);
                timetableSpacesGroup.add(m[2] + "_" + m[4] + "_" + extra);
                studentAvailibilityGroup.add(m[4] + "_" + extra);
            }
        }
    }
    
    private void rebuildPersonalSlots() {
        timetableSpacesPersonal.clear();
        studentAvailibilityPersonal.clear();
        for (String detail : lectureStoragePersonal.values()) {
            String[] m = detail.split("@");
            String slot = m[2] + "_" + m[4] + "_" + m[5];
            timetableSpacesPersonal.add(slot);
            studentAvailibilityPersonal.add(m[4] + "_" + m[5]);

            if ("2".equals(m[6])) {
                int h = Integer.parseInt(m[5].split(":")[0]) + 1;
                String extra = String.format("%02d:00", h);
                timetableSpacesPersonal.add(m[2] + "_" + m[4] + "_" + extra);
                studentAvailibilityPersonal.add(m[4] + "_" + extra);
            }
        }
    }
    
    private void rewriteToCSV(String filepath1, String filepath2, Map<String, String> map, ArrayList<String> a1, ArrayList<String> a2) {
        try {
            // 1) clear & rewrite the group‑timetable CSV
            CSVController.clearCSV(filepath1);
            for (var entry : map.entrySet()) {
                String key   = entry.getKey();
                String value = entry.getValue();
                CSVController.appendLineToCSV(filepath1, new String[]{ key, value });
            }

            // 2) clear & rewrite the lecture‑checks CSV
            CSVController.clearCSV(filepath2);
            for (int i = 0; i < a1.size(); i++) {
                CSVController.appendLineToCSV(filepath2, new String[]{a1.get(i), a2.get(i)});
            }
        } catch (IOException e) {
            // log or rethrow as unchecked if you prefer
            System.err.println("Error persisting personal timetable to CSV: " + e);
        }
    }
}
