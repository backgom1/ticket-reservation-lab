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


    public static Password of(String rawPassword, PasswordEncoder encoder) {

        if (hasMinLength(rawPassword)) {
            throw new IllegalArgumentException("비밀번호는 9자리 이상이어야합니다.");
        }

        if (PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("숫자, 대문자, 소문자, 특수문자 각 1개 이상 포함해야합니다.");
        }

        String encryptedPassword = encoder.encode(rawPassword);

        // 3. 암호화된 값을 가지고 private 생성자를 호출해 객체를 생성합니다.
        return new Password(encryptedPassword);
    }

    private static boolean hasMinLength(String value) {
        return value.length() <= MIN_LENGTH;
    }

    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.value);
    }

}
