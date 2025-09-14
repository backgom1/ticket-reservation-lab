package ticket.app.infra.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import ticket.app.infra.annotation.ValidPassword;

public class PasswordValidator implements ConstraintValidator<ValidPassword, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {


        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        String username = (String) beanWrapper.getPropertyValue("username");
        String password = (String) beanWrapper.getPropertyValue("password");

        if (password == null || password.isEmpty()) return true;
        if (isPasswordEqualToUsername(context, username, password)) return false;
        if (hasFourConsecutiveIdenticalChars(context, password)) return false;
        return !hasSequentialKeyboardChars(context, password);
    }

    private boolean hasSequentialKeyboardChars(ConstraintValidatorContext context, String password) {
        String sequentialPatterns = "abcdefghijklmnopqrstuvwxyz0123456789qwertyuiopasdfghjklzxcvbnm";
        for (int i = 0; i <= sequentialPatterns.length() - 4; i++) {
            String pattern = sequentialPatterns.substring(i, i + 4);
            if (password.toLowerCase().contains(pattern)) {
                addConstraintViolation(context, "키보드 연속 문자를 4자 이상 사용할 수 없습니다.");
                return true;
            }
        }
        return false;
    }

    private boolean hasFourConsecutiveIdenticalChars(ConstraintValidatorContext context, String password) {
        if (password.matches(".*(.)\\1{3,}.*")) {
            addConstraintViolation(context, "동일한 문자를 4번 이상 연속으로 사용할 수 없습니다.");
            return true;
        }
        return false;
    }

    private boolean isPasswordEqualToUsername(ConstraintValidatorContext context, String username, String password) {
        if (username != null && username.equals(password)) {
            addConstraintViolation(context, "비밀번호는 ID와 동일할 수 없습니다.");
            return true;
        }
        return false;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
