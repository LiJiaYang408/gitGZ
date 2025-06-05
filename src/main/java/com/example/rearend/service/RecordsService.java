package com.example.rearend.service;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.Records;
import com.example.rearend.model.SiteInfo;

import java.text.ParseException;
import java.util.List;

public interface RecordsService {

    List<Records> findByGoalName(String goalName);

    int insert(Records records);

    Records Comparison(List<SiteInfo>list, MitochondrialDetail mit, MitochondrialDetail detail,List<SiteInfo> list2) throws ParseException;
    Records Compare(List<SiteInfo>list, MitochondrialDetail mit, MitochondrialDetail detail,int num) throws ParseException;

    List<Records> findAll();
}
