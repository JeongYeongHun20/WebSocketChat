package com.myproject.websocket.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Table(name = "member_room")
@Entity
@NoArgsConstructor
public class MemberRoom {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private ChatRoom roomId;

    private MemberRoom(Member member, ChatRoom chatRoom){
        this.memberId=member;
        this.roomId=chatRoom;
    }

    public static MemberRoom create(Member member, ChatRoom chatRoom){
        return new MemberRoom(member, chatRoom);
    }








}
