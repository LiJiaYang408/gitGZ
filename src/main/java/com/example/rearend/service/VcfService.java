package com.example.rearend.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VcfService {

    public void processVcfFile(String inputFilePath, String outputFilePath) {
        List<String> bassesList = readVcf(inputFilePath);
        writeBassesListToTxt(bassesList, outputFilePath);
    }

    public List<String> readVcf(String inputFilePath) {
        List<String> bassesList = new ArrayList<>();
        Set<Integer> processedPositions = new HashSet<>(); // 用于记录已处理的位置

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String basses = processVcfLine(line, processedPositions);
                    if (basses != null) {
                        bassesList.add(basses);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bassesList;
    }

    private String processVcfLine(String line, Set<Integer> processedPositions) {
        String[] parts = line.split("\t");
        int pos = Integer.parseInt(parts[1]);
        if (pos > 16569)
            pos -= 16569;

        // 检查是否重复，重复则跳过当前行
        if (processedPositions.contains(pos)) {
            return null;
        }
        processedPositions.add(pos); // 记录已处理的位置

        String ref = parts[3];
        String alt = parts[4];
        String filter = parts[6];
        String info = parts[7];

        if (!"PASS".equalsIgnoreCase(filter)) {
            return null; // 只处理 filter 为 "PASS" 的变体
        }

        // 识别 SNP 和 INDEL
        String variantType="SNP";
        if (ref.length() == 1 && alt.length() == 1) {
            variantType = "SNP";
        } else if (ref.length() > 1 || alt.length() > 1) {
            variantType = "INDEL";
        }

        // 提取 DP 值
        int dp = Arrays.stream(info.split(";"))
                .filter(infoPart -> infoPart.startsWith("DP="))
                .map(infoPart -> Integer.parseInt(infoPart.substring(3)))
                .findFirst()
                .orElse(0);

        // 提取 DP4 值
        List<Integer> dp4 = Arrays.stream(info.split(";"))
                .filter(infoPart -> infoPart.startsWith("DP4="))
                .map(infoPart -> infoPart.substring(4))
                .flatMap(dp4Value -> Arrays.stream(dp4Value.split(",")))
                .map(Integer::parseInt).toList();

        // 计算最大值和总和
        int maxDp4 = dp4.stream().max(Integer::compareTo).orElse(0);
        int sumDp4 = dp4.stream().mapToInt(Integer::intValue).sum();
        double percentage = (1.0 - ((double) maxDp4 / sumDp4)) * 100;

        String dp4Sorted = dp4.stream()
                .sorted(Comparator.reverseOrder())
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return String.format("%s;%s;%s;%d;%.2f%%;%s", pos + ref, alt,variantType, dp, percentage, dp4Sorted);
    }

    private void writeBassesListToTxt(List<String> bassesList, String outputFilePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            bassesList.forEach(basses -> {
                try {
                    bw.write(basses);
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}