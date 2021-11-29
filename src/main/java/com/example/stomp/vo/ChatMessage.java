package com.example.stomp.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;



@RedisHash("chatMessage")
@Getter
@Setter
public class ChatMessage {

    @Id
    private String roomId;
    private String writer;
    private String message;
}
