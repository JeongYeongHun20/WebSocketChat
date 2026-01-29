package com.myproject.websocket.service;

import com.myproject.websocket.domain.ChatMessage;
import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import com.myproject.websocket.domain.MemberRoom;
import com.myproject.websocket.dto.ChatMessageDto;
import com.myproject.websocket.dto.ChatRoomDto;
import com.myproject.websocket.dto.MemberDto;
import com.myproject.websocket.repository.ChatMessageRepository;
import com.myproject.websocket.repository.ChatRoomRepository;
import com.myproject.websocket.repository.MemberRepository;
import com.myproject.websocket.repository.MemberRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MemberRoomRepository memberRoomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;


    public List<ChatRoomDto> findRooms(MemberDto loginUser){
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

    public ChatMessage createMessage(Long roomId, MemberDto member, String context){
        log.info("createMessage");
        Member loginUser = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 채팅방 입니다."));
        ChatMessage chatMessage = ChatMessage.create(chatRoom, loginUser, context);
        return chatMessageRepository.save(chatMessage);
    }

    public ChatRoomDto joinRoom(Long roomId, MemberDto member){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 입니다."));
        Member loginUser = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));

        //존재하는 채팅방 중복 생성 방지
        //차후에 예외처리 대신 존재하는 채팅방으로 연결
        if (memberRoomRepository.findMemberRoom(member.getId(), chatRoom.getId()).isPresent()){
            throw new IllegalStateException("이미 존재하는 채팅방 입니다");
        }

        memberRoomRepository.save(MemberRoom.create(loginUser, chatRoom));

        return ChatRoomDto.from(chatRoom);

    }

    //delete나 update시에는 @Tsransactional 필수
    @Transactional
    public void deleteRoom(Long memberId, Long roomId){
        memberRoomRepository.deleteRoom(memberId, roomId);
        if (memberRoomRepository.findByChatRoom(roomId).isEmpty()){
            chatRoomRepository.deleteById(roomId);
        }



    }




}
