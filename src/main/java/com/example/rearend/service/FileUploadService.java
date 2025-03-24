package com.example.rearend.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileUploadService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public void handleFileUpload(MultipartFile file, String uploadType) throws IOException {
        List<String[]> dataRows = parseFile(file);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            saveToDatabase(conn, dataRows, uploadType);
        } catch (SQLException e) {
            throw new IOException("数据库操作失败: " + e.getMessage());
        }
    }

    private List<String[]> parseFile(MultipartFile file) throws IOException {
        List<String[]> dataRows = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过标题行

                String[] rowData = new String[3];
                rowData[0] = getCellValue(row, 0); // 姓名
                rowData[1] = getCellValue(row, 1); // 年龄
                rowData[2] = getCellValue(row, 2); // 邮箱

                dataRows.add(rowData);
            }
        }

        return dataRows;
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private void saveToDatabase(Connection conn, List<String[]> dataRows, String uploadType) throws SQLException {
        String sql = "INSERT INTO users (name, age, email) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] rowData : dataRows) {
                pstmt.setString(1, rowData[0]);
                pstmt.setString(2, rowData[1]);
                pstmt.setString(3, rowData[2]);

                pstmt.executeUpdate();
            }
        }
    }
}
