package com.zizonhyunwoo.springgameserver.webSocket.service;

import com.zizonhyunwoo.springgameserver.webSocket.dto.GameMessage;
import com.zizonhyunwoo.springgameserver.webSocket.dto.IGameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameService {

    public void handleRequest(
        IGameMessage req
    ) {
        switch (req.messageType()){
            case ATTACK -> handleAttack(req);
            case MOVE -> handleMove(req);
            case ENTER ->  handleEnter(req);
            case CHAT -> handleChat(req);
            case LEAVE -> handleLeave(req);
            default -> throw new IllegalArgumentException("Invalid request");
        }
    }

    private void handleLeave(IGameMessage req) {
        log.info("Leaving game: {}", req);
    }

    private void handleChat(IGameMessage req) {
        log.info("Chat game: {}",req);

    }

    private void handleEnter(IGameMessage req){
        log.info("Enter game: {}",req);

    }

    private void handleMove(IGameMessage req){
        log.info("Move game: {}",req);

    }

    private void handleAttack(IGameMessage req){
        log.info("Attack {}",req);

    }
}