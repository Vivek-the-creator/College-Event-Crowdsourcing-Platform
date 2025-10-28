package com.college.eventcrowdsourcing.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event model class representing event proposals and information
 * Contains all details about college events including status, funding, etc.
 */
public class Event {
    private int eventId;
    private String title;
    private String description;
    private String category;
    private EventStatus status;
    private int proposerId;
    private double budgetEstimate;
    private double totalFunds;
    private double targetFunds;
    private LocalDate eventDate;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String proposerName;
    private int voteCount;
    private int commentCount;
    private List<String> organizers;
    
    /**
     * Enum for event status
     */
    public enum EventStatus {
        PROPOSED("proposed"),
        APPROVED("approved"),
        FUNDED("funded"),
        SCHEDULED("scheduled"),
        COMPLETED("completed"),
        CANCELLED("cancelled");
        
        private final String value;
        
        EventStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static EventStatus fromString(String status) {
            for (EventStatus s : EventStatus.values()) {
                if (s.value.equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return PROPOSED; // Default status
        }
    }
    
    // Default constructor
    public Event() {}
    
    // Constructor for new event proposals
    public Event(String title, String description, String category, int proposerId, 
                 double budgetEstimate, double targetFunds, LocalDate eventDate, String location) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.proposerId = proposerId;
        this.budgetEstimate = budgetEstimate;
        this.targetFunds = targetFunds;
        this.eventDate = eventDate;
        this.location = location;
        this.status = EventStatus.PROPOSED;
        this.totalFunds = 0.0;
    }
    
    // Full constructor
    public Event(int eventId, String title, String description, String category, 
                 EventStatus status, int proposerId, double budgetEstimate, 
                 double totalFunds, double targetFunds, LocalDate eventDate, 
                 String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.proposerId = proposerId;
        this.budgetEstimate = budgetEstimate;
        this.totalFunds = totalFunds;
        this.targetFunds = targetFunds;
        this.eventDate = eventDate;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public EventStatus getStatus() {
        return status;
    }
    
    public void setStatus(EventStatus status) {
        this.status = status;
    }
    
    public int getProposerId() {
        return proposerId;
    }
    
    public void setProposerId(int proposerId) {
        this.proposerId = proposerId;
    }
    
    public double getBudgetEstimate() {
        return budgetEstimate;
    }
    
    public void setBudgetEstimate(double budgetEstimate) {
        this.budgetEstimate = budgetEstimate;
    }
    
    public double getTotalFunds() {
        return totalFunds;
    }
    
    public void setTotalFunds(double totalFunds) {
        this.totalFunds = totalFunds;
    }
    
    public double getTargetFunds() {
        return targetFunds;
    }
    
    public void setTargetFunds(double targetFunds) {
        this.targetFunds = targetFunds;
    }
    
    public LocalDate getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public String getProposerName() {
        return proposerName;
    }
    
    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }
    
    public int getVoteCount() {
        return voteCount;
    }
    
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    
    public int getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    public List<String> getOrganizers() {
        return organizers;
    }
    
    public void setOrganizers(List<String> organizers) {
        this.organizers = organizers;
    }
    
    /**
     * Check if event is fully funded
     */
    public boolean isFullyFunded() {
        return totalFunds >= targetFunds;
    }
    
    /**
     * Get funding percentage
     */
    public double getFundingPercentage() {
        if (targetFunds == 0) return 0;
        return (totalFunds / targetFunds) * 100;
    }
    
    /**
     * Check if event can be approved
     */
    public boolean canBeApproved() {
        return status == EventStatus.PROPOSED;
    }
    
    /**
     * Check if event can be funded
     */
    public boolean canBeFunded() {
        return status == EventStatus.APPROVED;
    }
    
    /**
     * Check if event can be scheduled
     */
    public boolean canBeScheduled() {
        return status == EventStatus.FUNDED;
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", proposerId=" + proposerId +
                ", budgetEstimate=" + budgetEstimate +
                ", totalFunds=" + totalFunds +
                ", targetFunds=" + targetFunds +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event event = (Event) obj;
        return eventId == event.eventId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(eventId);
    }
}


