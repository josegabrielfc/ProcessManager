/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.ProcessManager;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author josga
 */
public class SyscallController {

    private String csvData;
    Map<String, ArrayList<String[]>> mapData;
    private ProcessManager p = new ProcessManager();

    public SyscallController() {
    }

    public String getCsvData() {
        return csvData;
    }

    public Map<String, ArrayList<String[]>> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, ArrayList<String[]>> mapData) {
        this.mapData = mapData;
    }

    /**
     *
     * @param csvData
     * @return un Hashmap que tan solo tiene la información de la cpu y la ram
     * El value es un Array, donde la posicion 0 de ese arreglo estara la ram y
     * la posicion 1 la cpu
     */
    public Map<String, ArrayList<String[]>> mapCpuRam(String csvData) {
        Map<String, ArrayList<String[]>> dataMap = new HashMap<>();

        String[] lines = csvData.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] data = lines[i].split(",");
            String key = data[0];
            String value1 = data[4];//4 - ram
            String value2 = data[7];//7 - cpu

            if (!dataMap.containsKey(key)) {
                dataMap.put(key, new ArrayList<>());
            }

            String[] values = {value1, value2};
            dataMap.get(key).add(values);
        }

        return dataMap;
    }

    /**
     * Hilo que rellena la tabla cada cierto tiempo, para actualizar los
     * procesos
     *
     * @param model
     * @throws IOException
     */
    public void threadTaskList(DefaultTableModel model) throws IOException {
        Thread thread = new Thread(() -> {
            while (true) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        generateTaskList(model);
                    } catch (IOException | InterruptedException ex) {
                        java.util.logging.Logger.getLogger(SyscallController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                try {
                    // Tiempo en milisegundos para las iteraciones posteriores (70 seg) 
                    Thread.sleep(70000); // Dado que se demora 20 seg en cargar la tabla y 50 para que se vuelva a recargar
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(SyscallController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    /**
     * Metodo que genera la lista de procesos en un String pero con formato csv
     * y rellena el JTable
     *
     * @param model
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public void generateTaskList(DefaultTableModel model) throws IOException, FileNotFoundException, InterruptedException {
        String csvString = p.executeTaskList();
        csvString = csvString.replace("\"", "");
        this.csvData = csvString;
        fillJTable(model, csvString);
    }

    private void fillJTable(DefaultTableModel model, String csvString) {
        String[] rows = csvString.split("\n");
        boolean isFirstRow = true;

        for (String row : rows) {
            if (isFirstRow) {
                isFirstRow = false;
                continue; // Ignorar la primera fila
            }
            String[] data = row.split(",");
            String name = data[0];
            String pid = data[1];
            String ram = data[4];
            String cpu = data[7];
            Object[] rowData = {name, pid, ram, cpu};
            model.addRow(rowData);
        }
    }

    private double parseDoubleWithLastDot(String value) {
        int lastDotIndex = value.lastIndexOf(".");
        String parsedValue = value.substring(0, lastDotIndex).replaceAll("\\.", "") + value.substring(lastDotIndex);
        return Double.parseDouble(parsedValue);
    }

    /**
     *
     * @param dataMap
     * @return Un objecto Dataset para poder generar el diagrama circular, y
     * este metodo necesita de mapCpuRam(dataMap), para tener el hashmap el cual
     * cuando este en valor, tenga solo en cuenta la posicion 0 del arreglo de
     * values, de acuerdo a la key
     */
    public DefaultPieDataset createPieChartRam(Map<String, ArrayList<String[]>> dataMap) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map.Entry<String, ArrayList<String[]>> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String[]> valuesList = entry.getValue();

            if (!valuesList.isEmpty()) {
                String[] firstRow = valuesList.get(0);
                String str = firstRow[0];
                String[] value = str.split(" ");
                String num = value[0];
                double parsedValue;
                try {
                    parsedValue = Double.parseDouble(num);
                } catch (NumberFormatException e) {
                    parsedValue = parseDoubleWithLastDot(num);
                }
                dataset.setValue(key, parsedValue);
            }
        }
        return dataset;
    }

    /**
     *
     * @param dataMap
     * @return Un objecto Dataset para poder generar el diagrama circular, y
     * este metodo necesita de mapCpuRam(dataMap), para tener el hashmap el cual
     * cuando este en valor, tenga solo en cuenta la posicion 1 del arreglo de
     * values, de acuerdo a la key
     */
    public DefaultPieDataset createPieChartCpu(Map<String, ArrayList<String[]>> dataMap) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map.Entry<String, ArrayList<String[]>> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String[]> valuesList = entry.getValue();

            if (!valuesList.isEmpty()) {
                String[] firstRow = valuesList.get(0);
                String str = firstRow[1];
                int value = convertHourToNumber(str);
                dataset.setValue(key, value);
            }
        }
    
    return dataset ;
}

private int convertHourToNumber(String hour) {
        String[] partes = hour.trim().replaceAll("\r", "").split(":");

        int hours = Integer.parseInt(partes[0]);
        int minutes = Integer.parseInt(partes[1]);
        int seconds = Integer.parseInt(partes[2]);

        int totalSegundos = hours * 3600 + minutes * 60 + seconds;
        //double valorDouble = (double) totalSegundos / 3600;

        return totalSegundos;
    }

    /*public static void main(String[] args) throws IOException, FileNotFoundException, InterruptedException {
        //String kill = p.executeTaskList();
        //System.out.println(kill);
        //String csvString = p.executeTaskList();
        //csvString = csvString.replace("\"", "");
        //System.out.println(csvString);

        //Map<String, ArrayList<String[]>> dataMap = p.mapCpuRam(csvString);
        //printMap(dataMap);

        //System.out.println(csvString);
        //String csvFilePath = "src/Util/datos.csv";
        //generateCsv(csvString,csvFilePath);

        //String[][] matrix = p.parseCSV(csvString);
        //String[][] matrix2 = p.parseCSV2(csvString);
        //String test = printMatrix(matrix);
        //System.out.println(test);
        //printMatrix(matrix2);
    }*/
    private static void printMap(Map<String, ArrayList<String[]>> dataMap) {
        for (Map.Entry<String, ArrayList<String[]>> entry : dataMap.entrySet()) {
            String imageName = entry.getKey();
            ArrayList<String[]> rows = entry.getValue();

            System.out.println(imageName + "{");
            for (String[] row : rows) {
                System.out.print("\t");
                for (int i = 0; i < row.length; i++) {
                    System.out.print(row[i]);
                    if (i < row.length - 1) {
                        System.out.print(",");
                    }
                }
                System.out.println();
            }
            System.out.println("}");
            System.out.println();
        }
    }

    private static String printMatrix(String[][] matrix) {
        // Imprimir la matriz
        String str = "";
        for (String[] row : matrix) {
            String aux = Arrays.toString(row);
            str += (aux + "\n");
            //System.out.println(aux);
        }
        return str;
    }

    private static void printMatrix2(String[][] matrix) {
        for (String[] row : matrix) {
            for (String element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    private static void generateCsv(String csvString, String csvFilePath) {
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, StandardCharsets.UTF_8))) {
            writer.write(csvString);
            System.out.println("Archivo CSV generado correctamente.");
        } catch (IOException e) {
            System.out.println("Ocurrió un error al generar el archivo CSV: " + e.getMessage());
        }
    }
}
