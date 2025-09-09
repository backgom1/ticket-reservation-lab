package ticket.app.presentation.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String nickname;
    private String password;
}
