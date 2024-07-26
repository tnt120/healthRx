package com.healthrx.backend.security.service;

import com.healthrx.backend.security.util.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long REFRESH_EXPIRATION = 30L;
    private static final long VERIFICATION_EXPIRATION = 1L;

    public void saveToken(String email, String token, TokenType tokenType) {
        if (tokenType == TokenType.REFRESH) {
            System.out.printf("Adding refresh token to redis for email %s%n", email);
            redisTemplate.opsForValue().set(tokenType + "_" + email, token, REFRESH_EXPIRATION, TimeUnit.DAYS);
        } else if (tokenType == TokenType.VERIFICATION) {
            System.out.printf("Adding verification token to redis for email %s%n", email);
            redisTemplate.opsForValue().set(tokenType + "_" + email, token, VERIFICATION_EXPIRATION, TimeUnit.DAYS);
        }
    }

    public String getToken(String email, TokenType tokenType) {
        return (String) redisTemplate.opsForValue().get(tokenType + "_" + email);
    }
}
