package com.example.rearend.controller;

import com.example.rearend.mapper.MitochondrialDetailMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.*;
import com.example.rearend.utils.FileNameUtils;
import com.example.rearend.utils.MultipartFileExample;
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
import java.util.*;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private UploadService upload;
    @Autowired
    private VcfService service;
    @Autowired
    private MitochondrialDetailService detailService;
    @Autowired
    private SiteInfoService siteInfoService;


    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, String uploadType,String inputValue) {

        if (Objects.equals(uploadType, "whole")) {
            return upload.upload(file);
        }

        boolean flag = detailService.selectDuplicateChecking(inputValue) > 0;
        if (flag){
            return ResponseEntity.status(500).body(Map.of("message", "已有重复数据"));
        }
        try {
            String fileName = FileNameUtils.getFileNameWithoutExtension(file);
            service.processVcfFile(MultipartFileExample.getTempFilePath(file), "example.txt");
            List<SiteInfo> siteInfos = upload.parseExampleFile("example.txt");
            MitochondrialDetail mitochondrialDetail = new MitochondrialDetail();
            mitochondrialDetail.setSample_name(inputValue);
            mitochondrialDetail.setAnalysis_date(LocalDateTime.now());
            mitochondrialDetail.setOriginal_data_name(fileName);
            detailService.insert(mitochondrialDetail);
            for (SiteInfo siteInfo : siteInfos) {
                siteInfo.setSample_name(inputValue);
                siteInfoService.insert(siteInfo);
            }
            return ResponseEntity.ok().body(Map.of("message", "数据解析成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "数据处理失败: " + e.getMessage()));
        }
    }
}