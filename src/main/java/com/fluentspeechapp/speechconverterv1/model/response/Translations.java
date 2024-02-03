package com.fluentspeechapp.speechconverterv1.model.response;

import lombok.Data;

@Data
public class Translations {

    private String text;
    private String detectedLanguageCode;

}
