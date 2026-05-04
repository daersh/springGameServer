package com.zizonhyunwoo.springgameserver.webSocket.handler;

import com.zizonhyunwoo.springgameserver.webSocket.dto.GameMessage;
import com.zizonhyunwoo.springgameserver.webSocket.dto.IGameMessage;
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

import java.io.IOException;
import java.util.HashSet;
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
                IGameMessage gameMessage = objectMapper.readValue(message.getPayload(), IGameMessage.class);
                String roomId = gameMessage.roomId();

                switch (gameMessage){
                    case GameMessage.Enter enter-> {
                        WebSocketSession curSession = (WebSocketSession) session.getAttributes().get("safeSession");
                        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(curSession);
                        session.getAttributes().put("roomId", roomId);
                        session.getAttributes().put("playerId", enter.playerId());
                    }
                    case GameMessage.Leave leave-> {

                    }
                    case GameMessage.Attack attack->{

                    }
                    case GameMessage.Chat chat->{

                    }
                    case GameMessage.Move move-> {

                    }
                    default -> {
                        throw new IllegalArgumentException("Unrecognized message type: " + gameMessage);
                    }
                }
                propagate(session, roomId, payload);
                // 비즈니스 로직 수행
                gameService.handleRequest(gameMessage);

            }catch (InvalidFormatException e){
                log.warn("Invalid message received: {}", e.getMessage());
            }
            catch (Exception e) {
                log.error("Error sending message", e);
            }
        });
    }

    private void propagate(WebSocketSession session, String roomId, String payload) throws IOException {
        // 전파
        Set<WebSocketSession> room = roomSessions.get(roomId);
        if (room == null) return;
        for (WebSocketSession s : room)
            if (s.isOpen()
//                    && !s.getId().equals(session.getId())
            ) {
                System.out.println("Sending text message");
                s.sendMessage(new TextMessage(payload));
            }
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
                    GameMessage.Leave leaveMessage = new GameMessage.Leave(
                            IGameMessage.MessageType.LEAVE,
                            roomId,
                            playerId
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
