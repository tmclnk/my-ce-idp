package com.example.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CloudEntityClient.class)
public class MyCustomAuthSourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyCustomAuthSourceApplication.class, args);
    }
}
