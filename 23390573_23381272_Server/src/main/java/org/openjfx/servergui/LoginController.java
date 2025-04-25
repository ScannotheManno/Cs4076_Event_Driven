
package org.openjfx.servergui;

import java.io.*;
import java.util.*;


public class LoginController {
    
    private static final String USER_PASSWORD_CSV_PATH = "CSV_Files/User_Password.csv";
    private String userType;
    private String userID;
    private CSVController csvCon;
    
    public String getUserType() {
        return userType;
    }
    
    public String getUserID() {
        return userID;
    }
    
    public boolean authenticate(String studentId, String password) {
        csvCon = new CSVController();
        try {
            List<String[]> csvData = csvCon.readCSV(USER_PASSWORD_CSV_PATH);
            for (String[] userData : csvData) {
                if (userData.length == 3) {
                    if (userData[0].equals(studentId) && userData[1].equals(password)) {
                        userType = userData[2].trim();
                        userID = userData[0].trim();
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV");
        }
        return false;
    }
    
}
