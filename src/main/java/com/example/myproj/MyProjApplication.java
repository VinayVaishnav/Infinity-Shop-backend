package com.example.myproj;

import com.example.myproj.config.RazorpayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RazorpayProperties.class)
public class MyProjApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyProjApplication.class, args);
    }

}
