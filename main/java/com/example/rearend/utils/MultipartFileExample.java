package com.example.rearend.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class MultipartFileExample {
    public static String getTempFilePath(MultipartFile file) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("uploaded-", "-" + file.getOriginalFilename());
            // 将上传的文件内容传输到临时文件
            file.transferTo(tempFile);
            // 返回临时文件的绝对路径
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}