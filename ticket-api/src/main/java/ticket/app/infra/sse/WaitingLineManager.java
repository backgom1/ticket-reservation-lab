package ticket.app.infra.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 사용자의 대기열을 담당하는 매니저 클래스
 *
 * @author eunsung
 * @since 2025-09-28
 */
@Component
public class WaitingLineManager {

    private final Map<String, Map<String, SseEmitter>> waitingEvent = new ConcurrentHashMap<>();

    public void addWaitingLine(String eventId, String userId) {
        SseEmitter emitter = new SseEmitter(30_000L);
        Map<String, SseEmitter> userIdEmitter = new HashMap<>();
        userIdEmitter.put(userId, emitter);
        waitingEvent.put(eventId, userIdEmitter);
    }

    public void removeWaitingEvent(String eventId) {
        waitingEvent.remove(eventId);
    }

    public void removeWaitingUser(String eventId, String userId) {
        getUserId(eventId, userId).complete();
        getEventId(eventId).remove(userId);
    }

    public int waitingEventUserSize(String eventId) {
        return getEventId(eventId).size();
    }

    public boolean isWaitingEvent(String eventId) {
        return waitingEvent.containsKey(eventId);
    }

    public boolean isWaitingUser(String eventId, String userId) {
        return getEventId(eventId).containsKey(userId);
    }

    private Map<String, SseEmitter> getEventId(String eventId) {
        return waitingEvent.get(eventId);
    }

    private SseEmitter getUserId(String eventId, String userId) {
        return getEventId(eventId).get(userId);
    }

    public void redirectReservation() {
    }
}
