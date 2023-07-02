/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author josga
 */
public class ProcessManager extends SyscallBase {

    public ProcessManager(String mensaje, String outputPath) {
        super(mensaje, outputPath);
    }

    public ProcessManager() {
    }

    private String getCommandTasklist() {
        return "tasklist /v";
    }

    private String getCommandTasklistCsv() {
        return "tasklist /v /fo csv";
    }

    private String getCommandTaskKillCsvPID(String id) {
        return "taskkill /PID " + id + " /f";
    }

    private String getCommandTaskKillCsvName(String name) {
        return "taskkill /IM " + name + " /f";
    }

    public String executeTaskList() throws FileNotFoundException, IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(getCommandTasklistCsv());
        String str = result(process);

        return str;
    }

    public String executeTaskKillId(String id) throws FileNotFoundException, IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(getCommandTaskKillCsvPID(id));
        String str = result(process);
        String arr[] = str.split(" el");
        return arr[1];
    }

    public String executeTaskKillName(String name) throws FileNotFoundException, IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(getCommandTaskKillCsvName(name));
        String str = result(process);
        String arr[] = str.split(" el");
        return arr[1];
    }

    public Map<String, ArrayList<String[]>> createMap(String csvData) {
        Map<String, ArrayList<String[]>> dataMap = new HashMap<>();

        String[] lines = csvData.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] data = lines[i].split(",");
            String imageName = data[0];

            if (!dataMap.containsKey(imageName)) {
                dataMap.put(imageName, new ArrayList<>());
            }
            dataMap.get(imageName).add(data);
        }
        return dataMap;
    }
    
    public String[][] parseCSV(String csvString) {
        String[] lines = csvString.split("\n");
        int numRows = lines.length;
        int numCols = lines[0].split(",").length;

        String[][] matrix = new String[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String[] columns = lines[i].split(",");
            matrix[i] = columns;
        }

        return matrix;
    }

    public String[][] parseCSV2(String csvString) {
        ArrayList<String[]> rows = new ArrayList<>();
        Scanner sc = new Scanner(csvString);
        sc.useDelimiter("\n");

        while (sc.hasNext()) {
            String line = sc.next().trim();
            ArrayList<String> fields = new ArrayList<>();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(",");

            while (lineScanner.hasNext()) {
                String field = lineScanner.next().trim();

                // Eliminar las comillas alrededor del campo
                if (field.startsWith("\"") && field.endsWith("\"")) {
                    field = field.substring(1, field.length() - 1);
                }

                fields.add(field);
            }

            lineScanner.close();

            rows.add(fields.toArray(new String[0]));
        }

        sc.close();

        return rows.toArray(new String[0][]);
    }
}
