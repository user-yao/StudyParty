package com.studyParty.AI;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class StudyPartyAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPartyAiApplication.class, args);
    }

}
