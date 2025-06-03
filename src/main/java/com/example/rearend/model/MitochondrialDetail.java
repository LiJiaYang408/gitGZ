package com.example.rearend.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MitochondrialDetail {
    private String sample_name;
    private String analysis_date;
    private String original_data_name;
}