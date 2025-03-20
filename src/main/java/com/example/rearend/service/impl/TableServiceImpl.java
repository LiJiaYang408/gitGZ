package com.example.rearend.service.impl;

import com.example.rearend.mapper.TableMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableMapper tableMapper;

    @Override
    public List<MitochondrialDetail> getAllMitochondrialDetail() {
        return tableMapper.getAllMitochondrialDetail();
    }

    @Override
    public List<SiteInfo> getMitochondrialDetailDetails(String name) {
        return tableMapper.getMitochondrialDetailDetails(name);
    }

}
