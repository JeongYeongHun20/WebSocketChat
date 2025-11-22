package com.myproject.websocket;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 1. 요청이 서블릿(HTTP) 요청인지 확인
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            // 2. HTTP 세션을 가져옵니다.
            HttpSession session = servletRequest.getServletRequest().getSession(false);

            if (session != null) {
                // 3. 세션에 저장된 "LOGIN_USER" (Member 객체)를 꺼냅니다.
                Object loginMember = session.getAttribute("LOGIN_USER");

                if (loginMember != null) {
                    // ★★★ 가장 중요한 부분 ★★★
                    // HTTP 세션에 있는 값을 -> WebSocket 세션의 'attributes' 창고로 복사합니다.
                    // 이제 웹소켓 안에서도 "LOGIN_USER"라는 이름으로 Member 객체를 꺼낼 수 있습니다.
                    attributes.put("LOGIN_USER", loginMember);

                    System.out.println("HandshakeInterceptor: HTTP 세션에서 웹소켓으로 사용자 정보 복사 완료");
                }
            }
        }
        return true; // true를 리턴해야 핸드셰이크가 계속 진행됩니다.
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 이후에 할 일 (보통 비워둠)
    }
}
