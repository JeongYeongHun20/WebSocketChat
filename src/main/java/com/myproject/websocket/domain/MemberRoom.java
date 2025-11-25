package com.myproject.websocket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member_room")
@Entity
@Getter
@NoArgsConstructor
public class MemberRoom {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private ChatRoom chatRoom;

    private MemberRoom(Member member, ChatRoom chatRoom){
        this.member =member;
        this.chatRoom =chatRoom;
    }

    public static MemberRoom create(Member member, ChatRoom chatRoom){
        return new MemberRoom(member, chatRoom);
    }








}
