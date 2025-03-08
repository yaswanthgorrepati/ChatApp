package com.lld.chat.privatechat.service;

import com.lld.chat.privatechat.model.ChatFriend;
import com.lld.chat.privatechat.repository.ChatFriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatFriendService {

    @Autowired
    private ChatFriendRepository chatFriendRepository;

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
}
