package com.fluentspeechapp.speechconverterv1.service;

import java.nio.file.Path;

public interface SpeechRecognitionService {

    public String recognizeSpeechFromWavFile(Path wavFilePath);

    void streamingRecognizeSpeech(byte[] data);

    void streamingMicRecognize(byte[] payload) throws Exception;

}
