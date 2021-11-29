package com.example.stomp.repository;

import com.example.stomp.vo.ChatMessage;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<ChatMessage,String> {
}
