package com.myproject.websocket.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterForm {
    String email;
    String pwd;
    String name;
}
