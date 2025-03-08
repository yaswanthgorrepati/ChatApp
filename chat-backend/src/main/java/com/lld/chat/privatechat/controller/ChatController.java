package com.lld.chat.privatechat.controller;

import com.lld.chat.privatechat.model.ChatMessage;
import com.lld.chat.privatechat.model.ChatResponse;
import com.lld.chat.privatechat.service.ChatFriendService;
import com.lld.chat.privatechat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatFriendService chatFriendService;

    @MessageMapping("/private")
    public void  privateMessages(@Payload ChatMessage chatMessage){
        chatMessageService.saveMessage(chatMessage);

        ChatResponse chatResponse  =new ChatResponse(chatMessage.getFromUser(), chatMessage.getContent(), chatMessage.getTimestamp());
        System.out.println(chatMessage);
        // Update chat friends list for both sender and recipient
        chatFriendService.addChatFriend(chatMessage.getFromUser(), chatMessage.getToUser());
        chatFriendService.addChatFriend(chatMessage.getToUser(), chatMessage.getFromUser());

        simpMessagingTemplate.convertAndSendToUser(chatMessage.getToUser(), "/queue/messages", chatResponse);
        if (!chatMessage.getFromUser().equals(chatMessage.getToUser())) {
            simpMessagingTemplate.convertAndSendToUser(chatMessage.getFromUser(), "/queue/messages", chatResponse);
        }
    }

    // Endpoint to retrieve conversation between two users
    @GetMapping("/chat/conversation")
    public ResponseEntity<?> getConversation(@RequestParam String user1, @RequestParam String user2) {
        System.out.println("in the chat history");
        List<ChatMessage> history = chatMessageService.getConversation(user1, user2);
        System.out.println("Histort us " + history);
        return ResponseEntity.ok(history);
    }
}
