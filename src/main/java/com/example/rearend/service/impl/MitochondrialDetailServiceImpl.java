package com.example.rearend.service.impl;

import com.example.rearend.mapper.MitochondrialDetailMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.MitochondrialDetailService;
import com.example.rearend.utils.AESUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MitochondrialDetailServiceImpl implements MitochondrialDetailService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH_Mit= "mitochondrial_detail.txt";
    private final MitochondrialDetailMapper mitochondrialDetailMapper;

    public MitochondrialDetailServiceImpl(MitochondrialDetailMapper mitochondrialDetailMapper) {
        this.mitochondrialDetailMapper = mitochondrialDetailMapper;
    }

    @Override
    public List<MitochondrialDetail> getAllMitochondrialDetail() {
        List<MitochondrialDetail> details = new ArrayList<>();
        File file = new File(FILE_PATH_Mit);
        if (!file.exists()) {
            System.err.println("文件不存在: " + FILE_PATH_Mit);
            return details;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int colonIndex = line.indexOf(':');
                    if (colonIndex == -1) continue; // 跳过格式错误的行
                    String encryptedJson = line.substring(colonIndex + 1);
                    String decryptedJson = AESUtils.decrypt(encryptedJson);
                    MitochondrialDetail detail = objectMapper.readValue(decryptedJson, MitochondrialDetail.class);
                    details.add(detail);
                } catch (Exception e) {
                    System.err.println("解析文件行失败: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
        return details;
    }

    @Override
    public List<SiteInfo> getMitochondrialDetailDetails(String name) {
        return mitochondrialDetailMapper.getMitochondrialDetailDetails(name);
    }

    @Override
    public Integer insert(MitochondrialDetail detail) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_Mit, true))) {
            String resultJson = objectMapper.writeValueAsString(detail);
            String encryptedJson = AESUtils.encrypt(resultJson);
            writer
                    .write(detail.getOriginal_data_name() + ":" + encryptedJson);
            writer
                    .newLine();
        } catch (Exception e) {
            // 记录异常信息，避免异常信息丢失
            System.err.println("Failed to save result to file: " + e.getMessage());
        }
        return 1;
    }

    @Override
    public Integer selectDuplicateChecking(String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        Map<String, Integer> countMap = new ConcurrentHashMap<>();
        File file = new File(FILE_PATH_Mit);
        if (!file.exists()) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex == -1) continue; // 跳过格式错误的行
                String currentName = line.substring(0, colonIndex);
                countMap.put(currentName, countMap.getOrDefault(currentName, 0) + 1);
            }
        } catch (IOException e) {
            System.err.println("统计重复项失败: " + e.getMessage());
        }
        return countMap.getOrDefault(name, 0);
    }

    @Override
    public void update(MitochondrialDetail detail) {
        List<String> fileLines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_Mit))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex == -1) {
                    fileLines.add(line);
                    continue;
                }

                String name = line.substring(0, colonIndex);
                if (name.equals(detail.getOriginal_data_name())) {
                    // 生成新的加密数据行
                    String newJson = objectMapper.writeValueAsString(detail);
                    String encryptedJson = AESUtils.encrypt(newJson);
                    fileLines.add(detail.getOriginal_data_name() + ":" + encryptedJson);
                    updated = true;
                } else {
                    fileLines.add(line); // 保留原行
                }
            }

            // 若未找到匹配项，可根据需求抛出异常或静默处理
            if (!updated) {
                System.err.println("未找到要更新的记录: " + detail.getOriginal_data_name());
                return;
            }

            // 写入更新后的内容（覆盖原文件）
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_Mit))) {
                for (String newLine : fileLines) {
                    writer.write(newLine);
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            System.err.println("更新文件数据失败: " + e.getMessage());
        }
    }

    @Override
    public MitochondrialDetail findByOriginalDataName(String originalDataName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_Mit))) {
            String line;
            while ((line = reader.readLine()) != null) {

                int colonIndex = line.indexOf(':');
                if (colonIndex == -1) continue;

                String name = line.substring(0, colonIndex);
                String encryptedJson = line.substring(colonIndex + 1);

                if (name.equals(originalDataName)) {
                    String decryptedJson = AESUtils.decrypt(encryptedJson);
                    return objectMapper.readValue(decryptedJson, MitochondrialDetail.class);
                }
            }
        } catch (Exception e) {
            System.err.println("查询文件数据失败: " + e.getMessage());
            return null;
        }
        return null;
    }

}
