package com.example.rearend.service;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.utils.CompareResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;

import java.util.*;

@Service
public class CompareService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MitochondrialDetailService mitochondrialDetailService;
    @Autowired
    private RecordsService recordsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, Object>> getMitochondrialDetailDetails(String name) {
        List<SiteInfo> list = mitochondrialDetailService.getMitochondrialDetailDetails(name);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SiteInfo siteInfo : list) {
            Map<String, Object> map = convertToMap(siteInfo);
            result.add(map);
        }
        return result;
    }

    private Map<String, Object> convertToMap(SiteInfo siteInfo) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = siteInfo.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(siteInfo));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public List<CompareResult> compareData(String sampleName1, String sampleName2) {
        //记录
        List<SiteInfo>siteInfos=mitochondrialDetailService.getMitochondrialDetailDetails(sampleName1);
        MitochondrialDetail mitochondrialDetail1=mitochondrialDetailService.findByOriginalDataName(sampleName1);
        MitochondrialDetail mitochondrialDetail2=mitochondrialDetailService.findByOriginalDataName(sampleName2);
        recordsService.Comparison(siteInfos,mitochondrialDetail1,mitochondrialDetail2);

        String redisKey = "compare:" + sampleName1 + ":" + sampleName2;
        // 先从 Redis 中获取结果
        String resultJson = redisTemplate.opsForValue().get(redisKey);
        if (resultJson != null&&!resultJson.equals("[]")) {
            try {
                return objectMapper.readValue(resultJson, new TypeReference<List<CompareResult>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        List<Map<String, Object>> targetData = getMitochondrialDetailDetails(sampleName1);
        List<Map<String, Object>> dbData = getMitochondrialDetailDetails(sampleName2);

        List<CompareResult> compareResult = new ArrayList<>();
        Map<String, Map<String, Object>> dbMap = new HashMap<>();
        for (Map<String, Object> dbItem : dbData) {
            dbMap.put(dbItem.get("base_position").toString(), dbItem);
        }

        // 遍历目标样本数据
        for (Map<String, Object> targetItem : targetData) {
            String basePosition = targetItem.get("base_position").toString();
            Map<String, Object> found = dbMap.get(basePosition);
            if (found == null) {
                CompareResult result = new CompareResult();
                result.setTarget(targetItem.get("mutant_base").toString());
                result.setDb("");
                result.setStandard(targetItem.get("reference_base").toString());
                result.setPosition(basePosition);
                compareResult.add(result);
            } else if (!found.get("mutant_base").equals(targetItem.get("mutant_base"))) {
                CompareResult result = new CompareResult();
                result.setTarget(targetItem.get("mutant_base").toString());
                result.setDb(found.get("mutant_base").toString());
                result.setStandard(targetItem.get("reference_base").toString());
                result.setPosition(basePosition);
                compareResult.add(result);
            }
        }

        // 遍历对比样本数据
        for (Map<String, Object> dbItem : dbData) {
            String basePosition = dbItem.get("base_position").toString();
            boolean found = false;
            for (Map<String, Object> targetItem : targetData) {
                if (targetItem.get("base_position").equals(basePosition)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                CompareResult result = new CompareResult();
                result.setTarget("");
                result.setDb(dbItem.get("mutant_base").toString());
                result.setStandard(dbItem.get("reference_base").toString());
                result.setPosition(basePosition);
                compareResult.add(result);
            }
        }

        // 将结果存入 Redis
        try {
            redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(compareResult));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compareResult;
    }

}