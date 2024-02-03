package com.fluentspeechapp.speechconverterv1.controller;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Map;

@Controller
public class SpeechWebSocketController {

    @MessageMapping("/speech")
    public void handleSpeech(String message) {
        System.err.println("TEST");
        System.out.println(message);
    }
}

//
//    @MessageMapping("/speech")
////    @SendTo("/topic/response") //эта аннотация отправляет ответы
//    public void handleSpeech(@Payload byte[] message, @Headers Map<String, Object> headers) {
//        System.out.println("Received message with headers: " + headers);
//        System.out.println(Arrays.toString(message));
//        String text = new String(message);
//        System.out.println(text);
//    }

//    @MessageMapping("/speech")
//    public void handleSpeech(@Payload String message) {
//        System.out.println(message);
//        // здесь логика обработки строки, принятой от клиента
//    }

//}

