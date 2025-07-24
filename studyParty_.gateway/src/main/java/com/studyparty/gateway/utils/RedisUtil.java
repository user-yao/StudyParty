package com.studyparty.gateway.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RedisUtil {
    @Autowired
    private ReactiveRedisOperations<String, String> reactiveRedisOperations;


    public Mono<Void> saveValue(String key, String value) {
        return reactiveRedisOperations.opsForValue().set(key, value).then();
    }

    public Mono<String> getValue(String key) {
        return reactiveRedisOperations.opsForValue().get(key);
    }
}
