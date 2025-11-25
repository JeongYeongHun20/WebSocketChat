package com.myproject.websocket.dto;


import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import com.myproject.websocket.domain.MemberRoom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberRoomDto {
    private final Long memberId;
    private final Long roomId;

    public static MemberRoomDto from(Member member, ChatRoom chatRoom){return new MemberRoomDto(member.getId(), chatRoom.getId());}
}
