package com.example.gradproject.data;

public class Message
{
    private String messageId;
    private String message;
    private boolean isSeen;
    private String type;
    private long time;
    private String from;

    public Message() {
    }

    public Message(String messageId, String message, boolean isSeen, String type, long time, String from) {
        this.messageId = messageId;
        this.message = message;
        this.isSeen = isSeen;
        this.type = type;
        this.time = time;
        this.from = from;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }
}
