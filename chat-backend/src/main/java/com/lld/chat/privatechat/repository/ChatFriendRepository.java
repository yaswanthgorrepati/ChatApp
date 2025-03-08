package com.lld.chat.privatechat.repository;

import com.lld.chat.privatechat.model.ChatFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatFriendRepository extends JpaRepository<ChatFriend, Long> {
    List<ChatFriend> findByChatUser(String chatUser);
    boolean existsByChatUserAndFriend(String chatUser, String friend);
}
