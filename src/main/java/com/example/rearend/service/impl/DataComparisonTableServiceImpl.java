package com.example.rearend.service.impl;

import com.example.rearend.mapper.DataComparisonTableMapper;
import com.example.rearend.model.DataComparisonTable;
import com.example.rearend.service.DataComparisonTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataComparisonTableServiceImpl implements DataComparisonTableService {
    @Autowired
    private DataComparisonTableMapper mapper;
    @Override
    public Integer insertDataComparison(DataComparisonTable dataComparisonTable) {
        return mapper.insertDataComparison(dataComparisonTable);
    }

    @Override
    public List<DataComparisonTable> getAllDataComparisonTable() {
        return mapper.getAllDataComparisonTable();
    }
}
