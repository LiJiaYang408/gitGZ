package com.example.rearend.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MitochondrialDetail {
    private String sample_name;
    private LocalDateTime analysis_date;
    private String original_data_name;
}