package com.fluentspeechapp.speechconverterv1.service;

import org.springframework.web.socket.BinaryMessage;

public interface OpusService {

    void saveTempOpusFile(BinaryMessage message);
}
