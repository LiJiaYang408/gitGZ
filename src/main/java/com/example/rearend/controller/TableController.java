package com.example.rearend.controller;

import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.TableService;
import com.example.rearend.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/table")
public class TableController {
    @Autowired
    private TableService tableService;

    @GetMapping("/getMitochondrialDetailAll")
    public ResultUtil getMitochondrialDetailAll(){
        List<MitochondrialDetail>list=tableService.getAllMitochondrialDetail();
        return ResultUtil.success(list);
    }

    @GetMapping("/getMitochondrialDetailDetails")
    public ResultUtil getMitochondrialDetailDetails(@RequestParam String name){
        List<SiteInfo>list=tableService.getMitochondrialDetailDetails(name);
        return ResultUtil.success(list);
    }

    @GetMapping("/getMitochondrialDetailAndSiteInfo")
    public ResultUtil getMitochondrialDetailAndSiteInfo(){
        Map data=new HashMap();
        List<MitochondrialDetail>sampleAll=tableService.getAllMitochondrialDetail();
        List<String>samples=new ArrayList<>();
        Map chartDataMap=new HashMap();
        for (MitochondrialDetail mitochondrialDetail : sampleAll) {
            List<SiteInfo> sample=tableService.getMitochondrialDetailDetails(mitochondrialDetail.getSample_name());
            chartDataMap.put(mitochondrialDetail.getSample_name(),sample);
            samples.add(mitochondrialDetail.getSample_name());
        }
        data.put("chartDataMap",chartDataMap);
        data.put("samples",samples);
        return ResultUtil.success(data);
    }
}
