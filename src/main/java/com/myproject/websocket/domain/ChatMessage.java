package com.myproject.websocket.domain;

import com.myproject.websocket.dto.ChatMessageDto;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table
@Entity
@Getter
public class ChatMessage {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private ChatRoom chatRoom;

    private String context;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public static ChatMessage create(ChatRoom chatRoom, Member member, String context){
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.member=member;
        chatMessage.chatRoom=chatRoom;
        chatMessage.context=context;

        return chatMessage;
    }


}
