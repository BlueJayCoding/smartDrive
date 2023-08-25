package com.selfdrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = "com.selfdrive")
@EnableWebMvc
public class SelfDriveApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfDriveApplication.class, args);
    }

}
