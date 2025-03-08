package com.lld.chat.privatechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //in memeory broker for broadcasting messages where destination starts with /topic and /queue
        registry.enableSimpleBroker("/topic", "/queue");

        //messages destination starts with /app will be routed to @messagemapping
        registry.setApplicationDestinationPrefixes("/app");

        //configure user destination for private message
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("**", "http://localhost:5173")
                .addInterceptors(new CustomHandshakeInterceptor())
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }
}
