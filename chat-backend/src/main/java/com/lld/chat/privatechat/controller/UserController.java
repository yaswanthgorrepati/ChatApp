package com.lld.chat.privatechat.controller;

import com.lld.chat.privatechat.model.ChatFriend;
import com.lld.chat.privatechat.model.ChatFriendDto;
import com.lld.chat.privatechat.model.User;
import com.lld.chat.privatechat.repository.ChatFriendRepository;
import com.lld.chat.privatechat.repository.ChatMessageRepository;
import com.lld.chat.privatechat.repository.UserRepository;
import com.lld.chat.privatechat.service.ChatFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatFriendService chatFriendService;

    @Autowired
    private ChatFriendRepository chatFriendRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam("q") String query) {
        System.out.println("searching for tghe user");
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getChatFriends(@RequestParam("username") String username) {
        System.out.println("messages in feinrd unread");
        List<ChatFriend> friends = chatFriendRepository.findByChatUser(username);
        List<ChatFriendDto> dtos = new ArrayList<>();
        for (ChatFriend cf : friends) {
            int unread = chatMessageRepository.countByFromUserAndToUserAndReadStatusFalse(cf.getFriend(), username);
            dtos.add(new ChatFriendDto(cf.getFriend(), unread));
        }
        return ResponseEntity.ok(dtos);
    }
}
