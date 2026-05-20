package com.proaula.aula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.proaula.aula.Repository.mongodb")
public class AulaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AulaApplication.class, args);
    }
}