package com.fluentspeechapp.speechconverterv1.controller;

import com.fluentspeechapp.speechconverterv1.model.request.YandexTranslateRequest;
import com.fluentspeechapp.speechconverterv1.model.response.YandexTranslateResponse;
import com.fluentspeechapp.speechconverterv1.service.LevenshteinService;
import com.fluentspeechapp.speechconverterv1.service.TranslateService;
import jakarta.annotation.PreDestroy;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class SpeechWebSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    private final TranslateService translateService;
    private final LevenshteinService levenshteinService;


    private static final long MAX_SILENCE_DURATION = 2000; // максимальное время молчания в миллисекундах
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private String bufferedText = ""; // буфер для накопления текста
    private ScheduledFuture<?> silenceFuture = null; // ссылка на запланированное будущее задание
    private final StringBuilder buffer = new StringBuilder();
    private final int aggregationInterval = 4; // Интервал агрегации в секундах


    public SpeechWebSocketController(TranslateService translateService,
                                     LevenshteinService levenshteinService) {
        this.translateService = translateService;
        this.levenshteinService = levenshteinService;

    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
    }

    @MessageMapping("/speech")
    public void handleSpeech(String message) {
        synchronized (this) {
            bufferedText += message + " ";
            System.err.println(bufferedText);
            translated(bufferedText);
            bufferedText = "";
//            if (silenceFuture == null || silenceFuture.isCancelled() || silenceFuture.isDone()) {
//                silenceFuture = scheduler.schedule(() -> {
//                    String text;
//                    synchronized (this) {
//                        text = levenshteinService.handleMessage(bufferedText.trim());
//                        bufferedText = "";
//                    }
//                    Date date=new Date(System.currentTimeMillis());
//                    System.out.println(date);
//                    System.err.println(text);
////                    translated(text);
//                    silenceFuture = null; // Сброс future после выполнения
//                }, 5, TimeUnit.SECONDS);
//            }
        }
    }

//        silenceFuture = scheduler.schedule(() -> {
//
//            YandexTranslateRequest yandexTranslateRequest = new YandexTranslateRequest();
//            yandexTranslateRequest.setSourceLanguageCode("sr");
//            yandexTranslateRequest.setTargetLanguageCode("ru");
//            yandexTranslateRequest.setTexts(List.of(message));
//            YandexTranslateResponse response = translateService.translate(yandexTranslateRequest);
//            StringBuilder stringBuilder = new StringBuilder();
//
//            for (int i = 0; i < response.getTranslations().size(); i++) {
//                stringBuilder.append(response.getTranslations().get(i).getText());
//                stringBuilder.append(" ");
//            }
//            String translatedResponse = stringBuilder.toString();
//            broadcastText(translatedResponse);
//
//            bufferedText = "";
//        }, MAX_SILENCE_DURATION, TimeUnit.MILLISECONDS);
//    }

    // вызовите этот метод, чтобы отправить сообщение всем подписчикам на "/topic/speechresults"
    public void broadcastText(String text) {
        template.convertAndSend("/topic/speechresults", text);
    }

    private void translated(String message) {
        YandexTranslateRequest yandexTranslateRequest = new YandexTranslateRequest();
        yandexTranslateRequest.setSourceLanguageCode("en");
        yandexTranslateRequest.setTargetLanguageCode("ru");
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

}

