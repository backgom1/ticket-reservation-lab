package ticket.app.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {


    /**
     * 토큰 인증 관련 예외 핸들링 메서드
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenException.class)
    public ProblemDetail handleTokenException(TokenException e) {
        log.error("TokenException : {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getTokenStatus().getDescription());
    }
}
