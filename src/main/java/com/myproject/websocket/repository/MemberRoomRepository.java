package com.myproject.websocket.repository;

import com.myproject.websocket.domain.ChatRoom;
import com.myproject.websocket.domain.Member;
import com.myproject.websocket.domain.MemberRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

    @Query("SELECT mr FROM MemberRoom mr " +
            "JOIN FETCH mr.chatRoom " +
            "WHERE mr.member.id = :memberId")
    List<MemberRoom> findByMember(@Param("memberId") Long member);

    Optional<MemberRoom> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);

    @Modifying // delete나 update 쿼리에는 필수!
    @Query("DELETE FROM MemberRoom mr WHERE mr.member.id = :memberId AND mr.chatRoom.id = :roomId")
    void deleteRoom(@Param("memberId") Long memberId,@Param("roomId") Long roomId);

}
