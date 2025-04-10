package com.example.rearend.controller;

import com.example.rearend.service.*;
import com.example.rearend.utils.ResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService upload;
    private final CompareService compareService;

    public UploadController(UploadService upload, CompareService compareService) {
        this.upload = upload;
        this.compareService = compareService;
    }

    @PostMapping("/upload")
    @Transactional(rollbackFor = Exception.class) // 添加事务注解
    public ResultUtil<ResponseEntity<?>> handleFileUpload(@RequestParam("file") MultipartFile[] files, String uploadType, @RequestParam(defaultValue = "true") boolean uploadToDb) {
        if (Objects.equals(uploadType, "whole")) {
            for (MultipartFile file : files) {
                upload.upload(file);
            }
            return ResultUtil.success(ResponseEntity.ok().body(Map.of("message", "所有 Excel 文件上传成功")));
        } else {
            for (MultipartFile file : files) {
                upload.uploadVcf(file);
            }
            return ResultUtil.success(ResponseEntity.ok().body(Map.of("message", "所有 vcf 文件上传成功")));
        }
    }

    @PostMapping("/uploadRedis")
    public ResultUtil<String> uploadRedis(@RequestParam("file") MultipartFile file, String uploadType, @RequestParam("flag") boolean flag) {
        try {
            String name;
            if (Objects.equals(uploadType, "whole")) {
                name = compareService.parseFileExcelAndSaveToRedis(file);
                if (flag) {
                    upload.upload(file);
                }
            } else {
                if (flag) {
                    upload.uploadVcf(file);
                }
                name = compareService.parseFileVcfAndSaveToRedis(file);
            }
            return ResultUtil.success(name);
        } catch (Exception e) {
            // 记录异常信息
            e.printStackTrace();
            return ResultUtil.error("文件上传失败: " + e.getMessage());
        }
    }
}