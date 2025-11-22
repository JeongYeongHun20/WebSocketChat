package com.myproject.websocket.service;

import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import com.myproject.websocket.domain.MemberRoom;
import com.myproject.websocket.dto.ChatRoomDto;
import com.myproject.websocket.repository.ChatRoomRepository;
import com.myproject.websocket.repository.MemberRoomRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MemberRoomRepository memberRoomRepository;
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomDto> findRooms(Member loginUser){
        return chatRoomRepository.findByMember(loginUser)
                .stream()
                .map(ChatRoomDto::from)
                .toList();
    }
    public ChatRoomDto createRoom(Member loginUser, String roomName){
        ChatRoom chatRoom = ChatRoom.create(roomName);
        ChatRoom save = chatRoomRepository.save(chatRoom);
        MemberRoom memberRoom = MemberRoom.create(loginUser, chatRoom);
        memberRoomRepository.save(memberRoom);

        return ChatRoomDto.from(save);
    }




}
