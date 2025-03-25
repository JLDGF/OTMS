package com.zjz.onlinetutoringmanagementsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zjz.onlinetutoringmanagementsystem.mapper")
@SpringBootApplication
public class OnlineTutoringManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineTutoringManagementSystemApplication.class, args);
    }


}
