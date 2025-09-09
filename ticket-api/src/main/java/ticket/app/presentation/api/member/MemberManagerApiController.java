package ticket.app.presentation.api.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ticket.app.application.service.MemberManagementService;
import ticket.app.infra.util.ApiResponse;
import ticket.app.presentation.dto.RegisterRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberManagerApiController {

    private final MemberManagementService memberManagementService;

    @PostMapping("/api/v1/account/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        memberManagementService.
                register(request);
        return ApiResponse.success("회원가입이 완료되었습니다.");
    }
}
