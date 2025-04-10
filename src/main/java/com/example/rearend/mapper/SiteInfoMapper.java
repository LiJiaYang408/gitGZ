package com.example.rearend.mapper;

import com.example.rearend.model.SiteInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SiteInfoMapper {
    /**
     * 插入 SiteInfo 实体到数据库
     * @param siteInfo SiteInfo 实体
     */
    Integer insert(SiteInfo siteInfo);

    @Select("SELECT `id`, `original_data_name`, `base_position`, `reference_base`, `mutant_base`, `total_depth`, `heterogeneity`, `type` FROM `site_info`;")
    List<SiteInfo>getAllSiteInfo();

    void deleteByOriginalDataName(String originalDataName);
}