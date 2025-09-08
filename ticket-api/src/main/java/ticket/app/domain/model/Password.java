package ticket.app.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Password {
    private static final int MIN_LENGTH = 9;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    private String value;

    private Password(String value) {
        this.value = value;
    }


    public static Password of(String rawPassword, String nickname, PasswordEncoder encoder) {

        if (isSamePassword(rawPassword, nickname)) {
            throw new IllegalArgumentException("사용자 ID와 비밀번호가 동일합니다.");
        }

        if (hasMinLength(rawPassword)) {
            throw new IllegalArgumentException("비밀번호는 9자리 이상이어야합니다.");
        }

        if (!PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("숫자, 대문자, 소문자, 특수문자 각 1개 이상 포함해야합니다.");
        }


        String encryptedPassword = encoder.encode(rawPassword);


        return new Password(encryptedPassword);
    }

    private static boolean hasMinLength(String value) {
        return value.length() < MIN_LENGTH;
    }

    private static boolean isSamePassword(String rawPassword, String nickname) {
        return rawPassword.equals(nickname);
    }

    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.value);
    }

    public Password changePassword(String rawPassword, PasswordEncoder encoder) {
        return of(rawPassword, this.value, encoder);
    }


}
