package com.fluentspeechapp.speechconverterv1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
//@EnableWebSocket
public class SpeechConverterV1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpeechConverterV1Application.class, args);
    }

}
