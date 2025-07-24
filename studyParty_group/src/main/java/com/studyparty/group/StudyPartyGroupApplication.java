package com.studyparty.group;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableDubbo
public class StudyPartyGroupApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPartyGroupApplication.class, args);
    }

}
