package com.fluentspeechapp.speechconverterv1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("simple")
    public String getMainPage() {
        return "forward:/html/simple.html";
    }

    @GetMapping("websocket")
    public String getWebSocket() {
        return "forward:/html/websocket.html";
    }

}
