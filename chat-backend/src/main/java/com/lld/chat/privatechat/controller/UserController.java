package com.lld.chat.privatechat.controller;

import com.lld.chat.privatechat.model.User;
import com.lld.chat.privatechat.repository.UserRepository;
import com.lld.chat.privatechat.service.ChatFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatFriendService chatFriendService;

    // Search for users by username (case-insensitive, partial match)
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam("q") String query) {
        System.out.println("searching for tghe user");
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // Get chat-friends list for a given user
    @GetMapping("/friends")
    public List<String> getChatFriends(@RequestParam("username") String username) {
        return chatFriendService.getChatFriends(username)
                .stream()
                .map(friend -> friend.getFriend())
                .collect(Collectors.toList());
    }
}
