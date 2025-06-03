package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.utils.DataParser;
import com.example.rearend.utils.DateUtils;
import com.example.rearend.utils.FileNameUtils;
import com.example.rearend.utils.MultipartFileExample;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UploadService {

    private final MitochondrialDetailService detailMapper;
    private final SiteInfoService siteInfoMapper;
    private final VcfService service;
    private final MitochondrialDetailService detailService;

    public UploadService(MitochondrialDetailService detailMapper, SiteInfoService siteInfoMapper, VcfService service, MitochondrialDetailService detailService) {
        this.detailMapper = detailMapper;
        this.siteInfoMapper = siteInfoMapper;
        this.service = service;
        this.detailService = detailService;
    }

    public ResponseEntity<Map<String, String>> uploadVcf(MultipartFile file) throws ParseException {

            String fileName = FileNameUtils.getFileNameWithoutExtension(file);
            service.processVcfFile(MultipartFileExample.getTempFilePath(file), "example.txt");
            List<SiteInfo> siteInfos = DataParser.parseExampleFile("example.txt");
            MitochondrialDetail mitochondrialDetail = new MitochondrialDetail();
            mitochondrialDetail.setSample_name(fileName);
            mitochondrialDetail.setAnalysis_date(DateUtils.getDate());
            mitochondrialDetail.setOriginal_data_name(fileName);

            // 检查是否存在相同原始样本名的数据
            MitochondrialDetail existingDetail = detailMapper.findByOriginalDataName(fileName);
            if (existingDetail != null) {
                // 更新分析日期
                existingDetail.setAnalysis_date(mitochondrialDetail.getAnalysis_date());
                detailMapper.update(existingDetail);
                // 删除相关的位点信息
                siteInfoMapper.deleteByOriginalDataName(fileName);
            } else {
                detailService.insert(mitochondrialDetail);
            }

            for (SiteInfo siteInfo : siteInfos) {
                siteInfo.setOriginal_data_name(fileName);
                siteInfoMapper.insert(siteInfo);
            }
            return ResponseEntity.ok().body(Map.of("message", "所有 VCF 文件数据解析成功"));
    }

    public ResponseEntity<Map<String, String>> upload(MultipartFile file) throws IOException, ParseException {

            List<SiteInfo> siteInfos = DataParser.parseExcelFile(file);
            String originalDataName = siteInfos.isEmpty() ? "" : siteInfos.get(0).getOriginal_data_name();

            // 检查是否存在相同原始样本名的数据
            MitochondrialDetail existingDetail = detailMapper.findByOriginalDataName(originalDataName);
            if (existingDetail != null) {
                // 更新分析日期
                existingDetail.setAnalysis_date(DateUtils.getDate());
                existingDetail.setSample_name(siteInfos.get(0).getSample_name());
                detailMapper.update(existingDetail);
                // 删除相关的位点信息
                siteInfoMapper.deleteByOriginalDataName(originalDataName);
            } else {
                // 保存主表数据
                MitochondrialDetail mitochondrialDetail = new MitochondrialDetail();
                mitochondrialDetail.setSample_name(siteInfos.get(0).getSample_name());
                mitochondrialDetail.setOriginal_data_name(originalDataName);
                mitochondrialDetail.setAnalysis_date(DateUtils.getDate());

                boolean flag = detailMapper.selectDuplicateChecking(mitochondrialDetail.getSample_name()) > 0;
                if (flag) {
                    return ResponseEntity.status(500).body(Map.of("message", "已有重复数据"));
                }

                detailMapper.insert(mitochondrialDetail);
            }

            for (SiteInfo siteInfo : siteInfos) {
                siteInfoMapper.insert(siteInfo);
            }

            return ResponseEntity.ok().body(Map.of("message", "数据解析成功"));

    }
}