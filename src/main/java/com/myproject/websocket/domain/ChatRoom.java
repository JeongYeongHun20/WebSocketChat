package com.myproject.websocket.domain;

import com.myproject.websocket.dto.ChatRoomDto;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table
@Entity
@Getter
public class ChatRoom {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;


    @CreationTimestamp
    private LocalDateTime createdAt;


    public static ChatRoom create(String roomName){
        ChatRoom chatRoom=new ChatRoom();
        chatRoom.name=roomName;
        return chatRoom;



    }

}
