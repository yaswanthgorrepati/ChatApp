package com.lld.chat.privatechat.service;

import com.lld.chat.privatechat.model.ChatMessage;
import com.lld.chat.privatechat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getConversation(String user1, String user2) {
        return chatMessageRepository.findByFromUserAndToUserOrFromUserAndToUser(user1, user2, user2, user1);
    }
}
