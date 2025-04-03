package com.example.rearend.service.impl;

import com.example.rearend.mapper.MitochondrialDetailMapper;
import com.example.rearend.mapper.RecordsMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.Records;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.RecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class RecordsServiceImpl implements RecordsService {
    @Autowired
    private RecordsMapper mapper;
    @Autowired
    private MitochondrialDetailMapper detailMapper;

    @Override
    public List<Records> findByGoalName(String goalName) {
        return mapper.findByGoalName(goalName);
    }

    @Override
    public int insert(Records records) {
        return mapper.insert(records);
    }


    @Override
    public Records Comparison(List<SiteInfo>list,MitochondrialDetail mit,MitochondrialDetail detail) {
        Records records = new Records();
        List<SiteInfo> siteInfoList = detailMapper.getMitochondrialDetailDetails(detail.getOriginal_data_name());
        int num = 0;
        boolean flag=false;
        for (SiteInfo siteInfo : siteInfoList) {
            for (SiteInfo info : list) {
                if (!siteInfo.getOriginal_data_name().equals(info.getOriginal_data_name())) {
                    flag=true;
                    if (Objects.equals(siteInfo.getBase_position(), info.getBase_position())) {
                        if (siteInfo.getMutant_base().equals(info.getMutant_base())) {
                            num++;
                            continue;
                        }
                        num++;
                    }
                }else {
                    flag=false;
                }
            }
        }
        int i=siteInfoList.size()+list.size()-num;
        if (i <= 10&&flag) {
            records.setAllowance(i);
            records.setTime(LocalDateTime.now());
            records.setGoal_name(mit.getSample_name());
            records.setCompare_name(detail.getSample_name());
            records.setOriginal_goal(mit.getOriginal_data_name());
            records.setOriginal_compare(detail.getOriginal_data_name());
            insert(records);
        }
        return records;
    }

    @Override
    public List<Records> findAll() {
        return mapper.findAll();
    }
}
