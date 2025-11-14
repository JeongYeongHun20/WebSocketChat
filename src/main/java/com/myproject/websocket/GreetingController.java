package com.myproject.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(SimpMessageHeaderAccessor headerAccessor, HelloMessage message) throws Exception {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        return new Greeting(username+": " + HtmlUtils.htmlEscape(message.getMessage()));
    }

    @MessageMapping("/send")
    @SendTo("/topic/greetings")
    public Greeting sending(SimpMessageHeaderAccessor headerAccessor)throws Exception{
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        return new Greeting(username+"이 입장하셨습니다" );

    }


}