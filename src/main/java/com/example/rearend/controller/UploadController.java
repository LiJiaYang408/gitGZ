package com.example.rearend.controller;

import com.example.rearend.utils.ResultUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {


    @PostMapping("/upload")
    public ResultUtil upload( @RequestParam("file") MultipartFile file,
                              @RequestParam("uploadType") String uploadType){

        return ResultUtil.success(null);
    }
}
