package com.example.stomp.repository;

import com.example.stomp.vo.ChatRoom;
import org.springframework.data.repository.CrudRepository;

public interface RoomRepository  extends CrudRepository<ChatRoom, String> {

}
