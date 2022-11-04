package com.example.idp;

import com.example.idp.cloudentity.CloudEntityClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CloudEntityClient.class)
public class IDPApplication {
    public static void main(String[] args) {
        SpringApplication.run(IDPApplication.class, args);
    }
}
