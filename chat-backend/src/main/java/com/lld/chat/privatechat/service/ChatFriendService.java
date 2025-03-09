package com.lld.chat.privatechat.service;

import com.lld.chat.privatechat.model.ChatFriend;
import com.lld.chat.privatechat.model.ChatFriendDto;
import com.lld.chat.privatechat.repository.ChatFriendRepository;
import com.lld.chat.privatechat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatFriendService {

    @Autowired
    private ChatFriendRepository chatFriendRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatFriend addChatFriend(String chatUser, String friend) {
        if (!chatFriendRepository.existsByChatUserAndFriend(chatUser, friend)) {
            ChatFriend chatFriend = new ChatFriend(chatUser, friend);
            return chatFriendRepository.save(chatFriend);
        }
        return null;
    }

    public List<ChatFriend> getChatFriends(String chatUser) {
        return chatFriendRepository.findByChatUser(chatUser);
    }

    public List<ChatFriendDto> getChatFriendsWithUnreadCount(String chatUser) {
        List<ChatFriend> friends = chatFriendRepository.findByChatUser(chatUser);
        List<ChatFriendDto> dtos = new ArrayList<>();
        for (ChatFriend cf : friends) {
            int unread = chatMessageRepository.countByFromUserAndToUserAndReadStatusFalse(cf.getFriend(), chatUser);
            dtos.add(new ChatFriendDto(cf.getFriend(), unread));
        }
        return dtos;
    }
}
