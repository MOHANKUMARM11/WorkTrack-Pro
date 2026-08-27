package com.worktrack.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    @MessageMapping("/ping")
    @SendTo("/topic/ping")
    public String ping(@Payload String message) {
        log.info("Received WebSocket STOMP ping message: {}", message);
        return "PONG: " + message;
    }
}
