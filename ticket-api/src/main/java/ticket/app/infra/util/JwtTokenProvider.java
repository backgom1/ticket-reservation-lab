package ticket.app.infra.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ticket.app.domain.model.Member;
import ticket.app.domain.model.SecurityMember;
import ticket.app.infra.enums.TokenStatus;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secret;

    private SecretKey key;

    @Value("${jwt.access-token-validity-ms}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity-ms}")
    private long refreshTokenValidityInMilliseconds;

    @Value("${jwt.rotation.absolute-max-days}")
    private int absoluteMaxDays;

    @Value("${jwt.rotation.max-rotations}")
    private int maxRotations;

    @Value("${jwt.rotation.inactivity-limit-days}")
    private int inactivityLimitDays;

    @Value("${jwt.rotation.rotation-threshold-minutes}")
    private int thresholdMinutes;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes) long for HS256 algorithm.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(SecurityMember user, String refreshId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(user.getPassword())
                .claim("id", (Long) user.id())
                .claim("name", user.username())
                .claim("refreshId", refreshId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 10_000))
                .signWith(key) // 0.12.xx 버전은 자동으로 알고리즘 키를 유추하여 넣어 주기때문에 기존 알고리즘 선택 방식은 올바르지 않다. -> 타입 안정성
                .compact();
    }

    public String generateRefreshToken(SecurityMember user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .claim("updateRotation", 0)
                .expiration(new Date(now.getTime() + refreshTokenValidityInMilliseconds))
                .signWith(key)//자동으로 알고리즘 키를 유추하여 넣어 주기때문에 기존 알고리즘 선택 방식은 올바르지 않다. -> 타입 안정성
                .compact();

    }

    public TokenStatus validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return TokenStatus.PASS;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            return TokenStatus.INVALID;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
            return TokenStatus.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            return TokenStatus.UNSUPPORTED;
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid: {}", e.getMessage());
            return TokenStatus.INVALID;
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Failed to get claims from token: {}", e.getMessage());
            throw new RuntimeException("에러");
        }
    }

    public Claims getRefreshClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            log.error("Failed to get claims from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }

    public Claims getExpiredClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(Integer.MAX_VALUE) // 아주 큰 시간 오차를 허용하여 만료 검사 우회
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("에러");
        }
    }


    public String generateNewRefreshTokens(String userId, Long originalIssueTime, Integer rotationCount) {

        long originalTime = originalIssueTime != null ? originalIssueTime : System.currentTimeMillis();
        int currentRotationCount = rotationCount != null ? rotationCount + 1 : 1;

        Date expiration = new Date(originalTime);
        return Jwts.builder()
                .subject(userId)
                .claim("originalIssueTime", originalTime)
                .claim("rotationCount", currentRotationCount)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

}
