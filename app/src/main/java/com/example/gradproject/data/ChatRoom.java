package com.example.gradproject.data;

public class ChatRoom
{
    private String chatRoomId;
    private String chatRoomRecevierName;
    private String recevierId;
    private boolean isSeen;
    private long timestamp;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, String chatRoomRecevierName, String recevierId, boolean isSeen, long timestamp) {
        this.chatRoomId = chatRoomId;
        this.chatRoomRecevierName = chatRoomRecevierName;
        this.recevierId = recevierId;
        this.isSeen = isSeen;
        this.timestamp = timestamp;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getChatRoomRecevierName() {
        return chatRoomRecevierName;
    }

    public String getRecevierId() {
        return recevierId;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
