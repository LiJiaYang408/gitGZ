package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MitochondrialDetailService {
    List<MitochondrialDetail> getAllMitochondrialDetail();

    List<SiteInfo> getMitochondrialDetailDetails(@Param("name") String name);

    Integer insert(MitochondrialDetail detail);

    Integer selectDuplicateChecking(@Param("name")String name);

    void update(MitochondrialDetail mitochondrialDetail);

    MitochondrialDetail findByOriginalDataName(String originalDataName);
}
