package com.myproject.websocket.service;

import com.myproject.websocket.domain.Member;
import com.myproject.websocket.dto.RegisterForm;
import com.myproject.websocket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public String create(RegisterForm registerForm){
        Member existingEmailMember = memberRepository.findByEmail(registerForm.getEmail());
        if (existingEmailMember != null) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }
        memberRepository.save(Member.create(registerForm));
        return registerForm.getEmail();
    }


}
