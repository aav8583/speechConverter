package com.fluentspeechapp.speechconverterv1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class BinaryWebSocketHandler extends AbstractWebSocketHandler {

    private OpusService opusService;
    private SpeechRecognitionService speechRecognitionService;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        System.out.println("ПРОВЕРКА");
        executorService.schedule(() -> {
            byte[] payload = message.getPayload().array();
            speechRecognitionService.streamingRecognizeSpeech(payload);
        }, 2900, TimeUnit.MILLISECONDS);
//        int chunkSize = 32 * 1024; // 2KB
//        int bufferSize = payload.length;
//        int offset = 0;
//
//        while (offset < bufferSize) {
//            int nextChunkSize = Math.min(bufferSize - offset, chunkSize);
//            // Создание чанка
//            byte[] chunk = Arrays.copyOfRange(payload, offset, offset + nextChunkSize);
//
//            speechRecognitionService.streamingRecognizeSpeech(chunk);
//            // Обновление смещения для следующего чанка
//            offset += nextChunkSize;
//        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setBinaryMessageSizeLimit(200 * 1024 * 1024); // Установка размера бинарного сообщения 2MB
        session.setTextMessageSizeLimit(200 * 1024 * 1024); // Установка размера текстового сообщения 2MB
    }

    @Autowired
    public void setOpusService(OpusService opusService) {
        this.opusService = opusService;
    }

    @Autowired
    public void setSpeechRecognitionService(SpeechRecognitionService speechRecognitionService) {
        this.speechRecognitionService = speechRecognitionService;
    }
}

//        int chunkSize = 256 * 1024; // 2KB
//        int bufferSize = payload.length;
//        int offset = 0;
//
//        while (offset < bufferSize) {
//            int nextChunkSize = Math.min(bufferSize - offset, chunkSize);
//            // Создание чанка
//            byte[] chunk = Arrays.copyOfRange(payload, offset, offset + nextChunkSize);
//
//            speechRecognitionService.streamingRecognizeSpeech(payload);
//            // Обновление смещения для следующего чанка
//            offset += nextChunkSize;
//        }