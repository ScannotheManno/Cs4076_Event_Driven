package org.openjfx._23381272_Server;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CSVController {

    public static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        Path pathToFile = Paths.get(filePath);

        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        }
        return data;
    }

    public static void overwriteCSV(String filePath, List<String[]> data) throws IOException {
        Path pathToFile = Paths.get(filePath);

        try (BufferedWriter bw = Files.newBufferedWriter(pathToFile)) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }

    public static void appendLineToCSV(String filePath, String[] row) throws IOException {
        Path pathToFile = Paths.get(filePath);

        try (BufferedWriter bw = Files.newBufferedWriter(pathToFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(String.join(",", row));
            bw.newLine();
        }
    }

    public static void removeLineFromCSV(String filePath, String keyWord) throws IOException {
        List<String[]> data = readCSV(filePath);

        if (data.isEmpty()) {
            System.out.println("CSV is empty.");
            return;
        }

        String[] header = data.get(0);
        boolean removed = false;

        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).length > 0 && data.get(i)[0].equalsIgnoreCase(keyWord)) {
                data.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            overwriteCSV(filePath, data);
        } else {
            System.out.println("No matching row found for '" + keyWord + "'.");
        }
    }
}
