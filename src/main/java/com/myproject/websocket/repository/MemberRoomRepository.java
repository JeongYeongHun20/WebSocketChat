package com.myproject.websocket.repository;

import com.myproject.websocket.domain.MemberRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

}
