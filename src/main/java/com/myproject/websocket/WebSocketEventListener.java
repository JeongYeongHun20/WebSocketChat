package com.myproject.websocket;


import com.myproject.websocket.domain.Member;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Object loginUserObj = accessor.getSessionAttributes().get("LOGIN_USER");

        if (loginUserObj != null) {
            // Member 객체로 캐스팅해서 사용 (Member 클래스 import 필요)
            Member member = (Member) loginUserObj;
            System.out.println("[입장] " + member.getName() + " 님이 연결되었습니다.");
        } else {
            System.out.println("[경고] 로그인하지 않은 사용자가 연결되었습니다.");
        }
    }
}
