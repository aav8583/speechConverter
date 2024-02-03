package com.fluentspeechapp.speechconverterv1.model.response;

import lombok.Data;

import java.util.List;

@Data
public class YandexTranslateResponse {

    List<Translations> translations;

}
