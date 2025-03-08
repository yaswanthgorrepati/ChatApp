package com.lld.chat.privatechat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_friends")
public class ChatFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatUser;    // owner of the friend list
    private String friend;  // friend the user has chatted with

    public ChatFriend() {}

    public ChatFriend(String chatUser, String friend) {
        this.chatUser = chatUser;
        this.friend = friend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
