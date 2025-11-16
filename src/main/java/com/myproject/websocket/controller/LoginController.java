package com.myproject.websocket.controller;

import com.myproject.websocket.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberRepository memberRepository;

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam("email")String email, @RequestParam("pwd")String pwd, HttpServletRequest request) {
        String memberPassword=memberRepository.findByEmail(email).getPwd();
        if (memberPassword != null && memberPassword.equals(pwd)) {
            HttpSession session = request.getSession();
            session.setAttribute("LOGIN_USER", email);
            return "로그인 성공";
        }else{
            return "로그인 실패";
        }
    }
}
