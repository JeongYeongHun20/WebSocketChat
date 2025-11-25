package com.myproject.websocket.repository;

import com.myproject.websocket.domain.Member;
import com.myproject.websocket.domain.MemberRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

    @Query("SELECT mr FROM MemberRoom mr " +
            "JOIN FETCH mr.chatRoom " +
            "WHERE mr.member.id = :memberId")
    List<MemberRoom> findByMember(@Param("memberId") Long member);

}
