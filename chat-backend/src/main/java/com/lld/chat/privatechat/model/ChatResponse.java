package com.lld.chat.privatechat.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatResponse {

    private String from;
    private String content;
    private String timestamp;

    public ChatResponse(String from, String content, String timestamp) {
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
