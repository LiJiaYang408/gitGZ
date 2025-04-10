package com.example.rearend.utils;

import com.example.rearend.model.SiteInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {

    public static List<SiteInfo> parseExcelFile(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int siteRowStart = 6;
            List<SiteInfo> siteInfos = new ArrayList<>();
            String originalDataName = getCellValue(sheet.getRow(2).getCell(5)); // F列，索引5
            String sampleName=getCellValue(sheet.getRow(2).getCell(1));
            for (int i = siteRowStart; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                SiteInfo siteInfo = new SiteInfo();
                if (row == null) continue;

                // 碱基位置（A列，索引0）
                int basePosition = getCellValueAsInt(row.getCell(0));
                // 参考碱基（B列，索引1）
                String referenceBase = getCellValue(row.getCell(1));
                // 突变碱基（C列，索引2）
                String mutantBase = getCellValue(row.getCell(2));
                // 总深度（E列，索引4）
                int totalDepth = getCellValueAsInt(row.getCell(3));
                // 异质性（F列，索引5）
                BigDecimal heterogeneity = getCellValueAsBigDecimal(row.getCell(4));
                // 类型（G列，索引6）
                String type = getCellValue(row.getCell(5));
                siteInfo.setOriginal_data_name(originalDataName);
                siteInfo.setTotal_depth(totalDepth);
                siteInfo.setReference_base(referenceBase);
                siteInfo.setMutant_base(mutantBase);
                siteInfo.setBase_position(basePosition);
                siteInfo.setHeterogeneity(heterogeneity);
                siteInfo.setType(type);
                siteInfo.setSample_name(sampleName);
                siteInfos.add(siteInfo);
            }

            return siteInfos;
        }
    }

    public static List<SiteInfo> parseExampleFile(String filePath) {
        List<SiteInfo> siteInfoList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+)(.*)"); // 优化：明确分割位置和参考碱基

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 6) { // 修正：检查是否有完整的6个字段
                    continue;
                }

                // 解析第一个字段：位置+参考碱基
                String firstPart = parts[0];
                Matcher matcher = pattern.matcher(firstPart);
                if (!matcher.matches()) {
                    continue;
                }

                int basePosition = Integer.parseInt(matcher.group(1));
                String referenceBase = matcher.group(2);

                // 字段顺序：parts[1]=变异碱基, parts[2]=类型, parts[3]=总深度, parts[4]=异质性%
                String mutantBase = parts[1];
                String type = parts[2];
                int totalDepth = Integer.parseInt(parts[3]);
                BigDecimal heterogeneity = new BigDecimal(parts[4].replace("%", ""));

                SiteInfo siteInfo = new SiteInfo();
                siteInfo.setBase_position(basePosition);
                siteInfo.setReference_base(referenceBase);
                siteInfo.setMutant_base(mutantBase);
                siteInfo.setTotal_depth(totalDepth);
                siteInfo.setHeterogeneity(heterogeneity);
                siteInfo.setType(type);
                siteInfoList.add(siteInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // 处理数值转换异常（如总深度非数字）
            System.err.println("解析数值失败: " + e.getMessage());
        }

        return siteInfoList;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private static int getCellValueAsInt(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    private static BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                try {
                    String value = cell.getStringCellValue().trim().replace("%", "");
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO;
                }
            default:
                return BigDecimal.ZERO;
        }
    }

    private static LocalDateTime parseDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return LocalDateTime.ofInstant(
                    cell.getDateCellValue().toInstant(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").getZone()
            );
        }
        String dateStr = getCellValue(cell);
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }
}