package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UploadService {

    @Autowired
    private MitochondrialDetailService detailMapper;
    @Autowired
    private SiteInfoService siteInfoMapper;

    @Transactional
    public ResponseEntity<Map<String, String>> upload(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row sampleRow = sheet.getRow(2);
            String sampleName = getCellValue(sampleRow.getCell(1)); // B列，索引1
            LocalDateTime analysisDate = parseDate(sampleRow.getCell(3)); // D列，索引3
            String originalDataName = getCellValue(sampleRow.getCell(5)); // F列，索引5

            // 保存主表数据
            MitochondrialDetail mitochondrialDetail = new MitochondrialDetail();
            mitochondrialDetail.setSample_name(sampleName);
            mitochondrialDetail.setOriginal_data_name(originalDataName);
            mitochondrialDetail.setAnalysis_date(analysisDate);

            boolean flag = detailMapper.selectDuplicateChecking(mitochondrialDetail.getSample_name()) > 0;
            if (flag){
                return ResponseEntity.status(500).body(Map.of("message", "已有重复数据"));
            }

            int siteRowStart = 6;
            List<SiteInfo> siteInfos = new ArrayList<>();
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
                siteInfo.setSample_name(mitochondrialDetail.getSample_name());
                siteInfo.setTotal_depth(totalDepth);
                siteInfo.setReference_base(referenceBase);
                siteInfo.setMutant_base(mutantBase);
                siteInfo.setBase_position(basePosition);
                siteInfo.setHeterogeneity(heterogeneity);
                siteInfo.setType(type);
                siteInfos.add(siteInfo);
            }


                detailMapper.insert(mitochondrialDetail);
                for (SiteInfo siteInfo : siteInfos) {
                    siteInfoMapper.insert(siteInfo);
                }


            return ResponseEntity.ok().body(Map.of("message", "数据解析成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "数据处理失败: " + e.getMessage()));
        }
    }

    private String getCellValue(Cell cell) {
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

    private int getCellValueAsInt(Cell cell) {
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

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
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

    private LocalDateTime parseDate(Cell cell) {
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
    public  List<SiteInfo> parseExampleFile(String filePath) {
        List<SiteInfo> siteInfoList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+)(.*)");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 5) {
                    continue; // 跳过不完整的行
                }

                // 解析第一个字段
                String firstPart = parts[0];
                Matcher matcher = pattern.matcher(firstPart);
                if (!matcher.matches()) {
                    continue; // 无法解析的行跳过
                }

                int basePosition = Integer.parseInt(matcher.group(1));
                String mutantBase = matcher.group(2);

                // 其他字段解析
                String referenceBase = parts[1];
                int totalDepth = Integer.parseInt(parts[2]);
                BigDecimal heterogeneity = new BigDecimal(parts[3].replace("%", ""));
                String type = parts[4];

                // 创建SiteInfo对象并添加到列表
                SiteInfo siteInfo = new SiteInfo();
                siteInfo.setBase_position(basePosition);
                siteInfo.setReference_base(referenceBase);
                siteInfo.setMutant_base(mutantBase);
                siteInfo.setTotal_depth(totalDepth);
                siteInfo.setHeterogeneity(heterogeneity);
                siteInfo.setType("SNP");
                siteInfoList.add(siteInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return siteInfoList;
    }
}
