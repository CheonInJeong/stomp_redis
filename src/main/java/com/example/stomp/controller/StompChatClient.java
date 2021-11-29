package com.example.stomp.controller;

import com.example.stomp.repository.ChatRoomRepository;
import com.example.stomp.repository.MessageRepository;
import com.example.stomp.repository.RoomRepository;
import com.example.stomp.service.RedisPublisher;
import com.example.stomp.vo.ChatMessage;
import com.sun.org.apache.xml.internal.res.XMLErrorResources_es;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompChatClient {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessage chatMessage) {
        System.out.println("연결성공");
        chatMessage.setMessage(chatMessage.getWriter() + "님이 채팅방에 참여하셨습니다.");
        chatRoomRepository.enterChatRoom(chatMessage.getRoomId());
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

        redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessage chatMessage) {
        //messageRepository.save(chatMessage);
        messagingTemplate.convertAndSend("/sub/chat/room/"+chatMessage.getRoomId(),chatMessage);
    }
}
