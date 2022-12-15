package io.coti.sdk.websocket;

import io.coti.sdk.data.NotifyTransactionChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@Slf4j
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/transactions", this);
        log.info("connection open, {}", session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.info("connection exception, {} {}", session.getSessionId(), command);
        log.error("exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return NotifyTransactionChange.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("Received transaction {} status: {}", ((NotifyTransactionChange) payload).getTransactionResponseData().getHash(),
                ((NotifyTransactionChange) payload).getStatus());
    }
}
