package com.myproject.websocket.service;

import com.myproject.websocket.domain.ChatMessage;
import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import com.myproject.websocket.domain.MemberRoom;
import com.myproject.websocket.dto.ChatMessageDto;
import com.myproject.websocket.dto.ChatRoomDto;
import com.myproject.websocket.dto.MemberRoomDto;
import com.myproject.websocket.repository.ChatMessageRepository;
import com.myproject.websocket.repository.ChatRoomRepository;
import com.myproject.websocket.repository.MemberRepository;
import com.myproject.websocket.repository.MemberRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MemberRoomRepository memberRoomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;


    public List<ChatRoomDto> findRooms(Member loginUser){
        return memberRoomRepository.findByMember(loginUser.getId())
                .stream()
                .map(mr->ChatRoomDto.from(mr.getChatRoom()))
                .toList();
    }
    public ChatRoomDto createRoom(Member loginUser, String roomName){
        ChatRoom chatRoom = ChatRoom.create(loginUser,roomName);
        ChatRoom save = chatRoomRepository.save(chatRoom);

        MemberRoom memberRoom = MemberRoom.create(loginUser, chatRoom);
        memberRoomRepository.save(memberRoom);

        return ChatRoomDto.from(save);
    }

    public List<ChatMessageDto> findMessages(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 채팅방 입니다."));

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoom(chatRoom);
        List<ChatMessageDto> chatMessageDtos= new java.util.ArrayList<>(chatMessages
                .stream()
                .map(ChatMessageDto::from)
                .toList());

        chatMessageDtos.sort(Comparator.comparing(ChatMessageDto::getCreatedAt));
        return chatMessageDtos;
    }

    public ChatMessage createMessage(Long roomId, Member member, String context){
        log.info("createMessage");
        Member loginUser = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 채팅방 입니다."));
        ChatMessage chatMessage = ChatMessage.create(chatRoom, loginUser, context);
        return chatMessageRepository.save(chatMessage);
    }

    public ChatRoomDto joinRoom(Long roomId, Member member){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 입니다."));
        Member loginUser = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));

        memberRoomRepository.save(MemberRoom.create(loginUser, chatRoom));

        return ChatRoomDto.from(chatRoom);

    }




}
