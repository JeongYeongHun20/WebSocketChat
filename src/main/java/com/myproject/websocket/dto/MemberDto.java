package com.myproject.websocket.dto;


import com.myproject.websocket.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberDto {
    private final Long id;
    private final String email;
    private final String name;

    public static MemberDto from(Member member){
        return new MemberDto(member.getId(), member.getEmail(), member.getName());
    }
}
