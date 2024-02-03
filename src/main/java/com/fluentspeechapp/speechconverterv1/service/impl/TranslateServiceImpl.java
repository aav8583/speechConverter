package com.fluentspeechapp.speechconverterv1.service.impl;

import com.fluentspeechapp.speechconverterv1.model.request.YandexTranslateRequest;
import com.fluentspeechapp.speechconverterv1.model.response.Translations;
import com.fluentspeechapp.speechconverterv1.model.response.YandexTranslateResponse;
import com.fluentspeechapp.speechconverterv1.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslateServiceImpl implements TranslateService {

    private final RestTemplate restTemplate;

    @Override
    public YandexTranslateResponse translate(YandexTranslateRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Api-Key ");

        HttpEntity<YandexTranslateRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<YandexTranslateResponse> response = restTemplate.exchange(
                "https://translate.api.cloud.yandex.net/translate/v2/translate",
                HttpMethod.POST,
                entity,
                YandexTranslateResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
            // Обработка успешного ответа
        } else {
            YandexTranslateResponse response1 = new YandexTranslateResponse();
            Translations translations = new Translations();
            translations.setText("не вышло");
            response1.setTranslations(List.of(translations));
            return response1;
        }

    }
}
