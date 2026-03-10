package com.floodrescue.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.floodrescue.backend") // Ép quét Entity ở đây
@EnableJpaRepositories(basePackages = "com.floodrescue.backend") // Ép quét Repository ở đây
public class BackendApplication {

    public static void main(String[] args) {
        System.setProperty("aws.java.v1.disableEc2Metadata", "true");
        System.setProperty("aws.disableEc2Metadata", "true");
        SpringApplication.run(BackendApplication.class, args);
    }

}