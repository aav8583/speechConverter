package com.fluentspeechapp.speechconverterv1.config;

import com.fluentspeechapp.speechconverterv1.service.BinaryWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class WebSocketBinaryConfig implements WebSocketConfigurer {

    @Bean
    public WebSocketHandler myBinaryWebSocketHandler() {
        return new BinaryWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myBinaryWebSocketHandler(), "/binary-speech-websocket")
                .setAllowedOrigins("*");
    }
}
