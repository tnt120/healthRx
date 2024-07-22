package com.healthrx.backend.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long EXPIRATION = 30L;

    public void saveRefreshToken(String email, String token) {
        redisTemplate.opsForValue().set(email, token, EXPIRATION, TimeUnit.DAYS);
    }

    public String getRefreshToken(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }
}
