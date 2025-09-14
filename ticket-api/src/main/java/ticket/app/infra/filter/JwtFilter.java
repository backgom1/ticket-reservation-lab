package ticket.app.infra.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ticket.app.domain.model.Member;
import ticket.app.domain.model.SecurityMember;
import ticket.app.infra.enums.TokenStatus;
import ticket.app.infra.util.ApiResponse;
import ticket.app.infra.util.CookieUtil;
import ticket.app.infra.util.JwtTokenProvider;

import java.io.IOException;

import static ticket.app.infra.enums.TokenStatus.*;


public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String accessToken = CookieUtil.getAccessTokenCookie(request.getCookies());

        TokenStatus tokenStatus = jwtTokenProvider.validateToken(accessToken);

        if (accessToken == null || accessToken.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    INVALID.getDescription(), INVALID.getCode());
            return;
        }


        switch (tokenStatus) {
            case INVALID -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String responseJson = objectMapper.writeValueAsString(ApiResponse.failure(INVALID.getDescription(), INVALID.getCode()));
                response.getWriter().write(responseJson);
                response.getWriter().flush();
                return;
            }
            case EXPIRED -> {
                response.setStatus(HttpServletResponse.SC_OK);
                String responseJson = objectMapper.writeValueAsString(ApiResponse.failure(EXPIRED.getDescription(), EXPIRED.getCode()));
                response.getWriter().write(responseJson);
                response.getWriter().flush();
                return;
            }

            case REQUIRED_REFRESH_TOKEN -> {
                response.setStatus(HttpServletResponse.SC_OK);
                String responseJson = objectMapper.writeValueAsString(ApiResponse.failure(REQUIRED_REFRESH_TOKEN.getDescription(), REQUIRED_REFRESH_TOKEN.getCode()));
                response.getWriter().write(responseJson);
                response.getWriter().flush();
                return;
            }
            default -> {
                Claims claims = jwtTokenProvider.getClaims(accessToken);
                Long id = claims.get("id", Long.class);
                String name = (String) claims.get("name");
                String email = (String) claims.get("email");
                //패스워드는 인증의 역할을 하기때문에 굳이 객체안에 값을 넣어줄 필요가 없다.
                Member member = Member.ofClaims(id, name, email);
                SecurityMember securityUser = SecurityMember.of(member);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        securityUser, null, securityUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * 에러 응답을 클라이언트에 전송하는 헬퍼 메서드
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message, String code) throws IOException {
        response.setStatus(status);
        String responseJson = objectMapper.writeValueAsString(ApiResponse.failure(message, code));
        response.getWriter().write(responseJson);
        response.getWriter().flush();
    }

}
