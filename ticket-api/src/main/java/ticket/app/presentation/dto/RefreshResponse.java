package ticket.app.presentation.dto;

import lombok.Getter;

@Getter
public class RefreshResponse {

    private final String accessToken;

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
