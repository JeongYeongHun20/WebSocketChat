package com.myproject.websocket.dto;

import com.myproject.websocket.domain.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
public class ChatRoomDto {
    private Long id;
    private String name;

    public ChatRoomDto(Long id, String name){
        this.id=id;
        this.name=name;

    }

    public static ChatRoomDto from(ChatRoom chatRoom){
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

}
