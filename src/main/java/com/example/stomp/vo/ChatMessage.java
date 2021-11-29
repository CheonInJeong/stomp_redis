package com.example.stomp.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@RedisHash("chatMessage")
@Getter
@Setter
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 2012201320152021L;
    @Id
    private String roomId;
    private String writer;
    private String message;
}
