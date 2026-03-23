package com.calero.lili.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.calero.lili.core", "com.calero.lili.api"})
@EnableJpaRepositories(basePackages = {"com.calero.lili.core", "com.calero.lili.api"})
@EntityScan(basePackages = {"com.calero.lili.core", "com.calero.lili.api"})
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
