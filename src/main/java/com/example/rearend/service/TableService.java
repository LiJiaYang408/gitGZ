package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableService {
    List<MitochondrialDetail> getAllMitochondrialDetail();

    List<SiteInfo> getMitochondrialDetailDetails(@Param("name") String name);

}
