package com.myproject.websocket.dto;

import com.myproject.websocket.domain.ChatRoom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class ChatRoomDto {
    private final Long id;
    private final String name;

    public static ChatRoomDto from(ChatRoom chatRoom){
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

}
