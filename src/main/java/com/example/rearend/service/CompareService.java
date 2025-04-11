package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.utils.CompareResult;
import com.example.rearend.utils.DataParser;
import com.example.rearend.utils.FileNameUtils;
import com.example.rearend.utils.MultipartFileExample;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CompareService {

    private final StringRedisTemplate redisTemplate;
    private final MitochondrialDetailService mitochondrialDetailService;
    private final RecordsService recordsService;
    private final VcfService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompareService(StringRedisTemplate redisTemplate, MitochondrialDetailService mitochondrialDetailService,
                          RecordsService recordsService, VcfService service) {
        this.redisTemplate = redisTemplate;
        this.mitochondrialDetailService = mitochondrialDetailService;
        this.recordsService = recordsService;
        this.service = service;
    }

    /**
     * 根据样本名称获取线粒体详细信息，并将其转换为 Map 列表
     * @param name 样本名称
     * @return 包含线粒体详细信息的 Map 列表
     */
    public List<Map<String, Object>> getMitochondrialDetailDetails(String name) {
        List<SiteInfo> list = mitochondrialDetailService.getMitochondrialDetailDetails(name);
        return convertToMapList(list);
    }

    /**
     * 将 SiteInfo 列表转换为 Map 列表
     * @param siteInfos SiteInfo 列表
     * @return 包含 SiteInfo 信息的 Map 列表
     */
    private List<Map<String, Object>> convertToMapList(List<SiteInfo> siteInfos) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SiteInfo siteInfo : siteInfos) {
            result.add(convertToMap(siteInfo));
        }
        return result;
    }

    /**
     * 将 SiteInfo 对象转换为 Map 对象
     * @param siteInfo SiteInfo 对象
     * @return 包含 SiteInfo 信息的 Map 对象
     */
    private Map<String, Object> convertToMap(SiteInfo siteInfo) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = siteInfo.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(siteInfo));
            } catch (IllegalAccessException e) {
                // 记录异常信息，避免异常信息丢失
                System.err.println("Failed to access field " + field.getName() + " of SiteInfo: " + e.getMessage());
            }
        }
        return map;
    }

    /**
     * 比较两个样本的数据
     * @param sampleName1 第一个样本名称
     * @param sampleName2 第二个样本名称
     * @return 比较结果列表
     */
    public List<CompareResult> compareData(String sampleName1, String sampleName2) {
        // 记录比较信息
        List<SiteInfo> siteInfos = mitochondrialDetailService.getMitochondrialDetailDetails(sampleName1);
        MitochondrialDetail mitochondrialDetail1 = mitochondrialDetailService.findByOriginalDataName(sampleName1);
        MitochondrialDetail mitochondrialDetail2 = mitochondrialDetailService.findByOriginalDataName(sampleName2);
        recordsService.Comparison(siteInfos, mitochondrialDetail1, mitochondrialDetail2, new ArrayList<>());

        String redisKey = "compare:" + sampleName1 + ":" + sampleName2;
        // 先从 Redis 中获取结果
        List<CompareResult> cachedResult = getResultFromRedis(redisKey);
        if (cachedResult != null && !cachedResult.isEmpty()) {
            return cachedResult;
        }

        List<Map<String, Object>> targetData = getMitochondrialDetailDetails(sampleName1);
        List<Map<String, Object>> dbData = getMitochondrialDetailDetails(sampleName2);

        List<CompareResult> compareResult = compareMaps(targetData, dbData);

        // 将结果存入 Redis
        saveResultToRedis(redisKey, compareResult);

        return compareResult;
    }

    /**
     * 复杂数据比较，比较两个 SiteInfo 列表的数据
     * @param list1 第一个 SiteInfo 列表
     * @param list2 第二个 SiteInfo 列表
     * @return 比较结果列表
     */
    public List<CompareResult> complexityCompareData(List<SiteInfo> list1, List<SiteInfo> list2,boolean flag) {
        // 初始化线粒体详细信息
        MitochondrialDetail mitochondrialDetail1 = new MitochondrialDetail();
        mitochondrialDetail1.setSample_name(list1.get(0).getSample_name());
        mitochondrialDetail1.setOriginal_data_name(list1.get(0).getOriginal_data_name());
        MitochondrialDetail mitochondrialDetail2 = new MitochondrialDetail();
        mitochondrialDetail2.setSample_name(list2.get(0).getSample_name());
        mitochondrialDetail2.setOriginal_data_name(list2.get(0).getOriginal_data_name());

        // 记录比较信息
        if (flag) {
            recordsService.Comparison(list1, mitochondrialDetail1, mitochondrialDetail2, list2);
        }
        String redisKey = "complexity:" + list1.get(0).getOriginal_data_name() + ":" + list2.get(0).getOriginal_data_name();

        List<Map<String, Object>> targetData = convertToMapList(list1);
        List<Map<String, Object>> dbData = convertToMapList(list2);

        List<CompareResult> compareResult = compareMaps(targetData, dbData);

        // 将结果存入 Redis
        saveResultToRedis(redisKey, compareResult);

        return compareResult;
    }

    /**
     * 从 Redis 中获取比较结果
     * @param redisKey Redis 键
     * @return 比较结果列表，如果不存在则返回 null
     */
    private List<CompareResult> getResultFromRedis(String redisKey) {
        String resultJson = redisTemplate.opsForValue().get(redisKey);
        if (resultJson != null && !resultJson.equals("[]")) {
            try {
                return objectMapper.readValue(resultJson, new TypeReference<List<CompareResult>>() {});
            } catch (IOException e) {
                // 记录异常信息，避免异常信息丢失
                System.err.println("Failed to read value from Redis: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 将比较结果保存到 Redis
     * @param redisKey Redis 键
     * @param result 比较结果列表
     */
    private void saveResultToRedis(String redisKey, List<CompareResult> result) {
        try {
            redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(result));
        } catch (IOException e) {
            // 记录异常信息，避免异常信息丢失
            System.err.println("Failed to save result to Redis: " + e.getMessage());
        }
    }

    /**
     * 比较两个 Map 列表的数据
     * @param targetData 目标数据列表
     * @param dbData 数据库数据列表
     * @return 比较结果列表
     */
    private List<CompareResult> compareMaps(List<Map<String, Object>> targetData, List<Map<String, Object>> dbData) {
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
                compareResult.add(createCompareResult(targetItem, "", basePosition));
            } else if (!found.get("mutant_base").equals(targetItem.get("mutant_base"))) {
                compareResult.add(createCompareResult(targetItem, found.get("mutant_base").toString(), basePosition));
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
                compareResult.add(createCompareResult(dbItem, dbItem.get("mutant_base").toString(), basePosition, true));
            }
        }

        return compareResult;
    }

    /**
     * 创建比较结果对象
     * @param item 数据项
     * @param dbValue 数据库中的值
     * @param basePosition 碱基位置
     * @return 比较结果对象
     */
    private CompareResult createCompareResult(Map<String, Object> item, String dbValue, String basePosition) {
        return createCompareResult(item, dbValue, basePosition, false);
    }

    /**
     * 创建比较结果对象
     * @param item 数据项
     * @param dbValue 数据库中的值
     * @param basePosition 碱基位置
     * @param isDbItem 是否为数据库中的项
     * @return 比较结果对象
     */
    private CompareResult createCompareResult(Map<String, Object> item, String dbValue, String basePosition, boolean isDbItem) {
        CompareResult result = new CompareResult();
        result.setTarget(isDbItem ? "" : item.get("mutant_base").toString());
        result.setDb(isDbItem ? dbValue : (dbValue.isEmpty() ? "" : dbValue));
        result.setStandard(item.get("reference_base").toString());
        result.setPosition(basePosition);
        return result;
    }

    /**
     * 解析 Excel 文件并将解析后的数据存储到 Redis
     * @param file Excel 文件
     * @return 样本名称，如果解析失败则返回 null
     * @throws IOException 文件解析异常
     */
    public String parseFileExcelAndSaveToRedis(MultipartFile file) throws IOException {
        // 使用 DataParser 解析 Excel 文件
        List<SiteInfo> siteInfos = DataParser.parseExcelFile(file);
        if (siteInfos.isEmpty()) {
            return null;
        }
        // 获取样本名
        String sampleName = siteInfos.get(0).getSample_name();
        // 将解析后的数据存储到 Redis
        String redisKey = "sample:" + sampleName;
        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(convertToMapList(siteInfos)));
        return sampleName;
    }

    /**
     * 解析 VCF 文件并将解析后的数据存储到 Redis
     * @param file VCF 文件
     * @return 样本名称，如果解析失败则返回 null
     * @throws IOException 文件解析异常
     */
    public String parseFileVcfAndSaveToRedis(MultipartFile file) throws IOException {
        // 获取样本名
        String fileName = FileNameUtils.getFileNameWithoutExtension(file);
        service.processVcfFile(MultipartFileExample.getTempFilePath(file), "example.txt");
        List<SiteInfo> siteInfos = DataParser.parseExampleFile("example.txt");
        if (siteInfos.isEmpty()) {
            return null;
        }
        // 将解析后的数据存储到 Redis
        String redisKey = "sample:" + fileName;
        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(convertToMapList(siteInfos)));
        return fileName;
    }

    /**
     * 从 Redis 中获取解析后的数据
     * @param sampleName 样本名称
     * @return 解析后的数据列表，如果不存在则返回 null
     */
    public List<Map<String, Object>> getParsedDataFromRedis(String sampleName) {
        String redisKey = "sample:" + sampleName;
        String resultJson = redisTemplate.opsForValue().get(redisKey);
        if (resultJson != null && !resultJson.equals("[]")) {
            try {
                return objectMapper.readValue(resultJson, new TypeReference<List<Map<String, Object>>>() {});
            } catch (IOException e) {
                // 记录异常信息，避免异常信息丢失
                System.err.println("Failed to read parsed data from Redis: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 将 Map 列表转换为 SiteInfo 列表
     * @param dataList Map 列表
     * @return SiteInfo 列表
     */
    public List<SiteInfo> convertToSiteInfoList(List<Map<String, Object>> dataList) {
        if (dataList == null) {
            return new ArrayList<>();
        }
        List<SiteInfo> siteInfoList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            SiteInfo siteInfo = new SiteInfo();
            if (data.get("id") != null) {
                siteInfo.setId((Integer) data.get("id"));
            }
            if (data.get("original_data_name") != null) {
                siteInfo.setOriginal_data_name((String) data.get("original_data_name"));
            }
            if (data.get("base_position") != null) {
                siteInfo.setBase_position((Integer) data.get("base_position"));
            }
            if (data.get("reference_base") != null) {
                siteInfo.setReference_base((String) data.get("reference_base"));
            }
            if (data.get("mutant_base") != null) {
                siteInfo.setMutant_base((String) data.get("mutant_base"));
            }
            if (data.get("total_depth") != null) {
                siteInfo.setTotal_depth((Integer) data.get("total_depth"));
            }
            if (data.get("heterogeneity") != null) {
                siteInfo.setHeterogeneity(new BigDecimal(data.get("heterogeneity").toString()));
            }
            if (data.get("type") != null) {
                siteInfo.setType((String) data.get("type"));
            }
            if (data.get("sample_name") != null) {
                siteInfo.setSample_name((String) data.get("sample_name"));
            }
            siteInfoList.add(siteInfo);
        }
        return siteInfoList;
    }
}