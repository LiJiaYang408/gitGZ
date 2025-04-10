package com.example.rearend.controller;

import com.example.rearend.mapper.MitochondrialDetailMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.Records;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.RecordsService;
import com.example.rearend.service.UploadService;
import com.example.rearend.utils.DataParser;
import com.example.rearend.utils.FileNameUtils;
import com.example.rearend.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/records")
public class RecordsController {
    private final RecordsService recordsService;
    private final UploadService upload;
    private final MitochondrialDetailMapper detailMapper;

    public RecordsController(RecordsService recordsService, UploadService upload, MitochondrialDetailMapper detailMapper) {
        this.recordsService = recordsService;
        this.upload = upload;
        this.detailMapper = detailMapper;
    }

    @PostMapping("/upload")
    @Transactional(rollbackFor = Exception.class) // 添加事务注解
    public ResultUtil<List<Records>> upload(@RequestParam("file") MultipartFile file, String uploadType) throws IOException {
        String fileName = null;
        if (Objects.equals(uploadType, "whole")) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Row sampleRow = sheet.getRow(2);
            fileName = DataParser.getCellValue(sampleRow.getCell(5));
            upload.upload(file);
        }else {
            fileName=FileNameUtils.getFileNameWithoutExtension(file);
            upload.uploadVcf(file);
        }
        MitochondrialDetail mitochondrialDetail=detailMapper.findByOriginalDataName(fileName);
        List<SiteInfo>siteInfoList=detailMapper.getMitochondrialDetailDetails(fileName);
        List<MitochondrialDetail>details=detailMapper.getAllMitochondrialDetail();
        List<Records>recordsList=new ArrayList<>();
        for (MitochondrialDetail detail : details) {
            Records records1=recordsService.Compare(siteInfoList,mitochondrialDetail,detail);
            if (records1.getCompare_name()!=null) {
                recordsList.add(records1);
            }
        }
        return ResultUtil.success(recordsList);
    }

    @GetMapping("/getRecords")
    public ResultUtil<List<Records>> getRecords(){
        return ResultUtil.success(recordsService.findAll());
    }
}
