package com.fluentspeechapp.speechconverterv1.service;

public interface LevenshteinService {  //вынести в отдельный микросервис, потому что очень русурсоемко на больших объемах

    String handleMessage(String incomingFragment);

}
