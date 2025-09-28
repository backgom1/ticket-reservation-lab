package ticket.app.infra.sse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WaitingLineManagerTest {

    @Autowired
    private WaitingLineManager waitingLineManager;


    @Test
    @DisplayName("예매 별 이벤트를 시작하고 큐에 사용자를 넣는다.")
    void addInQueue() {
        waitingLineManager.addWaitingLine("event-123","roro123");
        int waitingSize = waitingLineManager.waitingEventUserSize("event-123");
        assertThat(waitingSize).isEqualTo(1);
    }

    @Test
    @DisplayName("예매 별 이벤트를 종료한다.")
    void removeWaitingEvent() {
        waitingLineManager.addWaitingLine("event-123","roro123");
        waitingLineManager.removeWaitingEvent("event-123");
        boolean waitingSize = waitingLineManager.isWaitingEvent("event-123");
        assertThat(waitingSize).isFalse();
    }

    @Test
    @DisplayName("예매 별 사용자를 삭제한다.")
    void removeWaitingUser() {
        waitingLineManager.addWaitingLine("event-123","roro123");
        waitingLineManager.removeWaitingUser("event-123","roro123");
        boolean isUserInQueue = waitingLineManager.isWaitingUser("event-123", "roro123");
        assertThat(isUserInQueue).isFalse();
    }
}