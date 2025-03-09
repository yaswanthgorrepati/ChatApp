package com.lld.chat.privatechat.controller;

import com.lld.chat.privatechat.model.ChatMessage;
import com.lld.chat.privatechat.model.ChatResponse;
import com.lld.chat.privatechat.model.FriendUpdateDto;
import com.lld.chat.privatechat.repository.ChatMessageRepository;
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
import org.springframework.web.bind.annotation.PutMapping;
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

    @Autowired
    private ChatMessageRepository chatMessageRepository;

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

        // Send friend update to recipient: count unread messages from sender
        try {
            int unreadForRecipient = chatMessageRepository.countByFromUserAndToUserAndReadStatusFalse(chatMessage.getFromUser(), chatMessage.getToUser());
            FriendUpdateDto updateForRecipient = new FriendUpdateDto(chatMessage.getFromUser(), unreadForRecipient);
            simpMessagingTemplate.convertAndSendToUser(chatMessage.getToUser(), "/queue/friendUpdates", updateForRecipient);
        } catch (Exception ex) {
            // Log the exception and move on.
            System.err.println("Failed to send message, session may be closed -1 : " + ex.getMessage());
        }

        try{
            // Optionally, update sender's friend list too
            int unreadForSender = chatMessageRepository.countByFromUserAndToUserAndReadStatusFalse(chatMessage.getToUser(), chatMessage.getFromUser());
            FriendUpdateDto updateForSender = new FriendUpdateDto(chatMessage.getToUser(), unreadForSender);
            simpMessagingTemplate.convertAndSendToUser(chatMessage.getFromUser(), "/queue/friendUpdates", updateForSender);
        } catch (Exception ex) {
            // Log the exception and move on.
            System.err.println("Failed to send message, session may be closed -2: " + ex.getMessage());
        }

    }

    @GetMapping("/chat/conversation")
    public ResponseEntity<?> getConversation(@RequestParam String user1, @RequestParam String user2) {
        System.out.println("in the chat history");
        List<ChatMessage> history = chatMessageService.getConversation(user1, user2);
        System.out.println("Histort us " + history);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/chat/markRead")
    public ResponseEntity<String> markConversationAsRead(@RequestParam String user1, @RequestParam String user2) {
        List<ChatMessage> messages = chatMessageService.getConversation(user1, user2);
        messages.stream().filter(msg -> !msg.getReadStatus() && msg.getToUser().equals(user1))
                .forEach(msg -> msg.setReadStatus(true));
        chatMessageService.saveAll(messages);
        try {
            int unreadForRecipient = chatMessageRepository.countByFromUserAndToUserAndReadStatusFalse(user1, user2);
            FriendUpdateDto updateForRecipient = new FriendUpdateDto(user1, unreadForRecipient);
            simpMessagingTemplate.convertAndSendToUser(user2, "/queue/friendUpdates", updateForRecipient);
        } catch (Exception ex) {
            // Log the exception and move on.
            System.err.println("Failed to send message, session may be closed -1 : " + ex.getMessage());
        }

        try{
            // Optionally, update sender's friend list too
            int unreadForSender = chatMessageRepository.countByFromUserAndToUserAndReadStatusFalse(user2, user1);
            FriendUpdateDto updateForSender = new FriendUpdateDto(user2, unreadForSender);
            simpMessagingTemplate.convertAndSendToUser(user1, "/queue/friendUpdates", updateForSender);
        } catch (Exception ex) {
            // Log the exception and move on.
            System.err.println("Failed to send message, session may be closed -2: " + ex.getMessage());
        }
        return ResponseEntity.ok("Messages marked as read");
    }
}
