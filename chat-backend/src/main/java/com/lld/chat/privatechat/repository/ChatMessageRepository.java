package com.lld.chat.privatechat.repository;

import com.lld.chat.privatechat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByFromUserAndToUserOrFromUserAndToUser(String fromUser, String toUser, String reverseFrom, String reverseTo);
    int countByFromUserAndToUserAndReadStatusFalse(String fromUser, String toUser);

}
