
package org.openjfx.servergui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author lukes
 */
public class ManageStudentController {
    
    private static final String USER_PASSWORD_CSV_PATH = "CSV_Files/User_Password.csv";

    
    public void sendStudentNames(PrintWriter out) {
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

    public void addStudent(String line, PrintWriter out) {
        try {
            String[] parts = line.split(";");
            CSVController.appendLineToCSV(USER_PASSWORD_CSV_PATH, parts);
            out.println(parts[0] + " was added.");
        } catch (IOException e) {
            out.println("ERROR: Could not add student");
        }
    }

    public void removeStudent(String id, PrintWriter out) {
        try {
            CSVController.removeLineFromCSV(USER_PASSWORD_CSV_PATH, id);
            out.println(id + " Was removed from the database");
        } catch (IOException e) {
            out.println("ERROR: Could not remove student");
        }
    }    
}
