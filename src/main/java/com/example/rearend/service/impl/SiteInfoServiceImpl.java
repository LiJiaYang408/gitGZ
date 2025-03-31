package com.example.rearend.service.impl;

import com.example.rearend.mapper.SiteInfoMapper;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.SiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteInfoServiceImpl implements SiteInfoService {
    @Autowired
    private SiteInfoMapper mapper;
    @Override
    public Integer insert(SiteInfo siteInfo) {
        return mapper.insert(siteInfo);
    }

    @Override
    public List<SiteInfo> getAllSiteInfo() {
        return mapper.getAllSiteInfo();
    }

    @Override
    public void deleteByOriginalDataName(String originalDataName) {
        mapper.deleteByOriginalDataName(originalDataName);
    }
}
