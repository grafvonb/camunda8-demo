package com.boczek.c8demo.microservices.one;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MicroserviceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceOneApplication.class, args);
    }

    @Bean
    public ApplicationRunner initializeDatabase(OneEntityService service) {
        return args -> service.initializeWithSomeEntities();
    }
}
