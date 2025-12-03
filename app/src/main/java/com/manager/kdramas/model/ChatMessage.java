package com.manager.kdramas.model;

public class ChatMessage {
    public String userId;
    public String displayName;
    public String text;
    public long timestamp;

    public ChatMessage() {}
    public ChatMessage(String userId, String displayName, String text, long ts) {
        this.userId = userId;
        this.displayName = displayName;
        this.text = text;
        this.timestamp = ts;
    }
}

