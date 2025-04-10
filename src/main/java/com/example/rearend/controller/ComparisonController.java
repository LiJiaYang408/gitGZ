package com.example.rearend.controller;

import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.CompareService;
import com.example.rearend.service.MitochondrialDetailService;
import com.example.rearend.utils.CompareResult;
import com.example.rearend.utils.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comparison")
public class ComparisonController {

    private final CompareService compareService;
    private final MitochondrialDetailService mitochondrialDetailService;

    public ComparisonController(CompareService compareService, MitochondrialDetailService mitochondrialDetailService) {
        this.compareService = compareService;
        this.mitochondrialDetailService = mitochondrialDetailService;
    }

    //简单比对
    @GetMapping("/compare")
    public List<CompareResult> compare(@RequestParam String sampleName1, @RequestParam String sampleName2) {
        return compareService.compareData(sampleName1, sampleName2);
    }

    //复杂比对
    @GetMapping("/complexityCompare")
    public List<CompareResult> complexityCompare(@RequestParam String sampleName1,
                                                 @RequestParam String name1,
                                                 @RequestParam String sampleName2,
                                                 @RequestParam String name2) {
        List<SiteInfo>list1;
        if (sampleName1.equals("null")){
            list1=compareService.convertToSiteInfoList(compareService.getParsedDataFromRedis(name1));
        }else {
            list1=mitochondrialDetailService.getMitochondrialDetailDetails(sampleName1);
            list1.get(0).setSample_name(name1);
        }
        List<SiteInfo>list2;
        if (sampleName2.equals("null")){
            list2=compareService.convertToSiteInfoList(compareService.getParsedDataFromRedis(name2));
        }else {
            list2=mitochondrialDetailService.getMitochondrialDetailDetails(sampleName2);
            list2.get(0).setSample_name(name2);
        }


        return compareService.complexityCompareData(list1,list2);
    }
    @GetMapping("/getMitochondrialDetailDetails")
    public ResultUtil<List<SiteInfo>> getMitochondrialDetailDetails(@RequestParam String name,@RequestParam String name2){
        List<SiteInfo>list;
        if (name.equals("null")){
            list=compareService.convertToSiteInfoList(compareService.getParsedDataFromRedis(name2));
        }else {
            list=mitochondrialDetailService.getMitochondrialDetailDetails(name);
        }
        return ResultUtil.success(list);
    }
}
