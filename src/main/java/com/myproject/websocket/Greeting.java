package com.myproject.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor//역직렬화를 위해서
public class Greeting {

    private String content;

    public Greeting(String content) {
        this.content = content;
    }

}