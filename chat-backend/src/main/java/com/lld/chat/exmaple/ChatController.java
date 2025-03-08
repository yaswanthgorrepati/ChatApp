//package com.lld.chat.exmaple;
//
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class ChatController {
//
//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
//    public Greeting processMessage(ChatMessage message) throws Exception {
//        System.out.println("message received");
//        return new Greeting("[" + message.getFrom() + "]: " + message.getContent());
//    }
//}
