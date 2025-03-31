package com.example.rearend.controller;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.MitochondrialDetailService;
import com.example.rearend.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/table")
public class TableController {
    @Autowired
    private MitochondrialDetailService mitochondrialDetailService;

    @GetMapping("/getMitochondrialDetailAll")
    public ResultUtil getMitochondrialDetailAll(){
        List<MitochondrialDetail>list= mitochondrialDetailService.getAllMitochondrialDetail();
        return ResultUtil.success(list);
    }

    @GetMapping("/getMitochondrialDetailDetails")
    public ResultUtil getMitochondrialDetailDetails(@RequestParam String name){
        List<SiteInfo>list= mitochondrialDetailService.getMitochondrialDetailDetails(name);
        return ResultUtil.success(list);
    }
    @GetMapping("/getCountName")
    public ResultUtil getCountName(@RequestParam String name){
        Integer i=mitochondrialDetailService.selectDuplicateChecking(name);
        return ResultUtil.success(i);
    }

    @GetMapping("/getMitochondrialAndSiteInfo")
    public ResultUtil getMitochondrialDetailAndSiteInfo(){
        Map<String, Object> data=new HashMap<>();
        List<MitochondrialDetail>sampleAll= mitochondrialDetailService.getAllMitochondrialDetail();
        List<String>samples=new ArrayList<>();
        Map<String, List<SiteInfo>> chartDataMap=new HashMap<>();
        for (MitochondrialDetail mitochondrialDetail : sampleAll) {
            List<SiteInfo> sample= mitochondrialDetailService.getMitochondrialDetailDetails(mitochondrialDetail.getOriginal_data_name());
            chartDataMap.put(mitochondrialDetail.getSample_name(),sample);
            samples.add(mitochondrialDetail.getSample_name());
        }
        data.put("chartDataMap",chartDataMap);
        data.put("samples",samples);
        return ResultUtil.success(data);
    }

}

