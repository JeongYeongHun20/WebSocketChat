package com.myproject.websocket;


//json 직렬화에 사용되는 클래스
public class HelloMessage {

    private String message;

    public HelloMessage() {
    }

    public HelloMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}