package com.fluentspeechapp.speechconverterv1.model.request;

import lombok.Data;

import java.util.Map;

@Data
public class MessageDataRequest {

    private String text;
    private String recognitionLang;
    private String translatedLang;

    Map<String, String> languages = Map.of(
            "en-EN", "en",
            "ru-RU", "ru",
            "sr-SR", "sr");

    public String chooseLanguage(String code) {
        String s = this.languages.get(code);
        return s;
    }

}
