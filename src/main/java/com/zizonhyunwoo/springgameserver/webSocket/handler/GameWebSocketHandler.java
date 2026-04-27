package com.zizonhyunwoo.springgameserver.webSocket.handler;

import com.zizonhyunwoo.springgameserver.webSocket.dto.GameMessageDto;
import com.zizonhyunwoo.springgameserver.webSocket.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Map<String,Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final GameService gameService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session)  {
        log.info("Connected to web socket session: {}", session.getId());
        sessions.put(session);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    )
    {
        String payload = message.getPayload();
        GameMessageDto gameMessageDto = objectMapper.readValue(payload, GameMessageDto.class);

        gameService.handleRequest(gameMessageDto);
        for (WebSocketSession s : sessions) {
            if(s.isOpen() && !s.getId().equals(session.getId())) {
                virtualThreadExecutor.submit(() -> {
                    try {
                        s.sendMessage(new TextMessage(payload));
                    }catch (Exception e) {
                        log.error("Error sending message", e);
                    }
                });
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  {
        log.info("Disconnected from web socket session: {}", session.getId());
        sessions.remove(session);
    }
}
