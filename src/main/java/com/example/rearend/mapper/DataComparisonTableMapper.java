package com.example.rearend.mapper;

import com.example.rearend.model.DataComparisonTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DataComparisonTableMapper {
    Integer insertDataComparison(DataComparisonTable dataComparisonTable);

    @Select("select * from data_comparison_table")
    List<DataComparisonTable> getAllDataComparisonTable();
}
