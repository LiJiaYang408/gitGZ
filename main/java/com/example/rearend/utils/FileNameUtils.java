package com.example.rearend.utils;

import org.springframework.web.multipart.MultipartFile;

public class FileNameUtils {
    public static String getFileNameWithoutExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int lastIndex = originalFilename.lastIndexOf('.');
            if (lastIndex != -1) {
                return originalFilename.substring(0, lastIndex);
            }
        }
        return originalFilename;
    }
}