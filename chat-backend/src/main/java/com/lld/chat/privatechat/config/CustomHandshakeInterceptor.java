package com.lld.chat.privatechat.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {
  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    String query = request.getURI().getQuery();
    if (query != null) {
      String[] params = query.split("&");
      for (String param : params) {
        String[] keyValue = param.split("=");
        if (keyValue.length == 2 && keyValue[0].equals("username")) {
          attributes.put("username", keyValue[1]);
          break;
        }
      }
    }
    return true;
  }
  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
    // No operation
  }
}
