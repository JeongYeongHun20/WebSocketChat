package com.myproject.websocket.domain;

import jakarta.persistence.*;

@Table(name = "member_room")
@Entity
public class MemberRoom {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private ChatRoom roomId;



}
