package com.example.rearend.service;

import com.example.rearend.model.SiteInfo;

import java.util.List;

public interface SiteInfoService {
    Integer insert(SiteInfo siteInfo);

    List<SiteInfo> getAllSiteInfo();

    void deleteByOriginalDataName(String originalDataName);
}
