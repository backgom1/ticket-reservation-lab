package ticket.app.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    private String nickname;
    private String email;
    private String password;

    private RegisterRequest(String password, String email, String nickname) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    public static RegisterRequest of(String password, String email, String name) {
        return new RegisterRequest(password, email, name);
    }
}
