package com.myproject.websocket.repository;

import com.myproject.websocket.domain.ChatMessage;
import com.myproject.websocket.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
}
