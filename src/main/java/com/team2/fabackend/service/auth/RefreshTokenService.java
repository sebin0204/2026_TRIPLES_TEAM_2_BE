package com.team2.fabackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    private String getKey(Long userId) {
        return "refresh_token:" + userId;
    }

    public void saveRefreshToken(Long userId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(getKey(userId), refreshToken, ttl);
    }

    public boolean validateRefreshToken(Long userId, String token) {
        String savedToken = redisTemplate.opsForValue().get(getKey(userId));
        return savedToken != null && savedToken.equals(token);
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(getKey(userId));
    }
}
