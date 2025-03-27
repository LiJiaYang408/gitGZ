package com.example.rearend.controller;

import com.example.rearend.model.DataComparisonTable;
import com.example.rearend.service.CompareService;
import com.example.rearend.service.DataComparisonTableService;
import com.example.rearend.utils.CompareResult;
import com.example.rearend.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comparison")
public class ComparisonController {
    @Autowired
    private DataComparisonTableService comparisonTableService;
    @Autowired
    private CompareService compareService;

    @GetMapping("/getAllCom")
    public ResultUtil getAllCom(){
        List<DataComparisonTable>list=comparisonTableService.getAllDataComparisonTable();
        return ResultUtil.success(list);
    }

    @GetMapping("/compare")
    public List<CompareResult> compare(@RequestParam String sampleName1, @RequestParam String sampleName2) {
        return compareService.compareData(sampleName1, sampleName2);
    }
}
