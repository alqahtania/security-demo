package com.security.securitydemo.service.impl;

import com.security.securitydemo.service.WebauthnRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WebauthnRedisServiceImpl implements WebauthnRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    @Override
    public void saveAssertionOptions(String key, String options) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        ops.set(key, options, 10, TimeUnit.MINUTES); // Expires in 10 minutes
    }

    @Override
    public String getAssertionOptions(String key) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        return ops.get(key);
    }

    @Override
    public void deleteAssertionOptions(String key) {
        redisTemplate.delete(key);
    }
}
