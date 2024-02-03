package com.fluentspeechapp.speechconverterv1.controller;

import com.fluentspeechapp.speechconverterv1.model.request.YandexTranslateRequest;
import com.fluentspeechapp.speechconverterv1.model.response.YandexTranslateResponse;
import com.fluentspeechapp.speechconverterv1.service.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class SpeechWebSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    private final TranslateService translateService;

    public SpeechWebSocketController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @MessageMapping("/speech")
    public void handleSpeech(String message) {
//        System.err.println("TEST");
//        System.out.println(message);
        YandexTranslateRequest yandexTranslateRequest = new YandexTranslateRequest();
        yandexTranslateRequest.setSourceLanguageCode("ru");
        yandexTranslateRequest.setTargetLanguageCode("en");
        yandexTranslateRequest.setTexts(List.of(message));
        YandexTranslateResponse response = translateService.translate(yandexTranslateRequest);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < response.getTranslations().size(); i++) {
            stringBuilder.append(response.getTranslations().get(i).getText());
            stringBuilder.append(" ");
        }
        String translatedResponse = stringBuilder.toString();
        broadcastText(translatedResponse);
    }



    // вызовите этот метод, чтобы отправить сообщение всем подписчикам на "/topic/speechresults"
    public void broadcastText(String text) {
        template.convertAndSend("/topic/speechresults", text);
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

