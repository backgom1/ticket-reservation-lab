package ticket.app.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    @Test
    @DisplayName("비밀번호가 올바르게 암호화 되는지 확인한다")
    void passwordIsEncrypted() {
        String testPassword = "Aa123456789!";
        Password password = Password.of(testPassword, "test", new BCryptPasswordEncoder());
        assertThat(password.matches(testPassword, new BCryptPasswordEncoder())).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 9자리를 넘는지 확인하는 실패 테스트")
    void hasMinLength() {
        String testPassword = "A123456";
        assertThatThrownBy(() -> Password.of(testPassword, "test", new BCryptPasswordEncoder()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 9자리 이상이어야합니다.");
    }

    @Test
    @DisplayName("숫자, 대문자, 소문자, 특수문자 각 1개 이상 포함하는 실패 테스트")
    void validatePassword() {
        String testPassword = "A1234561111";
        assertThatThrownBy(() -> Password.of(testPassword, "test", new BCryptPasswordEncoder()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("숫자, 대문자, 소문자, 특수문자 각 1개 이상 포함해야합니다.");
    }


    @Test
    @DisplayName("사용자 ID와 비밀번호가 동일 실패 테스트")
    void isSamePassword() {
        String testPassword = "test";
        assertThatThrownBy(() -> Password.of(testPassword, "test", new BCryptPasswordEncoder()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 ID와 비밀번호가 동일합니다.");
    }


}