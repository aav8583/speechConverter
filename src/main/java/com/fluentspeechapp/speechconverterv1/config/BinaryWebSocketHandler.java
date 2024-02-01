package com.fluentspeechapp.speechconverterv1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.charset.StandardCharsets;

@Component
public class BinaryWebSocketHandler extends AbstractWebSocketHandler {

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] payload = message.getPayload().array();
        String text = new String(payload, StandardCharsets.UTF_8);
        System.out.println(text);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setBinaryMessageSizeLimit(200 * 1024 * 1024); // Установка размера бинарного сообщения 2MB
        session.setTextMessageSizeLimit(200 * 1024 * 1024); // Установка размера текстового сообщения 2MB
    }
}
