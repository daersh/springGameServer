package com.zizonhyunwoo.springgameserver.webSocket.dto;

public record GameMessageDto (
        MessageType type,
        String roomId,
        String playerId,
        double x,
        double y,
        double z,
        String targetId
){
    public enum MessageType{
        ENTER, MOVE, ATTACK, CHAT, LEAVE
    }
}
