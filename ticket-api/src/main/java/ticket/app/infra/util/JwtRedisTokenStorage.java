package ticket.app.infra.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtRedisTokenStorage {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String refreshId, String refreshToken, long expiration) {
        String key = "refresh_token:" + refreshId;
        long ttl = expiration - System.currentTimeMillis();

        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(ttl));
        }
    }

    public boolean replaceRefreshToken(String userId, String newRefreshToken, long newExpiration) {
        String key = "refresh_token:" + userId;

        // 기존 토큰 존재 여부 확인
        boolean hasExistingToken = redisTemplate.hasKey(key);

        // 새 토큰 저장 (기존 토큰이 있든 없든 덮어쓰기)
        long ttl = newExpiration - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, newRefreshToken, Duration.ofMillis(ttl));
        }

        return hasExistingToken;
    }

    public Optional<String> getRefreshToken(String userId) {
        String key = "refresh_token:" + userId;
        String token = (String) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }

    public void deleteRefreshToken(String userId) {
        String key = "refresh_token:" + userId;
        redisTemplate.delete(key);
    }


    public void addToBlacklist(String jwtId, long expiration) {

        long ttl = expiration - System.currentTimeMillis();

        if (ttl > 0) {
            // JWT 전체가 아닌 ID만 저장
            redisTemplate.opsForValue().set("blacklist:" + jwtId, "revoked", Duration.ofMillis(ttl));
        }
    }

    public boolean isBlacklisted(String jwtId) {
        return redisTemplate.hasKey("blacklist:" + jwtId);
    }

}
