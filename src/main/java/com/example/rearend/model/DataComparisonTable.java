package com.example.rearend.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DataComparisonTable {
    private Long id;
    private String target_sample_name;
    private String compare_sample_name;
    private Integer step;
    private String haplogroup;
    private Timestamp comparison_time;
    private String file_source;


}