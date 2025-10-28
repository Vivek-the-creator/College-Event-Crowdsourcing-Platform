package com.college.eventcrowdsourcing.model;

import java.time.LocalDateTime;

/**
 * User model class representing users in the system
 * Includes students, faculty, and administrators
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private int points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Enum for user roles in the system
     */
    public enum UserRole {
        STUDENT("student"),
        FACULTY("faculty"),
        ADMIN("admin");
        
        private final String value;
        
        UserRole(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static UserRole fromString(String role) {
            for (UserRole r : UserRole.values()) {
                if (r.value.equalsIgnoreCase(role)) {
                    return r;
                }
            }
            return STUDENT; // Default role
        }
    }
    
    // Default constructor
    public User() {}
    
    // Constructor with basic information
    public User(String name, String email, String passwordHash, UserRole role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.points = 0;
    }
    
    // Full constructor
    public User(int userId, String name, String email, String passwordHash, 
                UserRole role, int points, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.points = points;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public void addPoints(int points) {
        this.points += points;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    /**
     * Check if user is faculty
     */
    public boolean isFaculty() {
        return role == UserRole.FACULTY;
    }
    
    /**
     * Check if user is a student
     */
    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", points=" + points +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
}


