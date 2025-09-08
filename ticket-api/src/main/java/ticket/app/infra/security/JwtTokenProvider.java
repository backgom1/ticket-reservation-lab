package ticket.app.infra.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ticket.app.domain.model.Member;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

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

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes) long for HS256 algorithm.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }



    public String generateAccessToken(Member user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getNickname().value())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenValidityInMilliseconds))
                .signWith(key) // 0.12.xx 버전은 자동으로 알고리즘 키를 유추하여 넣어 주기때문에 기존 알고리즘 선택 방식은 올바르지 않다. -> 타입 안정성
                .compact();
    }

    public String generateRefreshToken(Member user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getNickname().value())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenValidityInMilliseconds))
                .signWith(key)//자동으로 알고리즘 키를 유추하여 넣어 주기때문에 기존 알고리즘 선택 방식은 올바르지 않다. -> 타입 안정성
                .compact();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid: {}", e.getMessage());
        }
        return false;
    }

}
