package ticket.app.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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


    /**
     * Valid 어노테이션에서 받아온 예외 반환 클래스
     * @return 메세지 정보 확인
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> detailMessages = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            detailMessages.put(error.getField(), error.getDefaultMessage());
        });
        log.error("MethodArgumentNotValidException : {}", detailMessages);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessages.toString());
    }
}
