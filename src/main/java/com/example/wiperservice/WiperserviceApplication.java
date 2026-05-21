package com.example.wiperservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.wiperservice", "kamaz.project.sandbox"})
@EntityScan(basePackages = {"com.example.wiperservice.model", "kamaz.project.sandbox.models"})
@EnableJpaRepositories(basePackages = {"com.example.wiperservice.repository", "kamaz.project.sandbox.repositories"})
public class WiperserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WiperserviceApplication.class, args);
    }
}