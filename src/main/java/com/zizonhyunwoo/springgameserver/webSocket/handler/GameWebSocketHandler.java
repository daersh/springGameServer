package com.zizonhyunwoo.springgameserver.webSocket.handler;

import com.zizonhyunwoo.springgameserver.webSocket.dto.GameMessageDto;
import com.zizonhyunwoo.springgameserver.webSocket.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

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
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final GameService gameService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session)  {
        log.info("Connected to web socket session: {}", session.getId());
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 5000, 65536);
        session.getAttributes().put("safeSession", safeSession);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    )
    {

        virtualThreadExecutor.submit(() -> {
            try {
                // 가공
                String payload = message.getPayload();
                log.info("Received text message: {}", payload);
                GameMessageDto gameMessageDto = objectMapper.readValue(message.getPayload(), GameMessageDto.class);
                String roomId = gameMessageDto.roomId();
                // 세션 정보 확인
                if (gameMessageDto.type() == GameMessageDto.MessageType.ENTER) {
                    WebSocketSession safeSession = (WebSocketSession) session.getAttributes().get("safeSession");
                    roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(safeSession);
                    session.getAttributes().put("roomId", roomId);
                    session.getAttributes().put("playerId", gameMessageDto.playerId());
                }
                // 비즈니스 로직 수행
                gameService.handleRequest(gameMessageDto);
                // 전파
                Set<WebSocketSession> room = roomSessions.get(roomId);
                if (room == null) return;
                for (WebSocketSession s : room)
                    if (s.isOpen() && !s.getId().equals(session.getId())) {
                        System.out.println("Sending text message");
                        s.sendMessage(new TextMessage(payload));
                    }
            }catch (InvalidFormatException e){
                log.warn("Invalid message received: {}", e.getMessage());
            }
            catch (Exception e) {
                log.error("Error sending message", e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  {
        try {
            log.info("Disconnected from web socket session: {}", session.getId());
            String roomId = (String) session.getAttributes().get("roomId");
            String playerId = (String) session.getAttributes().get("playerId");
            WebSocketSession safeSession = (WebSocketSession) session.getAttributes().get("safeSession");

            if (roomId != null && roomSessions.containsKey(roomId)) {
                if (safeSession != null) roomSessions.get(roomId).remove(safeSession);
                if (roomSessions.get(roomId).isEmpty()) roomSessions.remove(roomId);
                else if (playerId != null) {
                    GameMessageDto leaveMessage = new GameMessageDto(
                            GameMessageDto.MessageType.LEAVE,
                            roomId,
                            playerId,
                            0.0, 0.0, 0.0,
                            ""
                    );

                    String leavePayload = objectMapper.writeValueAsString(leaveMessage);
                    TextMessage textMessage = new TextMessage(leavePayload);
                    Set<WebSocketSession> room = roomSessions.get(roomId);
                    if (room != null) {
                        for (WebSocketSession s : room) {
                            if (s.isOpen()) {
                                s.sendMessage(textMessage);
                            }
                        }
                    }
                    log.info("🎉 {} 님의 퇴장(LEAVE) 패킷 전송 완료!", playerId);
                }
            }
        }catch (Exception e) {
            log.error("Error disconnecting from web socket session", e);
        }
    }
}
