package com.example.stomp.repository;

import com.example.stomp.service.RedisSubscriber;
import com.example.stomp.vo.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoom> chatRoomMap;
    // 채팅방(topic)에 발행되는 메세지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    private final RedisTemplate<String,Object> redisTemplate;
    private HashOperations<String,String,ChatRoom> opsHashChatRoom;
    private ListOperations<String, Object> opsListChatRoom;
    //채팅 방의 대화 메시지 발행하기 위한 redis topic 저보.
    private Map<String, ChannelTopic> topics;

    @PostConstruct // WAS가 올라가면서 bean이 생성 될 때 딱 한번 초기화
    private void init() {
        //chatRoomMap = new LinkedHashMap<>();
        opsHashChatRoom = redisTemplate.opsForHash();
        opsListChatRoom = redisTemplate.opsForList();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRooms() {
        /*List<ChatRoom> chatRoomList = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRoomList); //채팅방 생성 순서 최근 순으로 반환
        return chatRoomList;*/
        return opsHashChatRoom.values("CHAT_ROOM");
    }

    public ChatRoom findByRoomId(String roomId) {
        //return chatRoomMap.get(roomId);
        return opsHashChatRoom.get("CHAT_ROOM", roomId);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(name);
        //chatRoomMap.put(chatRoom.getRoomId(),chatRoom);
        //서버 간 채팅방 공유를 위해 redis hash에 저장
        opsHashChatRoom.put("CHAT_ROOM", chatRoom.getRoomId(),chatRoom);
        opsListChatRoom.leftPush("chatRoom", chatRoom);
        return chatRoom;
    }
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic((roomId));
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
