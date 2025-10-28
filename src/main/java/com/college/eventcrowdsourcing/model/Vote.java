package com.college.eventcrowdsourcing.model;

import java.time.LocalDateTime;

/**
 * Vote model class representing user votes on events
 * Each user can vote once per event
 */
public class Vote {
    private int voteId;
    private int eventId;
    private int userId;
    private LocalDateTime timestamp;
    
    // Additional fields for display
    private String userName;
    private String eventTitle;
    
    // Default constructor
    public Vote() {}
    
    // Constructor for new votes
    public Vote(int eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Full constructor
    public Vote(int voteId, int eventId, int userId, LocalDateTime timestamp) {
        this.voteId = voteId;
        this.eventId = eventId;
        this.userId = userId;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public int getVoteId() {
        return voteId;
    }
    
    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }
    
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getEventTitle() {
        return eventTitle;
    }
    
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    
    @Override
    public String toString() {
        return "Vote{" +
                "voteId=" + voteId +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vote vote = (Vote) obj;
        return voteId == vote.voteId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(voteId);
    }
}


