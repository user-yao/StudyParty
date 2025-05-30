package com.studyParty.user.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveToRedis(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getFromRedis(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}