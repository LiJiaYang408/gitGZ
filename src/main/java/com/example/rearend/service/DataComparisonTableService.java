package com.example.rearend.service;

import com.example.rearend.model.DataComparisonTable;

import java.util.List;

public interface DataComparisonTableService {
    Integer insertDataComparison(DataComparisonTable dataComparisonTable);

    List<DataComparisonTable> getAllDataComparisonTable();
}
