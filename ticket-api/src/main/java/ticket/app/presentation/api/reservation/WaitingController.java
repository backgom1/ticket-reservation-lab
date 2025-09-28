package ticket.app.presentation.api.reservation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WaitingController {

    @PostMapping("/api/v1/waiting/queue")
    public void waitingQueue() {
    }
}
