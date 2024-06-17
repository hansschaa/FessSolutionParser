package com.parser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class Parser {

    public static void main(String[] args) {
        String filePath = "solutions.sok";
        String excelFilePath = "data.xlsx";
        List<BoardSolution> boardsAndSolutions = ParseSokobanSolutionFile(filePath);
        WriteBoardsAndSolutionsToExcel(excelFilePath, boardsAndSolutions);
    }

    public static List<BoardSolution> ParseSokobanSolutionFile(String filePath) {
        List<BoardSolution> boardsAndSolutions = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            reader.close();
            String[] parts = content.toString().split("Solution\n");

            for (int i = 1; i < parts.length; i++) {
                String boardPart = parts[i - 1].split("\n\n")[parts[i - 1].trim().split("\n\n").length - 1];
                String solutionPart = parts[i].trim().split("\n")[0];

                // Extract solver time
                String solverTime = "";

                if (parts[i].contains("Solver time:")) {
                    solverTime = parts[i].substring(parts[i].indexOf("Solver time:") + 12, parts[i].indexOf("Solver date:")).trim();
                }

                // Clean up the board
                String[] boardLines = boardPart.split("\n");
                StringBuilder board = new StringBuilder();
                for (String boardLine : boardLines) {
                    if (!boardLine.startsWith("Author") && !boardLine.startsWith("Solver")) {
                        board.append(boardLine).append("\n");
                    }
                }

                boardsAndSolutions.add(new BoardSolution(board.toString(), solutionPart, ConvertSolverTimeToSeconds(solverTime)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return boardsAndSolutions;
    }

    public static void WriteBoardsAndSolutionsToExcel(String excelFilePath, List<BoardSolution> boardsAndSolutions) {
        FileInputStream fis = null;
        Workbook workbook = null;
        boolean fileExists = new File(excelFilePath).exists();

        try {
            if (fileExists) {
                fis = new FileInputStream(excelFilePath);
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.getSheet("Boards and Solutions");
            if (sheet == null) {
                sheet = workbook.createSheet("Boards and Solutions");

                // Create header row if the sheet is newly created
                Row headerRow = sheet.createRow(0);
                Cell headerCell1 = headerRow.createCell(0);
                headerCell1.setCellValue("Board");
                Cell headerCell2 = headerRow.createCell(1);
                headerCell2.setCellValue("Solution");
                Cell headerCell3 = headerRow.createCell(2);
                headerCell3.setCellValue("Uppercase Count");
                Cell headerCell4 = headerRow.createCell(3);
                headerCell4.setCellValue("Solver Time (seconds)");
            }

            // Find the last row number
            int lastRowNum = sheet.getLastRowNum();

            // Fill data rows
            for (int i = 0; i < boardsAndSolutions.size(); i++) {
                BoardSolution boardSolution = boardsAndSolutions.get(i);
                Row row = sheet.createRow(lastRowNum + 1 + i);

                Cell boardCell = row.createCell(0);
                boardCell.setCellValue(boardSolution.getBoard());

                Cell solutionCell = row.createCell(1);
                solutionCell.setCellValue(boardSolution.getSolution());

                Cell uppercaseCountCell = row.createCell(2);
                uppercaseCountCell.setCellValue(CountUppercaseLetters(boardSolution.getSolution()));

                Cell timeCell = row.createCell(3);
                timeCell.setCellValue(boardSolution.getSolverTime());
            }

            // Write the output to the file
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Data written to Excel file successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int CountUppercaseLetters(String solution) {
        int count = 0;
        for (char c : solution.toCharArray()) {
            if (Character.isUpperCase(c)) {
                count++;
            }
        }
        return count;
    }

    public static int ConvertSolverTimeToSeconds(String solverTime) {
        int seconds = 0;
        // Assume solverTime is in format HH:mm:ss
        String[] parts = solverTime.split(":");
        if (parts.length == 3) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int secs = Integer.parseInt(parts[2]);
            seconds = hours * 3600 + minutes * 60 + secs;
        }
        return seconds;
    }
}

class BoardSolution {
    private final String board;
    private final String solution;
    private final int solverTimeInSeconds;

    public BoardSolution(String board, String solution, int solverTimeInSeconds) {
        this.board = board;
        this.solution = solution;
        this.solverTimeInSeconds = solverTimeInSeconds;
    }

    public String getBoard() {
        return board;
    }

    public String getSolution() {
        return solution;
    }

    public int getSolverTime() {
        return solverTimeInSeconds;
    }
}
