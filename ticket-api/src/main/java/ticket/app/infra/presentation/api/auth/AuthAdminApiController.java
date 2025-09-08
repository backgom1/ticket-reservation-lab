package ticket.app.infra.presentation.api.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket.app.infra.application.service.AdminAuthService;

/**
 * 관리자 계정 인증을 담당하는 컨트롤러 클래스
 * @since 2025-08-25
 * @author eunsung
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthAdminApiController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/api/v1/login")
    public void login(){

    }
}
