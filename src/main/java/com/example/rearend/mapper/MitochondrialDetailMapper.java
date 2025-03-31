package com.example.rearend.mapper;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MitochondrialDetailMapper {

    @Select("select * from mitochondrial_detail")
    List<MitochondrialDetail> getAllMitochondrialDetail();

    @Select("select * from site_info where original_data_name=#{name}")
    List<SiteInfo> getMitochondrialDetailDetails(@Param("name") String name);

    /**
     * 插入 MitochondrialDetail 实体到数据库
     * @param detail MitochondrialDetail 实体
     */
    Integer insert(MitochondrialDetail detail);

    @Select("select count(*) from mitochondrial_detail where original_data_name=#{name}")
    Integer selectDuplicateChecking(@Param("name")String name);

    void update(MitochondrialDetail mitochondrialDetail);

    MitochondrialDetail findByOriginalDataName(String originalDataName);
}
