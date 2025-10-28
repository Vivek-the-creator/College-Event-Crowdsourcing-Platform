package com.college.eventcrowdsourcing.database;

import com.college.eventcrowdsourcing.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DatabaseManager class handles all database operations using JDBC
 * Implements singleton pattern for connection management
 * Provides CRUD operations for all entities
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/college_events?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Vivek@2006"; // Your actual MySQL password
    
    // Private constructor for singleton
    private DatabaseManager() {
        initializeConnection();
    }
    
    /**
     * Get singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Initialize database connection
     */
    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully.");
            ensureFeedbackTable();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please check your dependencies.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database 'college_events' exists");
            System.err.println("3. User 'root' has correct password");
            System.err.println("4. Update DB_PASSWORD in DatabaseManager.java if needed");
            e.printStackTrace();
        }
    }

    /**
     * Ensure feedback table exists (id, event_id, user_id, rating, comment, created_at)
     */
    private void ensureFeedbackTable() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS feedback (
                feedback_id INT PRIMARY KEY AUTO_INCREMENT,
                event_id INT NOT NULL,
                user_id INT NOT NULL,
                rating INT NOT NULL,
                comment TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
            )
            """;
        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate(ddl);
        } catch (SQLException e) {
            System.err.println("Failed ensuring feedback table: " + e.getMessage());
        }
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
            initializeConnection();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    // ==================== USER OPERATIONS ====================
    
    /**
     * Create a new user
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, points) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().getValue());
            stmt.setInt(5, user.getPoints());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    /**
     * Update user points
     */
    public boolean updateUserPoints(int userId, int points) {
        String sql = "UPDATE users SET points = points + ? WHERE user_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user points: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY points DESC";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    // ==================== EVENT OPERATIONS ====================
    
    /**
     * Create a new event
     */
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (title, description, category, proposer_id, budget_estimate, target_funds, event_date, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getCategory());
            stmt.setInt(4, event.getProposerId());
            stmt.setDouble(5, event.getBudgetEstimate());
            stmt.setDouble(6, event.getTargetFunds());
            stmt.setDate(7, Date.valueOf(event.getEventDate()));
            stmt.setString(8, event.getLocation());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    event.setEventId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating event: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get all events with additional information
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT e.*, u.name as proposer_name,
                   (SELECT COUNT(*) FROM votes v WHERE v.event_id = e.event_id) as vote_count,
                   (SELECT COUNT(*) FROM comments c WHERE c.event_id = e.event_id) as comment_count
            FROM events e
            LEFT JOIN users u ON e.proposer_id = u.user_id
            ORDER BY e.created_at DESC
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all events: " + e.getMessage());
        }
        return events;
    }
    
    /**
     * Get events by status
     */
    public List<Event> getEventsByStatus(Event.EventStatus status) {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT e.*, u.name as proposer_name,
                   (SELECT COUNT(*) FROM votes v WHERE v.event_id = e.event_id) as vote_count,
                   (SELECT COUNT(*) FROM comments c WHERE c.event_id = e.event_id) as comment_count
            FROM events e
            LEFT JOIN users u ON e.proposer_id = u.user_id
            WHERE e.status = ?
            ORDER BY e.created_at DESC
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.getValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting events by status: " + e.getMessage());
        }
        return events;
    }
    
    /**
     * Update event status
     */
    public boolean updateEventStatus(int eventId, Event.EventStatus status) {
        String sql = "UPDATE events SET status = ? WHERE event_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.getValue());
            stmt.setInt(2, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating event status: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update event funding
     */
    public boolean updateEventFunding(int eventId, double totalFunds) {
        String sql = "UPDATE events SET total_funds = ? WHERE event_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, totalFunds);
            stmt.setInt(2, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating event funding: " + e.getMessage());
        }
        return false;
    }
    
    // ==================== VOTE OPERATIONS ====================
    
    /**
     * Add a vote for an event
     */
    public boolean addVote(int eventId, int userId) {
        String sql = "INSERT INTO votes (event_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding vote: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Check if user has voted for an event
     */
    public boolean hasUserVoted(int eventId, int userId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking vote: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Remove a vote
     */
    public boolean removeVote(int eventId, int userId) {
        String sql = "DELETE FROM votes WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing vote: " + e.getMessage());
        }
        return false;
    }
    
    // ==================== COMMENT OPERATIONS ====================
    
    /**
     * Add a comment to an event
     */
    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (event_id, user_id, comment) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, comment.getEventId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getComment());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    comment.setCommentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get comments for an event
     */
    public List<Comment> getCommentsForEvent(int eventId) {
        List<Comment> comments = new ArrayList<>();
        String sql = """
            SELECT c.*, u.name as user_name, e.title as event_title
            FROM comments c
            LEFT JOIN users u ON c.user_id = u.user_id
            LEFT JOIN events e ON c.event_id = e.event_id
            WHERE c.event_id = ?
            ORDER BY c.timestamp DESC
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments: " + e.getMessage());
        }
        return comments;
    }
    
    // ==================== CONTRIBUTION OPERATIONS ====================
    
    /**
     * Add a contribution
     */
    public boolean addContribution(Contribution contribution) {
        String sql = "INSERT INTO contributions (event_id, user_id, type, details, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, contribution.getEventId());
            stmt.setInt(2, contribution.getUserId());
            stmt.setString(3, contribution.getType().getValue());
            stmt.setString(4, contribution.getDetails());
            stmt.setDouble(5, contribution.getAmount());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    contribution.setContributionId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding contribution: " + e.getMessage());
        }
        return false;
    }
    
    // ==================== FEEDBACK OPERATIONS ====================

    /**
     * Add feedback (rating + optional comment) for an event by a user
     */
    public boolean addFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback (event_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feedback.getEventId());
            stmt.setInt(2, feedback.getUserId());
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComment());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) feedback.setFeedbackId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding feedback: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get feedback list for an event
     */
    public List<Feedback> getFeedbackForEvent(int eventId) {
        List<Feedback> list = new ArrayList<>();
        String sql = """
            SELECT f.*, u.name AS user_name
            FROM feedback f
            LEFT JOIN users u ON f.user_id = u.user_id
            WHERE f.event_id = ?
            ORDER BY f.created_at DESC
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting feedback: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get contributions for an event
     */
    public List<Contribution> getContributionsForEvent(int eventId) {
        List<Contribution> contributions = new ArrayList<>();
        String sql = """
            SELECT c.*, u.name as user_name, e.title as event_title
            FROM contributions c
            LEFT JOIN users u ON c.user_id = u.user_id
            LEFT JOIN events e ON c.event_id = e.event_id
            WHERE c.event_id = ?
            ORDER BY c.created_at DESC
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contributions.add(mapResultSetToContribution(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting contributions: " + e.getMessage());
        }
        return contributions;
    }
    
    /**
     * Get contributions for a user
     */
    public List<Contribution> getContributionsForUser(int userId) {
        List<Contribution> contributions = new ArrayList<>();
        String sql = """
            SELECT c.*, u.name as user_name, e.title as event_title
            FROM contributions c
            LEFT JOIN users u ON c.user_id = u.user_id
            LEFT JOIN events e ON c.event_id = e.event_id
            WHERE c.user_id = ?
            ORDER BY c.created_at DESC
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contributions.add(mapResultSetToContribution(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user contributions: " + e.getMessage());
        }
        return contributions;
    }
    
    /**
     * Update contribution status
     */
    public boolean updateContributionStatus(int contributionId, Contribution.ContributionStatus status) {
        String sql = "UPDATE contributions SET status = ? WHERE contribution_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.getValue());
            stmt.setInt(2, contributionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating contribution status: " + e.getMessage());
        }
        return false;
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(User.UserRole.fromString(rs.getString("role")));
        user.setPoints(rs.getInt("points"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    }
    
    /**
     * Map ResultSet to Event object
     */
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setCategory(rs.getString("category"));
        event.setStatus(Event.EventStatus.fromString(rs.getString("status")));
        event.setProposerId(rs.getInt("proposer_id"));
        event.setBudgetEstimate(rs.getDouble("budget_estimate"));
        event.setTotalFunds(rs.getDouble("total_funds"));
        event.setTargetFunds(rs.getDouble("target_funds"));
        
        Date eventDate = rs.getDate("event_date");
        if (eventDate != null) {
            event.setEventDate(eventDate.toLocalDate());
        }
        
        event.setLocation(rs.getString("location"));
        event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Additional fields
        event.setProposerName(rs.getString("proposer_name"));
        event.setVoteCount(rs.getInt("vote_count"));
        event.setCommentCount(rs.getInt("comment_count"));
        
        return event;
    }
    
    /**
     * Map ResultSet to Comment object
     */
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setEventId(rs.getInt("event_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setComment(rs.getString("comment"));
        comment.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        comment.setUserName(rs.getString("user_name"));
        comment.setEventTitle(rs.getString("event_title"));
        return comment;
    }
    
    /**
     * Map ResultSet to Contribution object
     */
    private Contribution mapResultSetToContribution(ResultSet rs) throws SQLException {
        Contribution contribution = new Contribution();
        contribution.setContributionId(rs.getInt("contribution_id"));
        contribution.setEventId(rs.getInt("event_id"));
        contribution.setUserId(rs.getInt("user_id"));
        contribution.setType(Contribution.ContributionType.fromString(rs.getString("type")));
        contribution.setDetails(rs.getString("details"));
        contribution.setAmount(rs.getDouble("amount"));
        contribution.setStatus(Contribution.ContributionStatus.fromString(rs.getString("status")));
        contribution.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        contribution.setUserName(rs.getString("user_name"));
        contribution.setEventTitle(rs.getString("event_title"));
        return contribution;
    }

    /**
     * Map ResultSet to Feedback object
     */
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(rs.getInt("feedback_id"));
        feedback.setEventId(rs.getInt("event_id"));
        feedback.setUserId(rs.getInt("user_id"));
        feedback.setRating(rs.getInt("rating"));
        feedback.setComment(rs.getString("comment"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) feedback.setCreatedAt(ts.toLocalDateTime());
        try {
            feedback.setUserName(rs.getString("user_name"));
        } catch (SQLException ignored) {}
        return feedback;
    }
}

