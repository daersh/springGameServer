package com.zizonhyunwoo.springgameserver.webSocket.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "messageType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameMessage.Move.class, name = "MOVE"),
        @JsonSubTypes.Type(value = GameMessage.Attack.class, name = "ATTACK"),
        @JsonSubTypes.Type(value = GameMessage.Chat.class, name = "CHAT"),
        @JsonSubTypes.Type(value = GameMessage.Enter.class, name = "ENTER"),
        @JsonSubTypes.Type(value = GameMessage.Leave.class, name = "LEAVE"),
})
public interface IGameMessage {
    MessageType messageType();
    String roomId();
    String playerId();
    enum MessageType{
        ENTER, MOVE, ATTACK, CHAT, LEAVE
    }
}
