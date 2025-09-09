package ticket.app.application.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.app.domain.model.Member;
import ticket.app.domain.model.Nickname;
import ticket.app.domain.model.Password;
import ticket.app.domain.model.SecurityMember;
import ticket.app.infra.enums.TokenStatus;
import ticket.app.infra.repository.MemberJpaRepository;
import ticket.app.infra.util.CookieUtil;
import ticket.app.infra.util.JwtRedisTokenStorage;
import ticket.app.infra.util.JwtTokenProvider;
import ticket.app.presentation.dto.LoginRequest;
import ticket.app.presentation.dto.LoginResponse;
import ticket.app.presentation.dto.RefreshResponse;
import ticket.app.presentation.exception.TokenException;

import java.util.Date;


/**
 * 사용자 관련 인증을 담당하는 비지니스 클래스
 *
 * @author eunsung
 * @since 2025-09-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberJpaRepository memberJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRedisTokenStorage jwtRedisTokenStorage;


    @Transactional
    public LoginResponse login(LoginRequest request) {

        Password password = Password.of(request.getPassword(), request.getNickname(), new BCryptPasswordEncoder());
        Nickname nickname = new Nickname(request.getNickname());

        Member member = memberJpaRepository.findByPasswordAndNickname(password, nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 및 비밀번호가 올바르지 않습니다."));

        SecurityMember userDetails = SecurityMember.of(member);

        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        Claims claims = jwtTokenProvider.getClaims(refreshToken);
        String refreshId = claims.getId();
        Date expiration = claims.getExpiration();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails, refreshId);

        jwtRedisTokenStorage.saveRefreshToken(refreshId, refreshToken, expiration.getTime());

        return new LoginResponse(accessToken);
    }

    @Transactional
    public RefreshResponse refreshToken(HttpServletRequest request) {
        String accessToken = CookieUtil.getAccessTokenCookie(request.getCookies());
        Claims accessTokenClaims = jwtTokenProvider.getExpiredClaims(accessToken);
        Long id = accessTokenClaims.get("id", Long.class);
        String name = accessTokenClaims.get("name", String.class);
        String refreshId = accessTokenClaims.get("refreshId", String.class);

        String refreshToken = jwtRedisTokenStorage.getRefreshToken(refreshId).
                orElseThrow(() -> new IllegalArgumentException("올바르지 않은 리프래시 토큰입니다."));

        if (jwtTokenProvider.validateToken(refreshToken) == TokenStatus.INVALID) {
            throw new TokenException(TokenStatus.INVALID);
        }

        if (jwtRedisTokenStorage.isBlacklisted(refreshToken)) {
            throw new TokenException(TokenStatus.BLOCKED);
        }

        Claims refreshTokenClaims = jwtTokenProvider.getRefreshClaims(refreshToken);
        String userId = refreshTokenClaims.getSubject();
        Date refreshExpiration = refreshTokenClaims.getExpiration();
        Integer updateRotation = refreshTokenClaims.get("updateRotation", Integer.class);

        if (refreshExpiration.before(new Date(System.currentTimeMillis()))) {
            String newRefreshTokens = jwtTokenProvider.generateNewRefreshTokens(userId, refreshExpiration.getTime(), updateRotation);
            boolean replaceRefreshToken = jwtRedisTokenStorage.replaceRefreshToken(userId, newRefreshTokens, refreshExpiration.getTime());
        }
        Member member = memberJpaRepository.findByIdAndNickname(id, new Nickname(name))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 토큰 정보입니다."));

        SecurityMember securityUser = SecurityMember.of(member);
        String newAccessToken = jwtTokenProvider.generateAccessToken(securityUser, refreshId);
        return new RefreshResponse(newAccessToken);
    }
}
