package com.vodafone.orderapi;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Deployment(resources ={("classpath*:/processes/**/*.bpmn"),("classpath*:/decisions/**/*.dmn")})
//@EnableScheduling
public class OrderapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderapiApplication.class, args);
    }

}
