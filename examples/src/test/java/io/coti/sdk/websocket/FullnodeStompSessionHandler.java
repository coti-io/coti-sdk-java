package io.coti.sdk.websocket;

import io.coti.sdk.data.NotifyTransactionChange;
import io.coti.sdk.data.TotalTransactionsMessage;
import io.coti.sdk.data.UpdatedBalanceMessage;
import lombok.SneakyThrows;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.io.InputStream;
import java.lang.reflect.Type;

public class FullnodeStompSessionHandler extends StompSessionHandlerAdapter {
    @SneakyThrows
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/transactions", this);
        session.subscribe("/topic/addressTransactions/" + getReceiverAddressHash(), this);
        session.subscribe("/topic/" + getReceiverAddressHash(), this);
        session.subscribe("/topic/transaction/total", this);
        System.out.println("connection open, " + session.getSessionId());
    }

    private String getReceiverAddressHash() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream input = FullnodeStompClient.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(input);
        return config.getString("receiver.address");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("connection exception, " + session.getSessionId() + " " + command);
        System.out.println("exception: " + exception);
    }

    @SneakyThrows
    @Override
    public Type getPayloadType(StompHeaders headers) {
        if (headers.getDestination().equals("/topic/transaction/total")) {
            return TotalTransactionsMessage.class;
        } else if (headers.getDestination().equals("/topic/" + getReceiverAddressHash())) {
            return UpdatedBalanceMessage.class;
        }
        return NotifyTransactionChange.class;
    }

    @SneakyThrows
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String receiverAddress = getReceiverAddressHash();
        if (headers.getDestination().equals("/topic/transactions")) {
            System.out.println("Received transaction " + ((NotifyTransactionChange) payload).getTransactionResponseData().getHash() + " status: " +
                    ((NotifyTransactionChange) payload).getStatus());
        } else if (headers.getDestination().startsWith("/topic/addressTransactions/")) {
            System.out.println("Received addressTransactions " + ((NotifyTransactionChange) payload).getTransactionResponseData().getHash() + " status: " +
                    ((NotifyTransactionChange) payload).getStatus());
        } else if (headers.getDestination().equals("/topic/transaction/total")) {
            System.out.println("Received total transactions number: " + ((TotalTransactionsMessage) payload).getTotalTransactions());
        } else if (headers.getDestination().equals("/topic/" + receiverAddress)) {
            System.out.println("Address " + receiverAddress + "with currency " + ((UpdatedBalanceMessage) payload).getCurrencyHash() +
                    " status: balance " + ((UpdatedBalanceMessage) payload).getBalance() +
                    " and pre balance " + ((UpdatedBalanceMessage) payload).getPreBalance());
        }
    }

}
