package com.myproject.websocket;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("WebSocketEventListener");
        String username = accessor.getFirstNativeHeader("username");
        System.out.println(username);
        if (username != null) {
            accessor.getSessionAttributes().put("username", username);
            System.out.println(username + " 님이 연결되었습니다. (세션 ID: " + accessor.getSessionId() + ")");
        }
    }
}
