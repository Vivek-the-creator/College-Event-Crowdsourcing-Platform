package com.college.eventcrowdsourcing.model;

import java.time.LocalDateTime;

/**
 * Contribution model class representing user contributions to events
 * Includes skills, resources, and funding contributions
 */
public class Contribution {
    private int contributionId;
    private int eventId;
    private int userId;
    private ContributionType type;
    private String details;
    private double amount;
    private ContributionStatus status;
    private LocalDateTime createdAt;
    
    // Additional fields for display
    private String userName;
    private String eventTitle;
    
    /**
     * Enum for contribution types
     */
    public enum ContributionType {
        SKILL("skill"),
        RESOURCE("resource"),
        FUND("fund");
        
        private final String value;
        
        ContributionType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static ContributionType fromString(String type) {
            for (ContributionType t : ContributionType.values()) {
                if (t.value.equalsIgnoreCase(type)) {
                    return t;
                }
            }
            return SKILL; // Default type
        }
    }
    
    /**
     * Enum for contribution status
     */
    public enum ContributionStatus {
        PENDING("pending"),
        APPROVED("approved"),
        REJECTED("rejected");
        
        private final String value;
        
        ContributionStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static ContributionStatus fromString(String status) {
            for (ContributionStatus s : ContributionStatus.values()) {
                if (s.value.equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return PENDING; // Default status
        }
    }
    
    // Default constructor
    public Contribution() {}
    
    // Constructor for new contributions
    public Contribution(int eventId, int userId, ContributionType type, String details, double amount) {
        this.eventId = eventId;
        this.userId = userId;
        this.type = type;
        this.details = details;
        this.amount = amount;
        this.status = ContributionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Contribution(int contributionId, int eventId, int userId, ContributionType type, 
                       String details, double amount, ContributionStatus status, LocalDateTime createdAt) {
        this.contributionId = contributionId;
        this.eventId = eventId;
        this.userId = userId;
        this.type = type;
        this.details = details;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getContributionId() {
        return contributionId;
    }
    
    public void setContributionId(int contributionId) {
        this.contributionId = contributionId;
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
    
    public ContributionType getType() {
        return type;
    }
    
    public void setType(ContributionType type) {
        this.type = type;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public ContributionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ContributionStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
     * Check if contribution is a funding contribution
     */
    public boolean isFunding() {
        return type == ContributionType.FUND;
    }
    
    /**
     * Check if contribution is a skill contribution
     */
    public boolean isSkill() {
        return type == ContributionType.SKILL;
    }
    
    /**
     * Check if contribution is a resource contribution
     */
    public boolean isResource() {
        return type == ContributionType.RESOURCE;
    }
    
    /**
     * Check if contribution is approved
     */
    public boolean isApproved() {
        return status == ContributionStatus.APPROVED;
    }
    
    /**
     * Check if contribution is pending
     */
    public boolean isPending() {
        return status == ContributionStatus.PENDING;
    }
    
    /**
     * Check if contribution is rejected
     */
    public boolean isRejected() {
        return status == ContributionStatus.REJECTED;
    }
    
    @Override
    public String toString() {
        return "Contribution{" +
                "contributionId=" + contributionId +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", type=" + type +
                ", details='" + details + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contribution that = (Contribution) obj;
        return contributionId == that.contributionId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(contributionId);
    }
}


