package com.myproject.websocket.controller;


import com.myproject.websocket.domain.ChatMessage;
import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import com.myproject.websocket.dto.ChatMessageDto;
import com.myproject.websocket.dto.ChatRoomDto;
import com.myproject.websocket.dto.MemberRoomDto;
import com.myproject.websocket.repository.ChatMessageRepository;
import com.myproject.websocket.repository.ChatRoomRepository;
import com.myproject.websocket.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.IllformedLocaleException;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @GetMapping("/rooms")
    public List<ChatRoomDto> loadRooms(HttpServletRequest request){
        //session이 없을시 true: 새로 생서한 session반환, false: null값을 반환
        HttpSession session=request.getSession(false);
        loginCheck(session);
        Member loginUser = (Member) session.getAttribute("LOGIN_USER");

        return chatService.findRooms(loginUser);
    }


    @PostMapping("/room")
    public ChatRoomDto createRoom(@RequestParam String roomName, HttpServletRequest request){
        HttpSession session= request.getSession(false);
        loginCheck(session);
        Member loginUser = (Member) session.getAttribute("LOGIN_USER");
        return chatService.createRoom(loginUser, roomName);
    }

    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessageDto> loadMessages(@PathVariable(name = "roomId") Long roomId){
        log.info("EnterLoadMessages");
        return chatService.findMessages(roomId);
    }

    //@DestinationVariable은 STOMP에서 사용하는 @PathVariable과 같은것
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, String context, SimpMessageHeaderAccessor accessor){
        Member member = (Member) accessor.getSessionAttributes().get("LOGIN_USER");
        ChatMessageDto messageDto=ChatMessageDto.from(chatService.createMessage(roomId, member, context));
        simpMessagingTemplate.convertAndSend("/topic/room/"+roomId,messageDto);
    }

    private void loginCheck(HttpSession session) {
        if (session == null || session.getAttribute("LOGIN_USER") == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
    }



}
