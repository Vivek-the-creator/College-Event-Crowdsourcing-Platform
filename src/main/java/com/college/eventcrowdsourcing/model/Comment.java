package com.college.eventcrowdsourcing.model;

import java.time.LocalDateTime;

/**
 * Comment model class representing user comments on events
 * Users can comment on events to provide feedback and suggestions
 */
public class Comment {
    private int commentId;
    private int eventId;
    private int userId;
    private String comment;
    private LocalDateTime timestamp;
    
    // Additional fields for display
    private String userName;
    private String eventTitle;
    
    // Default constructor
    public Comment() {}
    
    // Constructor for new comments
    public Comment(int eventId, int userId, String comment) {
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }
    
    // Full constructor
    public Comment(int commentId, int eventId, int userId, String comment, LocalDateTime timestamp) {
        this.commentId = commentId;
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }
    
    public void setCommentId(int commentId) {
        this.commentId = commentId;
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
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
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
    
    /**
     * Check if comment is empty or null
     */
    public boolean isEmpty() {
        return comment == null || comment.trim().isEmpty();
    }
    
    /**
     * Get formatted timestamp string
     */
    public String getFormattedTimestamp() {
        return timestamp.toString().replace("T", " ");
    }
    
    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Comment comment1 = (Comment) obj;
        return commentId == comment1.commentId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(commentId);
    }
}


