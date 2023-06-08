package io.coti.sdk.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FullNodeStompClient {

    public static void main(String[] args) throws ConfigurationException {
        String url = getWebsocketUrl();

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockjsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockjsClient);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);

        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

        StompSessionHandler sessionHandler = new FullNodeStompSessionHandler();
        stompClient.connect(url, sessionHandler);

        new Scanner(System.in).nextLine(); // Don't close immediately.

    }

    private static String getWebsocketUrl() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream input = FullNodeStompClient.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(input);
        String fullNodeAddress = config.getString("full.node.backend.address");
        return "ws://".concat(fullNodeAddress.split("/")[2]).concat("/websocket");
    }
}

