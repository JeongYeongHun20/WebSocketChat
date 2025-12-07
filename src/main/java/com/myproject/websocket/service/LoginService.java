package com.myproject.websocket.service;

import com.myproject.websocket.domain.Member;
import com.myproject.websocket.dto.MemberDto;
import com.myproject.websocket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public MemberDto findMember(String email, String pwd){
        Member member = memberRepository.findByEmail(email);
        if (member == null) {

            throw new IllegalStateException("존재하지 않는 멤버");
        }
        if (!member.getPwd().equals(pwd)){
            throw new IllegalStateException("비밀번호가 틀립니다.");
        }
        return MemberDto.from(member);

    }
}
