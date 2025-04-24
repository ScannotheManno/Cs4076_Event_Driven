
package org.openjfx.servergui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ManageStudentController {
    
    private static final String USER_PASSWORD_CSV_PATH = "CSV_Files/User_Password.csv";
    private CSVController csvCon;
    
    public void csvConSetter() {
        csvCon = new CSVController();
    }
    public synchronized void sendStudentNames(PrintWriter out) {
        try {
            List<String[]> data = csvCon.readCSV(USER_PASSWORD_CSV_PATH);
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

    public synchronized void addStudent(String line, PrintWriter out) {
        try {
            String[] parts = line.split(";");
            csvCon.appendLineToCSV(USER_PASSWORD_CSV_PATH, parts);
            out.println(parts[0] + " was added.");
        } catch (IOException e) {
            out.println("ERROR: Could not add student");
        }
    }

    public synchronized void removeStudent(String id, PrintWriter out) {
        try {
            csvCon.removeLineFromCSV(USER_PASSWORD_CSV_PATH, id);
            out.println(id + " Was removed from the database");
        } catch (IOException e) {
            out.println("ERROR: Could not remove student");
        }
    }

    public synchronized List<String> getAllUserIds() {
        List<String> ids = new ArrayList<>();
        try {
            List<String[]> rows = csvCon.readCSV(USER_PASSWORD_CSV_PATH);
            for (String[] row : rows) {
                if (row.length > 0 && !row[0].trim().isEmpty()) {
                    ids.add(row[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ids;
    }    
}
