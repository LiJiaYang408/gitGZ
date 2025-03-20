package com.example.rearend.mapper;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TableMapper {

    @Select("select * from mitochondrial_detail")
    List<MitochondrialDetail> getAllMitochondrialDetail();

    @Select("select * from site_info where sample_name=#{name}")
    List<SiteInfo> getMitochondrialDetailDetails(@Param("name") String name);

}
