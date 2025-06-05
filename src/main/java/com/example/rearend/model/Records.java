package com.example.rearend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Records {
    private Integer id;
    private String goal_name;
    private String compare_name;
    private Integer allowance;
    private String time;
    private String original_goal;
    private String original_compare;
    private int status;
}