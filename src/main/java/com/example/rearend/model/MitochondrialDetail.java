package com.example.rearend.model;


import lombok.Data;

import java.util.Date;

@Data
public class MitochondrialDetail {
    private String sample_name;
    private Date analysis_date;
    private String original_data_name;
}