package com.example.rearend.utils;

import lombok.Data;

@Data
public class CompareResult {
    private String target;
    private String db;
    private String standard;
    private String position;
}