package ticket.app.presentation.api.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ticket.app.application.service.MemberAuthService;
import ticket.app.infra.util.ApiResponse;
import ticket.app.infra.util.CookieUtil;
import ticket.app.presentation.dto.LoginRequest;
import ticket.app.presentation.dto.LoginResponse;
import ticket.app.presentation.dto.RefreshResponse;

/**
 * 사용자 계정 인증을 담당하는 컨트롤러 클래스
 *
 * @author eunsung
 * @since 2025-08-25
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthMemberApiController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/api/v1/auth/user/login")
    public ApiResponse<LoginResponse> login(HttpServletResponse response, @RequestBody LoginRequest request) {
        //httpOnly는 리액트 및 자바스크립트에서 적용할수 없기때문에 (Express.js를 사용하면 가능) 서버측에서 보내줘야한다.
        LoginResponse login = memberAuthService.login(request);
        CookieUtil.addAccessTokenCookie(response, login.getAccessToken(), 60 * 60 * 24);
        return ApiResponse.success(login, "로그인을 성공했습니다.");
    }

    @PostMapping("/api/v1/auth/user/refresh")
    public ApiResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteAccessTokenCookie(response);
        RefreshResponse refreshResponse = memberAuthService.refreshToken(request);
        CookieUtil.addAccessTokenCookie(response, refreshResponse.getAccessToken(), 60 * 60 * 24);
        return ApiResponse.success("토큰 갱신을 완료했습니다.");
    }

}
