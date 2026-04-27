package com.zizonhyunwoo.springgameserver.webSocket.service;

import com.zizonhyunwoo.springgameserver.webSocket.dto.GameMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameService {

    public void handleRequest(
        GameMessageDto req
    ) {
        switch (req.type()){
            case ATTACK -> handleAttack(req);
            case MOVE -> handleMove(req);
            case ENTER ->  handleEnter(req);
            case CHAT -> handleChat(req);
            case LEAVE -> handleLeave(req);
            default -> throw new IllegalArgumentException("Invalid request");
        }
    }

    private void handleLeave(GameMessageDto req) {
        log.info("Leaving game: {}", req);
    }

    private void handleChat(GameMessageDto req) {
        log.info("Chat game: {}",req);

    }

    private void handleEnter(GameMessageDto req){
        log.info("Enter game: {}",req);

    }

    private void handleMove(GameMessageDto req){
        log.info("Move game: {}",req);

    }

    private void handleAttack(GameMessageDto req){
        log.info("Attack {}",req);

    }
}