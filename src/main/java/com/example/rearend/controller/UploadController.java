package com.example.rearend.controller;

import com.example.rearend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private UploadService upload;


    @PostMapping("/upload")
    @Transactional(rollbackFor = Exception.class) // 添加事务注解
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile[] files, String uploadType) {
        if (Objects.equals(uploadType, "whole")) {
            for (MultipartFile file : files) {
                upload.upload(file);
            }
            return ResponseEntity.ok().body(Map.of("message", "所有 Excel 文件上传成功"));
        }
       return upload.uploadVcf(files);
    }
}