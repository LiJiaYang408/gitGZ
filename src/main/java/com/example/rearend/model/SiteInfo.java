package com.example.rearend.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SiteInfo {
    private Integer id;
    private String original_data_name;
    private Integer base_position;
    private String reference_base;
    private String mutant_base;
    private Integer total_depth;
    private BigDecimal heterogeneity;
    private String type;

}