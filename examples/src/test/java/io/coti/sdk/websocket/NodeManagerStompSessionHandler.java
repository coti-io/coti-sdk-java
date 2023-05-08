package io.coti.sdk.websocket;

import io.coti.sdk.http.NotifyNodeHealthStateChange;
import lombok.SneakyThrows;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

public class NodeManagerStompSessionHandler extends StompSessionHandlerAdapter {
    @SneakyThrows
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/nodes/health/", this);
        System.out.println("connection open, " + session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("connection exception, " + session.getSessionId() + " " + command);
        System.out.println("exception: " + exception);
    }

    @SneakyThrows
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return NotifyNodeHealthStateChange.class;
    }

    @SneakyThrows
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Node " + ((NotifyNodeHealthStateChange) payload).getNodeHash() +
                " is now reported with health state " + ((NotifyNodeHealthStateChange) payload).getReportedHealthState());
    }

}
