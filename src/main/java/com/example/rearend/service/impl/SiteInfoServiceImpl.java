package com.example.rearend.service.impl;

import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.SiteInfoService;
import com.example.rearend.utils.AESUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteInfoServiceImpl implements SiteInfoService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static String FILE_PATH_Sit ;

    @Value("${file.site_info}")
    public void setFILE_PATH_Sit(String file_path_sit){
        FILE_PATH_Sit=file_path_sit;
    }

    @Override
    public Integer insert(SiteInfo siteInfo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_Sit,true))) {
            String resultJson = objectMapper.writeValueAsString(siteInfo);
            String encryptedJson = AESUtils.encrypt(resultJson);
            writer
                    .write(siteInfo.getOriginal_data_name() + ":" + encryptedJson);
            writer
                    .newLine();
        } catch (Exception e) {
            // 记录异常信息，避免异常信息丢失
            System.err.println("Failed to save result to file: " + e.getMessage());
        }
        return 1;
    }

    @Override
    public List<SiteInfo> getAllSiteInfo() {
        List<SiteInfo> siteInfoList = new ArrayList<>();
        try {
            if (new File(FILE_PATH_Sit).exists()) {
                String encryptedContent = readFileContent(FILE_PATH_Sit);
                String decryptedContent = AESUtils.decrypt(encryptedContent);
                siteInfoList = objectMapper.readValue(decryptedContent, objectMapper.getTypeFactory().constructCollectionType(List.class, SiteInfo.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siteInfoList;
    }

    @Override
    public void deleteByOriginalDataName(String originalDataName) {
        List<String> linesToKeep = new ArrayList<>();
        File file = new File(FILE_PATH_Sit);

        // 文件不存在时直接返回
        if (!file.exists()) {
            System.err.println("File does not exist: " + FILE_PATH_Sit);
            return;
        }

        // 1. 读取文件并过滤需要保留的行
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 解析每行的original_data_name（冒号前的部分）
                int colonIndex = line.indexOf(':');
                if (colonIndex == -1) {
                    System.err.println("Invalid line format: " + line);
                    continue; // 跳过格式错误的行
                }
                String currentOriginalDataName = line.substring(0, colonIndex);
                if (!currentOriginalDataName.equals(originalDataName)) {
                    linesToKeep.add(line); // 保留非目标行
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file for deletion: " + e.getMessage());
            return;
        }

        // 2. 清空文件并写入保留的行（覆盖原文件）
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : linesToKeep) {
                writer.write(line);
                writer.newLine(); // 保持与插入时一致的换行格式
            }
        } catch (IOException e) {
            System.err.println("Error writing file after deletion: " + e.getMessage());
        }
    }


    private String readFileContent(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }
    }

}