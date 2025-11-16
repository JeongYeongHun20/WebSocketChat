package com.myproject.websocket.domain;


import com.myproject.websocket.dto.RegisterForm;
import jakarta.persistence.*;
import lombok.Getter;

@Table
@Entity
@Getter
public class Member {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pwd;
    private String name;
    private String email;

    public static Member create(RegisterForm registerForm) {

        Member member=new Member();
        member.email=registerForm.getEmail();
        member.pwd=registerForm.getPwd();
        member.name=registerForm.getName();

        return member;
    }
}
