package com.zizonhyunwoo.springgameserver.webSocket.service;

import com.zizonhyunwoo.springgameserver.webSocket.dto.GameMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameService {
    public void handleEnter(GameMessageDto req){
        log.info("Enter game: {}",req);

    }
    public void handleMove(GameMessageDto req){
        log.info("Move game: {}",req);
    }
    public void handleAttack(GameMessageDto req){
        log.info("Attack {}",req);
    }
}
