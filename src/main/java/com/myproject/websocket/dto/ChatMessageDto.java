package com.myproject.websocket.dto;


import com.myproject.websocket.domain.ChatMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ChatMessageDto {
    private final Long roomId;
    private final Long memberId;
    private final String sender;
    private final String context;
    private final LocalDateTime createdAt;

    public static ChatMessageDto from(ChatMessage chatMessage){
        return new ChatMessageDto(
                chatMessage.getChatRoom().getId(),
                chatMessage.getMember().getId(),
                chatMessage.getMember().getName(),
                chatMessage.getContext(),
                chatMessage.getCreatedAt());

    }
}
