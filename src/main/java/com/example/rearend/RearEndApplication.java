package com.example.rearend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.rearend.mapper")
public class RearEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(RearEndApplication.class, args);
    }

}
