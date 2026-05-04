package com.zizonhyunwoo.springgameserver.webSocket.dto;

public class GameMessage{

    public record Move(
        IGameMessage.MessageType messageType,
        String roomId,
        String playerId,
        String targetId
    ) implements IGameMessage { }

    public record Attack(
            IGameMessage.MessageType messageType,
            String roomId,
            String playerId
    ) implements IGameMessage{ }

    public record Chat(
            IGameMessage.MessageType messageType,
            String roomId,
            String playerId
    ) implements IGameMessage{ }

    public record Enter(
            IGameMessage.MessageType messageType,
            String roomId,
            String playerId
    ) implements IGameMessage{ }

    public record Leave(
            IGameMessage.MessageType messageType,
            String roomId,
            String playerId
    ) implements IGameMessage{ }
}
