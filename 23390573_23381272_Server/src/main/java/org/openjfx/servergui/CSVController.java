package org.openjfx.servergui;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CSVController {
    private static final Object lock = new Object();

    public List<String[]> readCSV(String filePath) throws IOException {
        synchronized(lock) {
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
    }

    public void appendLineToCSV(String filePath, String[] row) throws IOException {
        synchronized(lock) {
            Path pathToFile = Paths.get(filePath);

            try (BufferedWriter bw = Files.newBufferedWriter(pathToFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }
    
    public static void clearCSV(String filePath) {
        synchronized(lock) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                // Read the header line
                String header = reader.readLine();

                if (header != null) {
                    // Overwrite the file with only the header
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                        writer.write(header);
                        writer.newLine(); // Optional: adds a new line after the header
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeLineFromCSV(String filePath, String keyWord) throws IOException {
        synchronized(lock) {
            List<String> lines = new ArrayList<>();
            boolean removed = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        lines.add(line); // Always keep the header
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",", -1); // Use -1 to handle empty columns
                    if (!removed && parts.length > 0 && parts[0].equals(keyWord)) {
                        removed = true; // Skip this line
                    } else {
                        lines.add(line); // Keep this line
                    }
                }
            }

            if (removed) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                }
                System.out.println("Line with key '" + keyWord + "' was removed.");
            } else {
                System.out.println("No matching row found for '" + keyWord + "'.");
            }
        }
    }

    public HashMap<String, String> csvToMap(String filePath) throws IOException {
        synchronized(lock) {
            List<String[]> rows = readCSV(filePath);
            HashMap<String, String> map = new HashMap<>();

            for (String[] row : rows) {
                if (row.length >= 2) {
                    map.put(row[0], row[1]);
                }
            }

            return map;
 
        }
    }
}
