package com.example.rearend.service.impl;

import com.example.rearend.mapper.MitochondrialDetailMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.MitochondrialDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MitochondrialDetailServiceImpl implements MitochondrialDetailService {

    @Autowired
    private MitochondrialDetailMapper mitochondrialDetailMapper;

    @Override
    public List<MitochondrialDetail> getAllMitochondrialDetail() {
        return mitochondrialDetailMapper.getAllMitochondrialDetail();
    }

    @Override
    public List<SiteInfo> getMitochondrialDetailDetails(String name) {
        return mitochondrialDetailMapper.getMitochondrialDetailDetails(name);
    }

    @Override
    public Integer insert(MitochondrialDetail detail) {
        return mitochondrialDetailMapper.insert(detail);
    }

    @Override
    public Integer selectDuplicateChecking(String name) {
        return mitochondrialDetailMapper.selectDuplicateChecking(name);
    }

    @Override
    public void update(MitochondrialDetail mitochondrialDetail) {
        mitochondrialDetailMapper.update(mitochondrialDetail);
    }

    @Override
    public MitochondrialDetail findByOriginalDataName(String originalDataName) {
        return mitochondrialDetailMapper.findByOriginalDataName(originalDataName);
    }

}
