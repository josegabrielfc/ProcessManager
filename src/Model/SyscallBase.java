/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josga
 */
public class SyscallBase { // Comando tasklist genera csv

    private String message;
    private String outputPath;
    private String buffer = "";

    public SyscallBase(String mensaje, String outputPath) {
        this.message = mensaje;
        this.outputPath = outputPath;
        //this.buffer = buffer;
    }

    public SyscallBase() {
    }

    public String getMessage() {
        return message;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void processNotepad() throws IOException {
        Process processBlocDeNotas = Runtime.getRuntime().exec(new String[]{"notepad.exe", getOutputPath()});
        try {
            processBlocDeNotas.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtener el resultado del comando
     *
     * @return el resultado
     * @throws UnsupportedEncodingException
     */
    public String result(Process process) {

        if (process == null) {
            System.err.print("Error, ningun proceso se ha ejecutado y no hay resultados.");
            return "Error";
        }
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;

            // reading the output
            while ((line = br.readLine()) != null) {
                buffer += line + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(SyscallBase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Closing the BufferedReader
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(SyscallBase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return buffer;
    }

    /**
     * Obtener el resultado cuando se ejecuto el comando
     *
     * @return
     */
    public String getBuffer() {
        return buffer;
    }

    /**
     * Limpiar el resultado del comando ejecutado
     */
    public void cleanBuffer() {
        this.buffer = "";
    }
}
