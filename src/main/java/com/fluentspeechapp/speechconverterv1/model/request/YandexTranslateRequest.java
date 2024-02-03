package com.fluentspeechapp.speechconverterv1.model.request;

import lombok.Data;

import java.util.List;

@Data
public class YandexTranslateRequest {

    private String sourceLanguageCode;
    private String targetLanguageCode;
    private List<String> texts;

}
