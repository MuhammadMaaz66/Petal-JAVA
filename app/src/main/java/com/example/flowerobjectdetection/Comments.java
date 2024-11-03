package com.example.flowerobjectdetection;

public class Comments {

    private String userId;
    private String text;
    private long timestamp;

    // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    public Comments() {}

    // Constructor
    public Comments(String userId, String text, long timestamp) {
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
