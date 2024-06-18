package com.parser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
    static class Level {
        String map;
        String solution;
        int solutionLength;
        int uppercaseCount;
        String time;

        Level(String map, String time, String solution) {
            this.map = map;
            this.solution = solution;
            this.solutionLength = solution.length();
            this.uppercaseCount = countUppercaseLetters(solution);
            this.time = time;
        }

        private int countUppercaseLetters(String solution) {
            int count = 0;
            for (char c : solution.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    count++;
                }
            }
            return count;
        }
    }

    public static void main(String[] args) {
        String inputFilePath = "solutions2.sok"; // Ruta del archivo de entrada
        String outputFilePath = "results.xlsx"; // Ruta del archivo de salida

        List<Level> levels = parseInputFile(inputFilePath);

        try {
            exportToExcel(levels, outputFilePath);
            System.out.println("Archivo Excel generado correctamente en: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Level> parseInputFile(String filePath) {
        List<Level> levels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String content1 = sb.toString();
            
            //Pre
            String content = procesarLineas(content1);
            
            String[] parts = content.split("Level");

            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                int solutionIndex = part.indexOf("Solution");
                String map;
                String time = "";
                String solution = "";

                if (solutionIndex != -1) {
                    map = removeFirstTwoLines(part.substring(0, solutionIndex));
                    solution = part.substring(solutionIndex + "Solution".length()).trim();
                    
                    
                if (part.contains("Solution")) {
                    if (part.contains("Solver time:")) {
                        time = part.substring(part.indexOf("Solver time:") + 12, part.indexOf("Solver date:")).trim();
                    }
                    System.out.println("");
                }
                    
                    
                    
                } else {
                    map = removeFirstTwoLines(part);
                }
                
                

                levels.add(new Level(map, time, solution));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return levels;
    }

    private static void exportToExcel(List<Level> levels, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Hoja para los niveles con solución
        Sheet sheet1 = workbook.createSheet("Niveles con Solución");

        // Definir encabezados de columna para niveles con solución
        Row headerRow1 = sheet1.createRow(0);
        headerRow1.createCell(0).setCellValue("Mapa");
        headerRow1.createCell(1).setCellValue("Solución");
        headerRow1.createCell(2).setCellValue("Longitud de la Solución");
        headerRow1.createCell(3).setCellValue("Cantidad de Mayúsculas");
        headerRow1.createCell(3).setCellValue("Tiempo");

        int rowNumSheet1 = 1; // Empezar en la segunda fila después del encabezado

        for (Level level : levels) {
            if (!level.solution.isEmpty()) {
                Row row = sheet1.createRow(rowNumSheet1++);
                Cell cell = row.createCell(0);
                cell.setCellValue(level.map);

                cell = row.createCell(1);
                if(level.solution.length() >=32767 )
                    level.solution = "a";
                cell.setCellValue(level.solution);

                cell = row.createCell(2);
                cell.setCellValue(level.solutionLength);

                cell = row.createCell(3);
                cell.setCellValue(level.uppercaseCount);
                
                cell = row.createCell(4);
                cell.setCellValue(level.time);
            }
        }

        // Hoja para los niveles sin solución
        Sheet sheet2 = workbook.createSheet("Niveles sin Solución");

        // Definir encabezados de columna para niveles sin solución
        Row headerRow2 = sheet2.createRow(0);
        headerRow2.createCell(0).setCellValue("Mapa");

        int rowNumSheet2 = 1; // Empezar en la segunda fila después del encabezado

        for (Level level : levels) {
            if (level.solution.isEmpty()) {
                Row row = sheet2.createRow(rowNumSheet2++);
                Cell cell = row.createCell(0);
                cell.setCellValue(removeLastFourLines(level.map));
            }
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    private static String removeLastFourLines(String levelMap) {
        String[] lines = levelMap.split("\\r?\\n");

        // Verificar si hay menos de 4 líneas
        if (lines.length <= 4) {
            return "";
        }

        // Crear un nuevo array de líneas sin las últimas 4 líneas
        String[] newLines = new String[lines.length - 4];
        System.arraycopy(lines, 0, newLines, 0, lines.length - 4);

        // Unir las líneas restantes en un nuevo String
        StringBuilder sb = new StringBuilder();
        for (String line : newLines) {
            sb.append(line).append(System.lineSeparator());
        }

        return sb.toString();
    }

    private static String removeFirstTwoLines(String levelMap) {
        String[] lines = levelMap.split("\\r?\\n");

        // Verificar si hay menos de 2 líneas
        if (lines.length <= 2) {
            return "";
        }

        // Crear un nuevo array de líneas sin las primeras 2 líneas
        String[] newLines = new String[lines.length - 2];
        System.arraycopy(lines, 2, newLines, 0, lines.length - 2);

        // Unir las líneas restantes en un nuevo String
        StringBuilder sb = new StringBuilder();
        for (String line : newLines) {
            sb.append(line).append(System.lineSeparator());
        }

        return sb.toString();
    }
    
    public static String procesarLineas(String texto) {
        // Dividir el texto en líneas usando el separador \n
        String[] lineas = texto.split("\n");

        // Inicializar el índice para recorrer el array de líneas
        int indice = 0;
        int aux;
        // Iterar mientras haya líneas por procesar
        while (indice < lineas.length) {
            String linea = lineas[indice];
            // Aquí puedes realizar cualquier operación con cada línea
            //System.out.println("Línea procesada: " + linea);
            
            if(linea.contains("Solver date:")){
                aux = indice+3;
                if(aux < lineas.length)
                    lineas[aux] = "Level";
            }
            
            // Incrementar el índice para pasar a la siguiente línea
            indice++;
        }
        
        StringBuilder sb = new StringBuilder();

        // Iterar sobre cada línea y agregarla al StringBuilder
        for (String linea : lineas) {
            sb.append(linea).append("\n");
        }

        // Eliminar el último salto de línea si es necesario
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Eliminar el último "\n"
        }

        // Devolver el string combinado
        return sb.toString();
    }
}
