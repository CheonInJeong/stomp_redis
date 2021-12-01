package com.example.stomp.controller;

import com.example.stomp.repository.ChatRoomRepository;
import com.example.stomp.service.RedisPublisher;
import com.example.stomp.vo.ChatMessage;
import com.example.stomp.vo.ChatRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class StompChatClient {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private HashOperations<String,String, List<ChatMessage>> opsHashChatMessage;
    private final RedisTemplate<String,Object> redisTemplate;
    private  List<ChatMessage> chatMessageList;

    @PostConstruct
    public void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        chatMessageList = new ArrayList<>();
    }
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessage chatMessage) {
        System.out.println("연결성공");
        chatMessage.setMessage(chatMessage.getWriter() + "님이 채팅방에 참여하셨습니다.");
        chatRoomRepository.enterChatRoom(chatMessage.getRoomId());

        List<ChatMessage> privateChatList = opsHashChatMessage.get("chatMessage:"+chatMessage.getRoomId(), chatMessage.getRoomId());

        if (privateChatList == null) {
            privateChatList = new ArrayList<>();
        }
        privateChatList.add(chatMessage);

        opsHashChatMessage.put("chatMessage:" + chatMessage.getRoomId(), chatMessage.getRoomId(), privateChatList);
        //메세지 보내기
        redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessage chatMessage) throws IOException {

      List<ChatMessage> privateChatList = opsHashChatMessage.get("chatMessage:"+chatMessage.getRoomId(), chatMessage.getRoomId());

      if (privateChatList == null) {
          privateChatList = new ArrayList<>();
      }
      privateChatList.add(chatMessage);
      opsHashChatMessage.put("chatMessage:"+chatMessage.getRoomId(), chatMessage.getRoomId(), privateChatList);
      //messagingTemplate.convertAndSend("/sub/chat/room/"+chatMessage.getRoomId(),chatMessage);
      redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()),chatMessage);
    }
}
