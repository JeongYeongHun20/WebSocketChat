package com.myproject.websocket.controller;

import com.myproject.websocket.domain.Member;
import com.myproject.websocket.repository.MemberRepository;
import com.myproject.websocket.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestParam("email")String email, @RequestParam("pwd")String pwd, HttpServletRequest request) {

        try {
            Member member = loginService.findMember(email, pwd);
            HttpSession session = request.getSession();
            session.setAttribute("LOGIN_USER", member);
            log.info("로그인 성공: {}", member.getName());

            return ResponseEntity.ok("로그인 성공");
        }catch (IllegalArgumentException e){
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
