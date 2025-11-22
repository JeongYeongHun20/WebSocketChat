package com.myproject.websocket.repository;

import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    List<ChatRoom> findByMember(Member member);

}
