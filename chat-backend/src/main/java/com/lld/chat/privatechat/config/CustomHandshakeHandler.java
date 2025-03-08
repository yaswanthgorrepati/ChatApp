package com.lld.chat.privatechat.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
  @Override
  protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
    String username = (String) attributes.get("username");
    if (username == null || username.trim().isEmpty()) {
      username = "anonymous"; // Fallback
    }
    return new StompPrincipal(username);
  }
}
