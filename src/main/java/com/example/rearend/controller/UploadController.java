package com.example.rearend.controller;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.MitochondrialDetailService;
import com.example.rearend.service.SiteInfoService;
import com.example.rearend.service.VcfService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private MitochondrialDetailService detailMapper;
    @Autowired
    private SiteInfoService siteInfoMapper;
    @Autowired
    private VcfService service;

    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,String uploadType) {

        if (Objects.equals(uploadType, "whole")){
            return upload(file);
        }
        System.out.println(file.getOriginalFilename());
        return null;
    }

    private ResponseEntity<Map<String, String>> upload(MultipartFile file){
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row sampleRow = sheet.getRow(2);
            String sampleName = getCellValue(sampleRow.getCell(1)); // B列，索引1
            LocalDateTime analysisDate = parseDate(sampleRow.getCell(3)); // D列，索引3
            String originalDataName = getCellValue(sampleRow.getCell(5)); // F列，索引5

            // 保存主表数据
            MitochondrialDetail mitochondrialDetail=new MitochondrialDetail();
            mitochondrialDetail.setSample_name(sampleName);
            mitochondrialDetail.setOriginal_data_name(originalDataName);
            mitochondrialDetail.setAnalysis_date(analysisDate);

            Integer flag=detailMapper.selectDuplicateChecking(mitochondrialDetail.getSample_name());
            if (flag>0){
                return ResponseEntity.status(500).body(Map.of("message", "重复数据"));
            }
            detailMapper.insert(mitochondrialDetail);

            System.out.println("样本名: " + sampleName);
            System.out.println("分析日期: " + analysisDate);
            System.out.println("原始数据名: " + originalDataName);


            int siteRowStart = 6;
            List<SiteInfo>siteInfos=new ArrayList<>();
            for (int i = siteRowStart; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                SiteInfo siteInfo=new SiteInfo();
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

                System.out.println("碱基位置: " + basePosition);
                System.out.println("参考碱基: " + referenceBase);
                System.out.println("突变碱基: " + mutantBase);
                System.out.println("总深度: " + totalDepth);
                System.out.println("异质性: " + heterogeneity);
                System.out.println("类型: " + type);
            }


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
}