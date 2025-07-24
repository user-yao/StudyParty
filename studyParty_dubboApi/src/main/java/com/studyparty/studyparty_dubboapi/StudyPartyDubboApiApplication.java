package com.studyparty.studyparty_dubboapi;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableDubbo
@EnableDiscoveryClient
public class StudyPartyDubboApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPartyDubboApiApplication.class, args);
    }

}
