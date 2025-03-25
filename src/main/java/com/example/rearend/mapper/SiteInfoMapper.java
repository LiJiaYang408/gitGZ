package com.example.rearend.mapper;

import com.example.rearend.model.SiteInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteInfoMapper {
    /**
     * 插入 SiteInfo 实体到数据库
     * @param siteInfo SiteInfo 实体
     */
    Integer insert(SiteInfo siteInfo);
}