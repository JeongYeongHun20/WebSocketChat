package com.myproject.websocket;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//json 직렬화에 사용되는 클래스
@Setter
@Getter
@NoArgsConstructor//역직렬화를 위해서
public class HelloMessage {

    private String message;

    public HelloMessage(String message) {
        this.message = message;
    }

}