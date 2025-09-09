package ticket.app.presentation.exception;

import lombok.Getter;
import ticket.app.infra.enums.TokenStatus;

@Getter
public class TokenException extends RuntimeException {

    private final TokenStatus tokenStatus;


    public TokenException(TokenStatus tokenStatus) {
        super(tokenStatus.getDescription());
        this.tokenStatus = tokenStatus;
    }
}
