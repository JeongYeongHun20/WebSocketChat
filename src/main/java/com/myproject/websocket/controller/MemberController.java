package com.myproject.websocket.controller;

import com.myproject.websocket.domain.Member;
import com.myproject.websocket.dto.RegisterForm;
import com.myproject.websocket.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/member")
    @ResponseBody
    public String signUp(@RequestBody RegisterForm registerForm){
        return memberService.create(registerForm);
    }

}
