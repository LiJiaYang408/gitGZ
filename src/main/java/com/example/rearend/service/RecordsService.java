package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.Records;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.utils.ResultUtil;

import java.util.List;

public interface RecordsService {

    List<Records> findByGoalName(String goalName);

    int insert(Records records);

    Records Comparison(List<SiteInfo>list, MitochondrialDetail mit, MitochondrialDetail detail);

    List<Records> findAll();
}
