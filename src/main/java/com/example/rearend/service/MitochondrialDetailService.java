package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;


import java.io.IOException;
import java.util.List;

public interface MitochondrialDetailService {
    List<MitochondrialDetail> getAllMitochondrialDetail() throws IOException;

    List<SiteInfo> getMitochondrialDetailDetails(String name);

    Integer insert(MitochondrialDetail detail);

    Integer selectDuplicateChecking(String name);

    void update(MitochondrialDetail mitochondrialDetail);

    MitochondrialDetail findByOriginalDataName(String originalDataName);
}
