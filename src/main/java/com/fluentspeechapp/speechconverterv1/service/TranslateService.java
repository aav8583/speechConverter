package com.fluentspeechapp.speechconverterv1.service;

import com.fluentspeechapp.speechconverterv1.model.request.YandexTranslateRequest;
import com.fluentspeechapp.speechconverterv1.model.response.YandexTranslateResponse;

public interface TranslateService {

    YandexTranslateResponse translate(YandexTranslateRequest request);

}
