package ticket.app.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.app.infra.annotation.ValidPassword;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    @NotEmpty(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotEmpty(message = "이메일을 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;


    @Size(min = 9, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).*$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자(!@#$%^&*)를 최소 1개 이상 포함해야 합니다."
    )
    @ValidPassword
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
