package com.fluentspeechapp.speechconverterv1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpeechController {

    @PostMapping("/api/voice-to-text")
    public void convertSpeechToText(@RequestBody String speech) {
        System.out.println(speech);
    }
}
